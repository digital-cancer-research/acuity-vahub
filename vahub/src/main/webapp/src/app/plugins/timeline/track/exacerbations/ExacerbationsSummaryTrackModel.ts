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
import {ITrackDataPoint} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {ExacerbationsTrackUtils} from './ExacerbationsTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';
import * as _ from 'lodash';

@Injectable()
export class ExacerbationsSummaryTrackModel extends AbstractTrackModel {

    constructor(protected barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
        this.categories.push(ExacerbationsTrackUtils.EXACERBATIONS_SUMMARY_SUB_TRACK_NAME);
    }

    canExpand(): boolean {
        return false;
    }

    canCollapse(): boolean {
        return false;
    }

    getTrackName(): string {
        return ExacerbationsTrackUtils.EXACERBATIONS_TRACK_NAME;
    }

    protected createPlotConfig(subjectId: string): any {
        const plotConfig: any = {};
        plotConfig.id = {
            subject: subjectId,
            track: ExacerbationsTrackUtils.EXACERBATIONS_TRACK_NAME,
            level: ExacerbationsTrackUtils.EXACERBATIONS_SUMMARY_TRACK_EXPANSION_LEVEL,
        };

        return plotConfig;
    }

    protected transformDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformDurationalDataPoint(dataPoint);
        event.group = ExacerbationsTrackUtils.EXACERBATIONS_SUMMARY_SUB_TRACK_NAME;
        event.plotOptions.color = ExacerbationsTrackUtils.assignColour(dataPoint.metadata.severityGrade, !dataPoint.metadata.imputedEndDate);
        event.metadata = {
            ongoing: dataPoint.metadata.ongoing,
            imputedEndDate: dataPoint.metadata.imputedEndDate,
            duration: dataPoint.metadata.duration,
            numberOfDoseReceived: dataPoint.metadata.numberOfDoseReceived,
            comorbidMedicalHistories: dataPoint.metadata.comorbidMedicalHistories,
            nonComorbidMedicalHistories: dataPoint.metadata.nonComorbidMedicalHistories,
            tooltip: this.createTooltip(dataPoint, true)
        };
        return event;
    }

    private createTooltip(dataPoint: ITrackDataPoint, hasEndDate: boolean): string {
        let tooltip = 'Event: <b>Exacerbation severity grade</b><br/>';
        tooltip += !_.isNil(dataPoint.metadata.numberOfDoseReceived) ? (
            'Number of study drug doses recieved: <b>' + dataPoint.metadata.numberOfDoseReceived + '</b><br/>'
            ) : '';
        tooltip += 'Severity of exacerbation: <b>' + (dataPoint.metadata.severityGrade || 'Unknown') + '</b>';
        return tooltip;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.group = ExacerbationsTrackUtils.EXACERBATIONS_SUMMARY_SUB_TRACK_NAME;
        event.metadata.tooltip = this.createTooltip(dataPoint, false);
        return event;
    }
}
