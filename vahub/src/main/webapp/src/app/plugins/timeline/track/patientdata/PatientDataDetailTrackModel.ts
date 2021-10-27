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
import {ITrack, ITrackDataPoint} from '../../store/ITimeline';
import {PlotExtreme, RawEvent} from '../../chart/IChartEvent';
import {LineChartEventService} from '../../chart/line/LineChartEventService';
import {PatientDataTrackUtils} from './PatientDataTrackUtils';
import {ITrackPlotDetail} from '../ITrackPlotDetail';
import {AbstractPatientDataTrackModel} from './AbstractPatientDataTrackModel';
import * as _ from 'lodash';

@Injectable()
export class PatientDataDetailTrackModel extends AbstractPatientDataTrackModel {
    constructor(protected lineChartPlotEventService: LineChartEventService) {
        super(lineChartPlotEventService);
    }

    canExpand(): boolean {
        return false;
    }

    canCollapse(): boolean {
        return true;
    }

    createTrackPlotDetail(subjectId: string, track: ITrack): ITrackPlotDetail[] {
        const plotDetails: ITrackPlotDetail[] = super.createTrackPlotDetail(subjectId, track);
        plotDetails.forEach(plotDetail => {
            const extremes = this.createExtremes(plotDetail.plotData[0]);
            plotDetail.extremes = extremes.extremes;
        });
        return plotDetails;
    }

    protected createPlotConfig(subjectId: string): any {
        const plotConfig: any = {};
        plotConfig.id = {
            subject: subjectId,
            track: PatientDataTrackUtils.PATIENTDATA_TRACK_NAME,
            level: PatientDataTrackUtils.LINECHART_LEVEL_TRACK_EXPANSION_LEVEL
        };

        return plotConfig;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        return {
            group: dataPoint.metadata.testName,
            plotOptions: {},
            type: dataPoint.metadata.type,
            start: {
                date: dataPoint.start.date,
                dayHour: dataPoint.start.dayHour,
                dayHourAsString: dataPoint.start.dayHourAsString,
                studyDayHourAsString: dataPoint.start.studyDayHourAsString,
                doseDayHour: dataPoint.start.doseDayHour
            },
            metadata: {
                tooltip: this.createTooltip(dataPoint),
                testName: dataPoint.metadata.testName,
                unit: dataPoint.metadata.unit,
                value: dataPoint.metadata.value,
            }
        };
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = `Test name: <b>${dataPoint.metadata.testName}</b><br>`;
        tooltip += `Raw value: <b>${dataPoint.metadata.value}</b><br>`;
        tooltip += `Unit: <b>${dataPoint.metadata.unit}</b>`;
        return tooltip;
    }

    private createExtremes(sereies: any): any {
        const result: any = {};
        const sereiesData: any[] = sereies.data;

        if (sereiesData.length > 0) {

            const minEventValue: any = _.minBy(sereiesData, event => event.metadata.value).metadata.value;
            const maxEventValue: any = _.maxBy(sereiesData, event => event.metadata.value).metadata.value;

            const defaultDiff: number = _.mean([minEventValue, maxEventValue]) * 0.5;
            const chartMax: number = maxEventValue + defaultDiff;
            const chartMin: number = minEventValue - defaultDiff;

            result.extremes = <PlotExtreme>{
                min: chartMin,
                max: chartMax
            };
        }
        return result;
    }
}
