/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Injectable} from '@angular/core';
import {IChartEventService} from './../../chart/IChartEventService';
import {RawEvent} from './../../chart/IChartEvent';
import {formatDayHourString, getUrlWrappedMarkerSymbol, TimelineUtils} from '../../chart/TimelineUtils';
import {EventDateType} from '../../chart/IChartEvent';
import {DoseTrackUtils} from './DoseTrackUtils';
import {SessionEventService} from '../../../../session/event/SessionEventService';
import {StudyService} from '../../../../common/StudyService';

/**
 * Service transforms data from events to series for plots
 */
@Injectable()
export class DoseTrackSteppedLineChartEventService implements IChartEventService {

    public static DEFAULT_MARKER = 'circle';
    public static DEFAULT_MARKER_HEIGHT = 10;
    public static DEFAULT_MARKER_WIDTH = 20;

    constructor(private sessionEventService: SessionEventService) {

    }

    /**
     * @param {RawEvent[]} data - event data
     * @param {string[]} categories
     * @returns {[{}]} series - series for plots
     */
    createPlotDataSeries(data: RawEvent[], categories: string[]): any[] {
        const series: any[] = [
            {
                name: 'dosingEvents',
                color: DoseTrackUtils.DRUG_DETAIL_COLOUR,
                marker: {
                    enabled: true,
                    states: {
                        hover: {
                            radiusPlus: 2
                        },
                        select: {
                            fillColor: 'red'
                        }
                    }
                },
                data: [],

                // zones are used to change the colour of segments of the line (i.e. when an event is selected).
                zoneAxis: 'x',
                zones: [],
                step: 'right'
            }
        ];
        // Iterate through data and assign correctly formatted objects to the correct series.
        series[0].data = this.createSeriesData(data);

        return series;
    }

    /**
     * Handles data for series
     * @param {RawEvent[]} events - data from events
     * @returns {any[]} seriesData
     */
    private createSeriesData(events: RawEvent[]): any[] {
        const seriesData: any[] = [];

        events.forEach((event: RawEvent, index: number) => {
            if (event.metadata.special) {
                if (event.metadata.ongoing) {
                    seriesData.push(this.parseOngoingEvent(event, events[index - 1]));
                }
            } else if (event.metadata.drugDoses.length > 0) {
                this.parsePoints(event, index, events).forEach(point => seriesData.push(point));
            }
        });

        return seriesData;
    }

    /**
     * Gets information about points on the plot for every event. Dose goes by y, day hours go by x.
     * @param {RawEvent} event - currentEvent
     * @param {number} index - index of event
     * @param {RawEvent[]} events - all events
     * @returns {[]} points
     */
    private parsePoints(event: RawEvent, index: number, events: RawEvent[]): any[] {
        const points = [];
        const firstPoint = this.parseEventWithDurationFirstPoint(event);
        const secondPoint = this.parseEventWithDurationSecondPoint(event);
        const thirdPoint = this.parseEventWithDurationThirdPoint(event);

        // otherwise, if second point has 0 by y, it would be duplicated by the first one
        if (secondPoint.y !== 0) {
            points.push(firstPoint);
        }
        points.push(secondPoint);
        points.push(thirdPoint);

        const ended = !event.metadata.ongoing && (index === events.length - 1);
        const nextIsDiscontinued = event.metadata.subsequentPeriodType === 'DISCONTINUED';

        if (ended || nextIsDiscontinued) {
            // if last point is not 0 on y, adding the fourth point with 0 by y
            if (thirdPoint.y !== 0) {
                points.push(this.parseEventWithDurationFourthPoint(event));
            }
            points.splice(-1, 1); // remove last point as it would be overflowed by disc point
            points.push(this.parseDiscontinuationEvent(event)); // add discontinuation event instead of the last one
            points.push(null); // added to make the end of the line
        } else if (index < events.length - 1 && !events[index + 1].metadata.ongoing) {
            points.push(this.parseEventWithDurationFourthPoint(event));
        }

        return points;
    }

    private parseEventWithDurationFirstPoint(eventWithDuration: RawEvent): any {
        return {
            x: eventWithDuration.start.dayHour,
            y: 0,
            marker: {
                enabled: false,
                states: {
                    hover: {
                        enabled: false
                    }
                }
            }
        };
    }

    private parseEventWithDurationSecondPoint(eventWithDuration: RawEvent): any {
        return {
            x: eventWithDuration.start.dayHour,
            y: eventWithDuration.metadata.drugDoses[0].dose,
            tooltip: this.createDoseEventTooltip(eventWithDuration),
            marker: {
                symbol: DoseTrackSteppedLineChartEventService.DEFAULT_MARKER,
                enabled: false
            }
        };
    }

