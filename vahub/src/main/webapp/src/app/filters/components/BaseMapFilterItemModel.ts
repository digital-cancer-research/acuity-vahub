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

import {BaseFilterItemModel} from './BaseFilterItemModel';
import * as  _ from 'lodash';
import {FILTER_TYPE} from './dtos';

export abstract class BaseMapFilterItemModel extends BaseFilterItemModel {

    filters: BaseFilterItemModel[];
    isMapFilter = true;

    constructor(type: FILTER_TYPE, key: string, displayName: string) {
        super(type, key, displayName);
        this.filters = [];
    }

    toServerObject(manuallyApplied?: boolean): any {
        if (!this.filters.length) {
            return null;
        }
        const serverObject = {
            map: {}
        };
        _.forEach(this.filters, (filterItem: BaseFilterItemModel) => {
            const filterItemServerObject = filterItem.toServerObject(true);
            if (filterItemServerObject && !_.isEmpty(filterItemServerObject)) {
                serverObject.map[filterItem.key] = filterItemServerObject;
            }

        });
        return _.isEmpty(serverObject.map) ? null : serverObject;
    }

    fromServerObject(returnedServerObject: any): void {
        const that = this;
        if (returnedServerObject) {
            that.filters = that.findEmptyFilters(that.filters, returnedServerObject);
            _.forEach(returnedServerObject.map, (value, key) => {
                let listFilterModel: BaseFilterItemModel;
                listFilterModel = _.find(that.filters, {key: key} as any);
                if (!listFilterModel) {
                    listFilterModel = that.createFilterItemModelInstance(key as any);
                    that.filters.push(listFilterModel);
                }
                listFilterModel.fromServerObject(value);
            });
        }
    }

    abstract createFilterItemModelInstance(key: string): BaseFilterItemModel;

    reset(resetAndDestroy = false): void {
        if (resetAndDestroy) {
            this.filters = [];
        } else {
            _.forEach(this.filters, (filterItem: BaseFilterItemModel) => {
                filterItem.reset();
            });
        }
    }

    clear(name?: string): void {
        _.forEach(this.filters, (filterItem: BaseFilterItemModel) => {
            filterItem.clear();
        });
    }

    resetNumberOfSelectedFilters(): void {
        _.forEach(this.filters, (filterItem: BaseFilterItemModel) => {
            filterItem.numberOfSelectedFilters = 0;
        });
    }

    clearNotAppliedSelectedValues(): void {
        _.forEach(this.filters, (filterItem: BaseFilterItemModel) => {
            filterItem.clearNotAppliedSelectedValues();
        });
    }

    updateNumberOfSelectedFilters(): void {
        _.forEach(this.filters, (filterItem: BaseFilterItemModel) => {
            filterItem.updateNumberOfSelectedFilters();
        });
    }

    findEmptyFilters(filters: BaseFilterItemModel[], returnedServerObject: any): BaseFilterItemModel[] {
        _.forEach(filters, (value) => {
            const filterKey = value.key;
            if (!returnedServerObject.map[filterKey]) {
                const filterType = this.filters[0].type;
                let emptyFilter = {};
                switch (filterType) {
                    case (FILTER_TYPE.LIST):
                        emptyFilter = {values: []};
                        break;
                    case(FILTER_TYPE.RANGE):
                        emptyFilter = {'to': 0, 'from': 0};
                        break;
                    case(FILTER_TYPE.RANGE_DATE):
                        emptyFilter = {'to': null, 'from': null};
                        break;
                    default:
                        emptyFilter = {};
                        break;
                }
                returnedServerObject.map[filterKey] = emptyFilter;
            }
        });
        return filters;
    }

    setSelectedValues(values: any): void {
        const that = this;
        this.fromServerObject(values);
        _.forEach(values.map, (value, key) => {
            const filterModel: BaseFilterItemModel = _.find(that.filters, {key: key} as any);
            filterModel.setSelectedValues(value);
        });
        this.updateNumberOfSelectedFilters();
    }
}
