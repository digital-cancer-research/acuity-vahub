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
import {uniq} from 'lodash';
import {ITrackDataPoint} from '../../store/ITimeline';
import {PlotOptions, RawEvent} from '../../chart/IChartEvent';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {PatientDataTrackUtils} from './PatientDataTrackUtils';
import {AbstractPatientDataTrackModel} from './AbstractPatientDataTrackModel';
import {getMarkerHeight, getMarkerWidth} from '../../chart/TimelineUtils';

@Injectable()
export class PatientDataSummaryTrackModel extends AbstractPatientDataTrackModel {

    constructor(protected barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
        this.categories.push(PatientDataTrackUtils.PATIENTDATA_SUMMARY_SUB_TRACK_NAME);
    }

    canExpand(): boolean {
        return true;
    }

    canCollapse(): boolean {
        return false;
    }

    protected createPlotConfig(subjectId: string): any {
        const plotConfig: any = {};
        plotConfig.id = {
            subject: subjectId,
            track: PatientDataTrackUtils.PATIENTDATA_TRACK_NAME,
            level: PatientDataTrackUtils.TIMELINE_LEVEL_TRACK_EXPANSION_LEVEL,
        };
        return plotConfig;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        return {
            group: PatientDataTrackUtils.PATIENTDATA_SUMMARY_SUB_TRACK_NAME,
            type: dataPoint.metadata.type,
            start: {
                date: dataPoint.start.date,
                dayHour: dataPoint.start.dayHour,
                dayHourAsString: dataPoint.start.dayHourAsString,
                studyDayHourAsString: dataPoint.start.studyDayHourAsString,
                doseDayHour: dataPoint.start.doseDayHour
            },
            plotOptions: <PlotOptions> {
                markerSymbol: 'square',
                height: getMarkerHeight(dataPoint.metadata.type),
                width: getMarkerWidth(dataPoint.metadata.type),
                fillColor: PatientDataTrackUtils.assignColour(dataPoint.metadata.numberOfEvents,
                    PatientDataTrackUtils.TIMELINE_LEVEL_TRACK_EXPANSION_LEVEL)
            },
            metadata: {
                tooltip: this.createTooltip(dataPoint),
                numberOfEvents: dataPoint.metadata.numberOfEvents,
                details: dataPoint.metadata.details
            }
        };
    }

    protected createTooltip(dataPoint: ITrackDataPoint): string {
        const eventNames = uniq(dataPoint.metadata.details.map(detail => detail.measurementName)).sort();
        let tooltip = `Number of events: <b>${dataPoint.metadata.numberOfEvents}</b><br>`;
        tooltip += 'Included events:<br>';
        tooltip += eventNames.map(name => `<b>${name}</b>`).join('<br>');
        return tooltip;
    }
}
