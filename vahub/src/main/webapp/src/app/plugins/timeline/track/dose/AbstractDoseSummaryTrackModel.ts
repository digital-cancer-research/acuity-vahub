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

import {ITrackDataPoint} from '../../store/ITimeline';
import {RawEvent} from '../../chart/IChartEvent';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {DoseTrackUtils} from './DoseTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';

export abstract class AbstractDoseSummaryTrackModel extends AbstractTrackModel {

    constructor(protected barChartPlotEventService: BarChartEventService) {
        super(barChartPlotEventService);
    }

    getTrackName(): string {
        return DoseTrackUtils.DOSE_TRACK_NAME;
    }

    protected transformDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformDurationalDataPoint(dataPoint);
        event.plotOptions.color = DoseTrackUtils.assignColour(dataPoint.metadata.percentChange.perAdmin);
        event.metadata = {
            duration: dataPoint.metadata.duration,
            ongoing: dataPoint.metadata.ongoing,
            imputedEndDate: dataPoint.metadata.imputedEndDate,
            tooltip: this.createTooltip(dataPoint),
            drugDoses: dataPoint.metadata.drugDoses,
            periodType: dataPoint.metadata.periodType,
            subsequentPeriodType: dataPoint.metadata.subsequentPeriodType
        };
        return event;
    }

    private createTooltip(dataPoint: ITrackDataPoint): string {
        const drugDoses = dataPoint.metadata.drugDoses ? dataPoint.metadata.drugDoses.filter(e => e.dose > 0) : <any>[];

        if (drugDoses.length > 0) {
            let tooltip = 'Active dosing';

            drugDoses.forEach(drugDose => {
                tooltip += `<br/><b>${drugDose.drug}: </b> ${drugDose.dose} ${drugDose.doseUnit} ${drugDose.frequency.name}`;
            });

            return tooltip;
        } else {
            return 'Inactive dosing';
        }

    }
}
