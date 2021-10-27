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

import {TabId} from '../ITrellising';
import {TrackName} from '../../../../plugins/timeline/store/ITimeline';

import {Subject} from 'rxjs/Subject';
import {DatasetViews} from '../../../../security/DatasetViews';
import {BaseFilterItemModel} from '../../../../filters/components/BaseFilterItemModel';
import {LabsFiltersModel} from '../../../../filters/dataTypes/labs/LabsFiltersModel';
import {find} from 'lodash';

export interface Track {
    name: TrackName;
    expansionLevel: number;
    selected: boolean;
    data?: Array<any>;
}

@Injectable()
export class TimelineConfigService {

    navigateToTimeline: Subject<TabId> = new Subject<TabId>();

    constructor(private labsFilterModel: LabsFiltersModel,
                private datasetViews: DatasetViews) {
    }

    getInitialState(tabId: TabId): Track[] {
        this.navigateToTimeline.next(tabId);
        switch (tabId) {
            case TabId.AES_COUNTS_BARCHART:
            case TabId.AES_OVER_TIME:
            case TabId.CI_EVENT_COUNTS:
            case TabId.CI_EVENT_OVERTIME:
            case TabId.CEREBROVASCULAR_COUNTS:
            case TabId.CEREBROVASCULAR_EVENTS_OVER_TIME:
            case TabId.CVOT_ENDPOINTS_COUNTS:
            case TabId.CVOT_ENDPOINTS_OVER_TIME:
            case TabId.AES_CHORD_DIAGRAM:
                return [
                    {
                        name: TrackName.AES,
                        expansionLevel: 2,
                        selected: true,
                        data: []
                    }];
            case TabId.LAB_BOXPLOT:
            case TabId.LAB_LINEPLOT:
            case TabId.LAB_SHIFTPLOT:
                return [
                    {
                        name: TrackName.LABS,
                        expansionLevel: 3,
                        selected: true,
                        data: []
                    }
                ];
            case TabId.LIVER_HYSLAW:
                this.setLabFilterToFilterForLiverLabCodes();
                return [
                    {
                        name: TrackName.LABS,
                        expansionLevel: 3,
                        selected: true,
                        data: []
                    }
                ];
            case TabId.VITALS_BOXPLOT:
                return [
                    {
                        name: TrackName.VITALS,
                        expansionLevel: 2,
                        selected: true,
                        data: []
                    }
                ];
            case TabId.CARDIAC_BOXPLOT:
                return [
                    {
                        name: TrackName.ECG,
                        expansionLevel: 2,
                        selected: true,
                        data: []
                    }
                ];
            case TabId.CONMEDS_BARCHART:
                return [
                    {
                        name: TrackName.CONMEDS,
                        expansionLevel: 2,
                        selected: true,
                        data: []
                    }
                ];
            case TabId.EXACERBATIONS_OVER_TIME:
            case TabId.EXACERBATIONS_GROUPED_COUNTS:
            case TabId.EXACERBATIONS_COUNTS:
            case TabId.LUNG_FUNCTION_BOXPLOT:
                return [
                    {
                        name: TrackName.EXACERBATION,
                        expansionLevel: 1,
                        selected: true,
                        data: []
                    },
                    {
                        name: TrackName.SPIROMETRY,
                        expansionLevel: 2,
                        selected: true,
                        data: []
                    }
                ];
            case TabId.RENAL_CKD_BARCHART:
            case TabId.RENAL_LABS_BOXPLOT:
                this.setLabFilterToFilterForRenalLabCodes();
                return [
                    {
                        name: TrackName.LABS,
                        expansionLevel: 3,
                        selected: true,
                        data: []
                    }
                ];
            case TabId.PK_RESULT_OVERALL_RESPONSE:
            case TabId.DOSE_PROPORTIONALITY_BOX_PLOT:
            case TabId.ANALYTE_CONCENTRATION:
                return [
                    {
                        name: TrackName.DOSE,
                        expansionLevel: 2,
                        selected: true,
                        data: []
                    }];
            default:
                return [{
                    name: TrackName.SUMMARY,
                    expansionLevel: 1,
                    selected: true,
                    data: []
                }];
        }
    }

    private setLabFilterToFilterForLiverLabCodes(): void {
        const liverLabNames = this.datasetViews.getJumpFilterValues('liver', 'labNames');
        this.setLabFilterToFilterForLabCodes(liverLabNames);
    }

    private setLabFilterToFilterForRenalLabCodes(): void {
        const renalLabNames = this.datasetViews.getJumpFilterValues('renal-java', 'labCodes');
        this.setLabFilterToFilterForLabCodes(renalLabNames);
    }

    private setLabFilterToFilterForLabCodes(labNames: any): void {
        this.labsFilterModel.reset();
        const itemModel: any = find(this.labsFilterModel.itemsModels, (model: BaseFilterItemModel) => {
            return model.key === 'labcode';
        });
        itemModel.selectedValues = labNames;
        itemModel.appliedSelectedValues = labNames;
        itemModel.numberOfSelectedFilters = labNames.length;
        this.labsFilterModel.getFilters(true);
    }
}
