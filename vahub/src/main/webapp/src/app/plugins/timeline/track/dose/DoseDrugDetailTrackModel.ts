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
import {DoseTrackSteppedLineChartEventService} from './DoseTrackSteppedLineChartEventService';
import {DoseTrackUtils} from './DoseTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';

@Injectable()
export class DoseDrugDetailTrackModel extends AbstractTrackModel {

    constructor(protected doseTrackSteppedLineChartPlotEventService: DoseTrackSteppedLineChartEventService) {
        super(doseTrackSteppedLineChartPlotEventService);
    }

    canExpand(): boolean {
        return false;
    }

    canCollapse(): boolean {
        return true;
    }

    getTrackName(): string {
        return DoseTrackUtils.DOSE_TRACK_NAME;
    }

    protected createPlotConfig(subjectId: string): any {
        return {
            id: {
                subject: subjectId,
                track: DoseTrackUtils.DOSE_TRACK_NAME,
                level: DoseTrackUtils.DRUG_DETAIL_SUB_TRACK_EXPANSION_LEVEL
            }
        };
    }

    protected transformDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.drug;
        event.plotOptions.color = DoseTrackUtils.assignColour(dataPoint.metadata.percentChange.perAdmin);
        event.metadata = {
            duration: dataPoint.metadata.duration,
            ongoing: dataPoint.metadata.ongoing,
            imputedEndDate: dataPoint.metadata.imputedEndDate,
            active: dataPoint.metadata.periodType === 'ACTIVE' || dataPoint.metadata.percentChange.perAdmin !== -100,
            drugDoses: dataPoint.metadata.drugDoses,
            periodType: dataPoint.metadata.periodType,
            subsequentPeriodType: dataPoint.metadata.subsequentPeriodType,
            special: dataPoint.metadata.special
        };
        return event;
    }

    protected transformNoneDurationalDataPoint(dataPoint: ITrackDataPoint): RawEvent {
        const event = super.transformNoneDurationalDataPoint(dataPoint);
        event.group = dataPoint.metadata.drug;
        event.metadata = {
            ongoing: dataPoint.metadata.ongoing,
            special: true
        };
        return event;
    }
}
