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

import {FILTER_TYPE} from '../dtos';
import {ListFilterItemModel} from '../list/ListFilterItemModel';
import {BaseMapFilterItemModel} from '../BaseMapFilterItemModel';
import * as  _ from 'lodash';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

export class MapListFilterItemModel extends BaseMapFilterItemModel {

    static MAP_LIST_FILTER_DISPLAY_NAMES = {
        drugsActionTaken: `Action Taken on <%= drugName %>`,
        drugsCausality: `Causality, <%= drugName %>`,
        drugsDosed: `<%= drugName %> Dosed`,
        drugsMaxDoses: `Max. Dose per Admin. of <%= drugName %>`,
        drugsMaxFrequencies: `Max. Admin. Frequency of <%= drugName %>`,
        drugsDiscontinued: `<%= drugName %> Discontinuation`,
        drugsDiscontinuationReason: `<%= drugName %> Main Reason for Discontinuation`,
        drugsDiscontinuationDate: `Date of Discontinuation of <%= drugName %>`,
        biomarkerGroups: `<%= drugName %> Biomarkers`
    };

    constructor(key: string, displayName: string) {
        super(FILTER_TYPE.MAP_LIST, key, displayName);
    }

    createFilterItemModelInstance(drugName: string): BaseFilterItemModel {
        let listFilterDisplayName: string;
        const template: string = MapListFilterItemModel.MAP_LIST_FILTER_DISPLAY_NAMES[this.key];
        listFilterDisplayName = _.template(template)({drugName: drugName});
        return new ListFilterItemModel(drugName, listFilterDisplayName);
    }
}
