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
import {RangeDateFilterItemModel} from '../rangedate/RangeDateFilterItemModel';
import {BaseMapFilterItemModel} from '../BaseMapFilterItemModel';
import * as  _ from 'lodash';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

export class MapRangeDateFilterItemModel extends BaseMapFilterItemModel {

    static MAP_RANGE_DISPLAY_NAMES = {
        drugsDiscontinuationDate: `Date of <%= drugName %> Discontinuation`
    };

    constructor(key: string, displayName: string) {
        super(FILTER_TYPE.MAP_RANGE_DATE, key, displayName);
    }

    createFilterItemModelInstance(drugName: string): BaseFilterItemModel {
        let listFilterDisplayName: string;
        const template: string = MapRangeDateFilterItemModel.MAP_RANGE_DISPLAY_NAMES[this.key];
        listFilterDisplayName = _.template(template)({drugName: drugName});
        return new RangeDateFilterItemModel(drugName, listFilterDisplayName);
    }
}
