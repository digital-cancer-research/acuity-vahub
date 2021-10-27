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
export class AesDetailTrackModel extends AbstractTrackModel {

    constructor(protected barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
    }

    canExpand(): boolean {
        return false;
    }

    canCollapse(): boolean {
        return true;
    }

    getTrackName(): string {
        return AesTrackUtils.AE_TRACK_NAME;
    }

    protected createPlotConfig(subjectId: string): any {
        return {
            id: {
                subject: subjectId,
                track: AesTrackUtils.AE_TRACK_NAME,
                level: AesTrackUtils.DETAIL_SUB_TRACK_EXPANSION_LEVEL
            }
        };
    }

    protected transformDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.pt;
        event.plotOptions.color = AesTrackUtils.assignColour(dataPoint.metadata.severityGradeNum, dataPoint.metadata.noEndDate, dataPoint.metadata.ongoing);
        event.metadata = {
            duration: dataPoint.metadata.duration,
            pt: dataPoint.metadata.pt,
            hlt: dataPoint.metadata.hlt,
            soc: dataPoint.metadata.soc,
            ongoing: dataPoint.metadata.ongoing,
            imputedEndDate: dataPoint.metadata.imputedEndDate,
            severityGradeNum: dataPoint.metadata.severityGradeNum,
            severityGrade: dataPoint.metadata.severityGrade,
            tooltip: this.createTooltip(dataPoint)
        };
        return event;
    }

    private createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = `PT: <b> ${dataPoint.metadata.pt}</b>`;

        // serious
        if (dataPoint.metadata.serious) {
            tooltip += `<br/>Serious: <b>${dataPoint.metadata.serious.toLowerCase()}</b>`;
        }

        // causality
        if (dataPoint.metadata.causality) {
            tooltip += `<br/>Causality: <b>${dataPoint.metadata.causality.toLowerCase()}</b>`;
        }

        // action taken
        if (dataPoint.metadata.actionTaken) {
            tooltip += `<br/>Action taken: <b>${dataPoint.metadata.actionTaken.toLowerCase()}</b>`;
        }

        // doses prior to event
        if (dataPoint.metadata.dosesPrior) {
            tooltip += `<br/>Doses prior to event: <b>${dataPoint.metadata.dosesPrior}</b>`;
        }

        tooltip += `<br/>Severity grade: <b>${dataPoint.metadata.severityGrade}</b>`;

        if (dataPoint.metadata.imputedEndDate) {
            tooltip += '<br/><b>No end date</b>';
        }

        return tooltip;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.pt;
        event.plotOptions.height = 20;
        event.plotOptions.width = 20;
        return event;
    }
}
