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
import {ConmedsTrackUtils} from './ConmedsTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';

@Injectable()
export class ConmedsLevelTrackModel extends AbstractTrackModel {

    constructor(protected barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
        this.categories.push(ConmedsTrackUtils.MEDICATION_LEVEL_SUB_TRACK_NAME);
    }

    canExpand(): boolean {
        return false;
    }

    canCollapse(): boolean {
        return true;
    }

    getTrackName(): string {
        return ConmedsTrackUtils.CONMEDS_TRACK_NAME;
    }

    protected createPlotConfig(subjectId: string): any {
        return {
            id: {
                subject: subjectId,
                track: ConmedsTrackUtils.CONMEDS_TRACK_NAME,
                level: ConmedsTrackUtils.MEDICATION_LEVEL_TRACK_EXPANSION_LEVEL
            }
        };
    }

    protected transformDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.conmedMedication;
        event.plotOptions.color = ConmedsTrackUtils.assignColour(dataPoint.metadata.numberOfConmeds, ConmedsTrackUtils.MEDICATION_LEVEL_TRACK_EXPANSION_LEVEL);
        event.metadata = {
            ongoing: dataPoint.metadata.ongoing,
            imputedEndDate: dataPoint.metadata.imputedEndDate,
            duration: dataPoint.metadata.duration,
            conmed: dataPoint.metadata.conmed,
            dose: dataPoint.metadata.dose,
            frequency: dataPoint.metadata.frequency,
            indication: dataPoint.metadata.indication,
            conmedMedication: dataPoint.metadata.conmedMedication,
            tooltip: this.createTooltip(dataPoint)
        };
        return event;
    }

    private createTooltip(dataPoint: ITrackDataPoint): string {
        let tooltip = 'Event: <b>Conmed medication</b><br/>';
        tooltip += `Conmed:<b>${dataPoint.metadata.conmed}</b></br>`;
        const dose = dataPoint.metadata.dose || 'N/A';
        const frequency = dataPoint.metadata.frequency || 'N/A';
        const indication = dataPoint.metadata.indication || 'N/A';
        tooltip += `Dose: <b>${dose}</b></br>`;
        tooltip += `Frequency: <b>${frequency}</b></br>`;
        tooltip += `Indication: <b>${indication}</b>`;
        return tooltip;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.conmedMedication;
        return event;
    }

}