    private parseEventWithDurationThirdPoint(eventWithDuration: RawEvent): any {
        return {
            x: eventWithDuration.end.dayHour,
            y: eventWithDuration.metadata.drugDoses[0].dose,
            tooltip: this.createDoseEventTooltip(eventWithDuration),
            marker: {
                symbol: DoseTrackSteppedLineChartEventService.DEFAULT_MARKER,
                enabled: false
            }
        };
    }

    private parseEventWithDurationFourthPoint(eventWithDuration: RawEvent): any {
        return {
            x: eventWithDuration.end.dayHour,
            y: 0,
            marker: {
                enabled: false,
                states: {
                    hover: {
                        enabled: false
                    }
                }
            }
        };
    }

    private createDoseEventTooltip(eventWithDuration: RawEvent): string {
        const {dose, doseUnit, frequency} = eventWithDuration.metadata.drugDoses[0];
        let value = '' + dose;
        if (value.includes('.') && value.length > 6) {
            // this causes a 3 dp digit to be calculated, without displaying three dp of 0.
            // e.g. 84.00000000000001 -> '84', and 0.683252 -> '0.683'.
            value = '' + (Math.round(dose * 1000) / 1000);
        }

        const formattedEndDate = formatDayHourString(eventWithDuration.end.dayHourAsString,
            eventWithDuration.end.dayHour, this.isOngoing());
        const formattedStudyEndDate = formatDayHourString(eventWithDuration.end.studyDayHourAsString,
            eventWithDuration.end.doseDayHour, eventWithDuration.metadata.ongoing);

        const tooltip = '<br/>Event: <b>Dose Event' +
            `</b><br/>Dose: <b>${value} ${doseUnit ? doseUnit : ''} ${frequency.name ? frequency.name : ''}</b><br/>` +
            `Start: <b>${eventWithDuration.start.dayHourAsString}</b><br/>` +
            `Study Day Start: <b>${eventWithDuration.start.studyDayHourAsString}</b><br/> ` +
            `End: <b>${formattedEndDate}</b><br/>` +
            `Study Day End: <b> ${formattedStudyEndDate}</b> <br/>Ongoing: <b>${eventWithDuration.metadata.ongoing}</b>`;

        return tooltip;
    }

    private parseOngoingEvent(eventWithoutDuration: RawEvent, previousEvent: RawEvent): any {
        return {
            x: eventWithoutDuration.start.dayHour,
            y: previousEvent.metadata.drugDoses[0].dose,
            yAsString: eventWithoutDuration.start.dayHourAsString,
            tooltip: this.createOngoingDoseTooltip(eventWithoutDuration),
            eventType: eventWithoutDuration.type,
            marker: {
                symbol: getUrlWrappedMarkerSymbol(EventDateType.STEP3_ONGOING),
                height: TimelineUtils.STEP3_MARKER_SYMBOL_HEIGHT,
                width: TimelineUtils.STEP3_MARKER_SYMBOL_WIDTH
            }
        };
    }

    private createOngoingDoseTooltip(eventWithoutDuration: RawEvent): string {
        return `<br/><b>Ongoing</b> event at day <b>${eventWithoutDuration.start.dayHourAsString}</b>`
            + `<br/></b> Study day: <b>${eventWithoutDuration.start.studyDayHourAsString}</b>`;
    }

    private parseDiscontinuationEvent(eventWithoutDuration: RawEvent): any {
        return {
            x: eventWithoutDuration.end.dayHour,
            y: 0,
            yAsString: eventWithoutDuration.start.dayHourAsString,
            tooltip: this.createDiscontinuedDoseTooltip(eventWithoutDuration),
            eventType: eventWithoutDuration.type,
            marker: {
                symbol: DoseTrackSteppedLineChartEventService.DEFAULT_MARKER,
                height: DoseTrackSteppedLineChartEventService.DEFAULT_MARKER_HEIGHT,
                width: DoseTrackSteppedLineChartEventService.DEFAULT_MARKER_WIDTH
            }
        };
    }

    private createDiscontinuedDoseTooltip(eventWithoutDuration: RawEvent): string {
        const dosingName = eventWithoutDuration.metadata.ongoing ? 'Ongoing inactive dosing' : 'Discontinued';
        return `<br/><b>${dosingName}</b> at <b>${eventWithoutDuration.end.dayHourAsString}</b>`
            + `<br/><b>${dosingName}</b> at <b> study day ${eventWithoutDuration.end.studyDayHourAsString}</b>`;
    }

    public isOngoing(): boolean {
        return StudyService.isOngoingStudy(this.sessionEventService.currentSelectedDatasets[0].type);
    }

}
