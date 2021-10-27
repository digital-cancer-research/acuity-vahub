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

import { TabId, FilterId, IJumpLink } from '../store/ITrellising';
import {
    AesFiltersModel,
    LabsFiltersModel,
    AbstractFiltersModel} from '../../../filters/module';
import { DatasetViews } from '../../../security/DatasetViews';
import * as  _ from 'lodash';
import {BaseFilterItemModel} from '../../../filters/components/BaseFilterItemModel';

@Injectable()
export class TrellisingJumpService {

    constructor(private aesFiltersModel: AesFiltersModel,
                private labsFiltersModel: LabsFiltersModel,
                protected datasetViews: DatasetViews) {

    }

    getLinksForCurrentTab(tabId: TabId): IJumpLink[] {
        let links: IJumpLink[];
        switch (tabId) {
            case TabId.RENAL_LABS_BOXPLOT:
                links = [{
                    originTab: 'renal-java',
                    filtersToReset: ['soc', 'hlt', 'pt'],
                    label: 'Jump to renal and urinary disorder AEs',
                    destinationFilterKey: FilterId.AES,
                    destinationUrl: '/plugins/aes/subject-counts',
                    filterKey: 'soc',
                    metadataFilterKey: 'socs'

                }, {
                    originTab: 'renal-java',
                    filtersToReset: ['labCategory', 'labcode'],
                    label: 'Jump to renal-related lab results',
                    destinationFilterKey: FilterId.LAB,
                    destinationUrl: '/plugins/labs/box-plot',
                    filterKey: 'labcode',
                    metadataFilterKey: 'labCodes'
                }];
                break;
            case TabId.LIVER_HYSLAW:
                links = [{
                    originTab: 'liver',
                    filtersToReset: ['soc', 'hlt', 'pt'],
                    label: 'Jump to hepatobiliary disorder AEs',
                    destinationFilterKey: FilterId.AES,
                    destinationUrl: '/plugins/aes/subject-counts',
                    filterKey: 'soc',
                    metadataFilterKey: 'socs'

                }, {
                    originTab: 'liver',
                    filtersToReset: ['labCategory', 'labcode'],
                    label: 'Jump to liver-related lab results',
                    destinationFilterKey: FilterId.LAB,
                    destinationUrl: '/plugins/labs/box-plot',
                    filterKey: 'labcode',
                    metadataFilterKey: 'labNames'
                }];
                break;
            case TabId.CARDIAC_BOXPLOT:
                links = [{
                    originTab: 'cardiac',
                    filtersToReset: ['soc', 'hlt', 'pt'],
                    label: 'Jump to cardiac-related AEs',
                    destinationFilterKey: FilterId.AES,
                    destinationUrl: '/plugins/aes/subject-counts',
                    filterKey: 'soc',
                    metadataFilterKey: 'socs'
                }];
                break;
            default:
                links = [];
                break;
        }
        return this.filterEmptyPresets(links);
    }

    /**
     * Only include the jumps if they have preset filter values
     */
    private filterEmptyPresets(links: IJumpLink[]): IJumpLink[] {
        return links.filter(link => {
            const filtersToPreset = this.datasetViews.getJumpFilterValues(link.originTab, link.metadataFilterKey);
            return filtersToPreset && filtersToPreset.length > 0;
        });
    }

    presetFiltersForJump(link: IJumpLink): void {
        const filtersModel = this.getFilterModelById(link.destinationFilterKey);
        if (filtersModel) {
            _.forEach(filtersModel.itemsModels, (filterItem: BaseFilterItemModel) => {
                if (link.filtersToReset.indexOf(filterItem.key) !== -1) {
                    filterItem.reset();
                    filterItem.resetNumberOfSelectedFilters();
                }
            });
            const filtersToPreset = this.datasetViews.getJumpFilterValues(link.originTab, link.metadataFilterKey);
            const itemModel: any = _.find(filtersModel.itemsModels, (itemModel: BaseFilterItemModel) => {
                return itemModel.key === link.filterKey;
            });
            itemModel.selectedValues = _.clone(filtersToPreset);
            itemModel.appliedSelectedValues = _.clone(filtersToPreset);
            itemModel.numberOfSelectedFilters = filtersToPreset.length;
            filtersModel.getFilters(true);
        }
    }

    getFilterModelById(filterId: FilterId): AbstractFiltersModel {
        switch (filterId) {
            case FilterId.AES:
                return this.aesFiltersModel;
            case FilterId.LAB:
                return this.labsFiltersModel;
            default:
                return null;
        }
    }
}
