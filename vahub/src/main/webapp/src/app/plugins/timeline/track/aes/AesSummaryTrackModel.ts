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

import { Injectable } from '@angular/core';
import {ITrackDataPoint} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {AesTrackUtils} from './AesTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';

@Injectable()
export class AesSummaryTrackModel extends AbstractTrackModel {

    constructor(protected barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
        this.categories.push(AesTrackUtils.AE_SUMMARY_TRACK_NAME);
    }

    canExpand(): boolean {
        return true;
    }

    canCollapse(): boolean {
        return false;
    }

    getTrackName(): string {
        return AesTrackUtils.AE_TRACK_NAME;
    }

    protected createPlotConfig(subjectId: string): any {
        return {
            id: {
                subject: subjectId,
                track: this.getTrackName(),
                level: AesTrackUtils.SUMMARY_SUB_TRACK_EXPANSION_LEVEL
            }
        };
    }

    protected transformDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformDurationalDataPoint(dataPoint);
        event.group = AesTrackUtils.AE_SUMMARY_TRACK_NAME;
        event.plotOptions.color = AesTrackUtils.assignColour(dataPoint.metadata.maxSeverityGradeNum, dataPoint.metadata.noEndDate, dataPoint.metadata.ongoing);
        event.metadata = {
            duration: dataPoint.metadata.duration,
            pts: dataPoint.metadata.pts,
            ongoing: dataPoint.metadata.ongoing,
            imputedEndDate: dataPoint.metadata.imputedEndDate,
            maxSeverityGradeNum: dataPoint.metadata.maxSeverityGradeNum,
            maxSeverityGrade: dataPoint.metadata.maxSeverityGrade,
            numberOfEvents: dataPoint.metadata.numberOfEvents,
            tooltip: this.createTooltip(dataPoint)
        };
        return event;
    }

    private createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = 'Event: <b>Adverse Event Summary</b><br/> Included AEs: <br/>';

        dataPoint.metadata.pts.forEach(pt => {
            if (pt) {
                tooltip += `<b>${pt.toLowerCase()}</b><br/>`;
            }
        });

        tooltip += `Max severity grade: <b>${dataPoint.metadata.maxSeverityGrade}</b>`;

        return tooltip;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.group = AesTrackUtils.AE_SUMMARY_TRACK_NAME;
        event.plotOptions.height = 20;
        event.plotOptions.width = 20;
        return event;
    }

}
