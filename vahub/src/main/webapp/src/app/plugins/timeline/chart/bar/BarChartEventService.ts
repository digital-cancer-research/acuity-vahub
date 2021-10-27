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
import {BarChartSeriesOptions, RawEvent} from '../IChartEvent';
import {IChartEventService} from '../IChartEventService';
import {formatDayHourString} from '../TimelineUtils';
import {StudyService} from '../../../../common/StudyService';
import {SessionEventService} from '../../../../session/event/SessionEventService';

@Injectable()
export class BarChartEventService implements IChartEventService {
    constructor(private sessionEventService: SessionEventService) {

    }

    createPlotDataSeries(data: RawEvent[], categories: string[]): any[] {
        const series: BarChartSeriesOptions[] = [
            {
                name: 'eventsWithDuration',
                type: 'columnrange',
                data: []
            },
            {
                name: 'eventsWithoutDuration',
                type: 'scatter',
                data: []
            }
        ];

        const dataWithDuration: any[] = [];
        const dataWithoutDuration: any[] = [];

        // Iterate through data and assign correctly formatted objects to the correct series.
        data.forEach((event: RawEvent, index: number) => {
            if (event.end) {
                dataWithDuration.push(this.parseEventWithDuration(event, categories, index));
            } else {
                dataWithoutDuration.push(this.parseEventWithoutDuration(event, categories, index));
            }
        });

        // sort series data by ascending points on y axis in order to display later point above previous one
        dataWithoutDuration.sort((pointA, pointB) => pointA.y - pointB.y);

        series[0].data = dataWithDuration;
        series[1].data = dataWithoutDuration;

        return series;
    }

    private parseEventWithDuration(eventWithDuration: RawEvent, categories: string[], id: number): any {
        return {
            id: id.toString(),
            x: categories.indexOf(eventWithDuration.group),
            low: eventWithDuration.start.dayHour,
            studyLow: eventWithDuration.start.studyDayHourAsString,
            high: eventWithDuration.end.dayHour,
            studyHigh: formatDayHourString(eventWithDuration.end.studyDayHourAsString, eventWithDuration.end.doseDayHour, this.isOngoing()),
            lowAsString: eventWithDuration.start.dayHourAsString,
            highAsString: formatDayHourString(eventWithDuration.end.dayHourAsString, eventWithDuration.end.dayHour, this.isOngoing()),
            color: eventWithDuration.plotOptions.color,
            tooltip: eventWithDuration.metadata['tooltip'],
            duration: eventWithDuration.metadata['duration'],
            eventType: eventWithDuration.type,
            ongoing: eventWithDuration.metadata.ongoing
        };
    }

    private parseEventWithoutDuration(eventWithoutDuration: RawEvent, categories: string[], id: number): any {
        return {
            id: id.toString(),
            x: categories.indexOf(eventWithoutDuration.group),
            y: eventWithoutDuration.start.dayHour,
            //Probably this TimelineUtils.formatDayHourString due to Summary track having icons as non-duration events
            yAsString: eventWithoutDuration.start.dayHourAsString,
            studyY: eventWithoutDuration.start.studyDayHourAsString,
            tooltip: eventWithoutDuration.metadata['tooltip'],
            eventType: eventWithoutDuration.type,
            marker: {
                symbol: eventWithoutDuration.plotOptions.markerSymbol,
                fillColor: eventWithoutDuration.plotOptions.fillColor,
                height: eventWithoutDuration.plotOptions.height,
                width: eventWithoutDuration.plotOptions.width
            }
        };
    }

    public isOngoing(): boolean {
        return StudyService.isOngoingStudy(this.sessionEventService.currentSelectedDatasets[0].type);
    }
}
