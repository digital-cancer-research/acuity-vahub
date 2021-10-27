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

import {ITrackModel} from './ITrackModel';
import {ITrack, ITrackDataPoint} from '../store/ITimeline';
import {PlotOptions, RawEvent} from '../chart/IChartEvent';
import {IChartEventService} from '../chart/IChartEventService';
import {ITrackPlotDetail} from './ITrackPlotDetail';
import {getMarkerHeight, getMarkerWidth, getUrlWrappedMarkerSymbol} from '../chart/TimelineUtils';
import * as  _ from 'lodash';

export abstract class AbstractTrackModel implements ITrackModel {

    protected categories: string[] = [];

    constructor(protected chartPlotEventService: IChartEventService) {
    }

    abstract canExpand(): boolean;

    abstract canCollapse(): boolean;

    abstract getTrackName(): string;

    createTrackPlotDetail(subjectId: string, track: ITrack): ITrackPlotDetail[] {
        const plotDetails: ITrackPlotDetail[] = [];

        const groupedPlotData = this.createPlotData(track);

        groupedPlotData.forEach(gpd => {
            plotDetails.push({
                trackName: this.getTrackName(),
                subTrackName: gpd.group,
                plotConfig: this.createPlotConfig(subjectId),
                plotData: gpd.plotData
            });
        });

        return plotDetails;
    }

    protected abstract createPlotConfig(subjectId: string): any;

    protected createPlotData(track: ITrack): any {
        if (!track.data) {
            return [];
        }

        const that = this;
        const data: RawEvent[] = track.data.reduce((rawEvents: RawEvent[], trackEvent: ITrackDataPoint) => {
            rawEvents.push(trackEvent.end ? this.transformDurationalDataPoint(trackEvent)
                : this.transformNoneDurationalDataPoint(trackEvent));
            return rawEvents;
        }, new Array<RawEvent>());

        // group and transform data by given field

        return _.chain(data)
            .groupBy('group')
            .toPairs()
            .map(currentItem => {
                const formattedItem = [currentItem[0],
                    that.chartPlotEventService.createPlotDataSeries(<RawEvent[]>currentItem[1], that.categories)];
                return _.zipObject(['group', 'plotData'], formattedItem);
            })
            .sortBy('group')
            .value();
    }

    protected transformDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        return {
            group: null,
            type: dataPoint.metadata.type,
            start: {
                date: dataPoint.start.date,
                dayHour: dataPoint.start.dayHour,
                dayHourAsString: dataPoint.start.dayHourAsString,
                studyDayHourAsString: dataPoint.start.studyDayHourAsString,
                doseDayHour: dataPoint.start.doseDayHour
            },
            end: {
                date: dataPoint.end.date,
                dayHour: dataPoint.end.dayHour,
                dayHourAsString: dataPoint.end.dayHourAsString,
                studyDayHourAsString: dataPoint.end.studyDayHourAsString,
                doseDayHour: dataPoint.end.doseDayHour
            },
            plotOptions: {},
            metadata: {}
        };
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        return {
            group: null,
            type: dataPoint.metadata.type,
            start: {
                date: dataPoint.start.date,
                dayHour: dataPoint.start.dayHour,
                dayHourAsString: dataPoint.start.dayHourAsString,
                studyDayHourAsString: dataPoint.start.studyDayHourAsString,
                doseDayHour: dataPoint.start.doseDayHour
            },
            plotOptions: <PlotOptions> {
                markerSymbol: getUrlWrappedMarkerSymbol(dataPoint.metadata.type),
                height: getMarkerHeight(dataPoint.metadata.type),
                width: getMarkerWidth(dataPoint.metadata.type)
            },
            metadata: {}
        };
    }
}
