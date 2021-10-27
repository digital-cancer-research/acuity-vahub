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

import {FILTER_TYPE, Values} from '../dtos';
import * as  _ from 'lodash';
import {BaseFilterItemModel} from '../BaseFilterItemModel';
import {EMPTY} from '../../../common/trellising/store';

export class StudySpecificFilter {
    name: string;
    initialValues: string[];
    availableValues: string[];
    selectedValues: string[];
    appliedSelectedValues: string[];
    numberOfSelectedFilters = 0;

    constructor(filterName: string, options: string[]) {
        this.name = filterName;
        this.initialValues = options;
        this.availableValues = options;
        this.selectedValues = _.cloneDeep(options);
        this.appliedSelectedValues = _.clone(this.selectedValues);
    }

    update(newOptions: string[]): void {
        this.availableValues = newOptions;
        if (this.initialValues && !this.initialValues.length ||
            this.initialValues.length < newOptions.length) {
            this.initialValues = newOptions;
            this.selectedValues = _.cloneDeep(newOptions);
        }
    }

    isItemPresentInAvailableList(item: string): boolean {
        return this.availableValues.indexOf(item) > -1 || (_.isNull(item) && this.availableValues.indexOf(EMPTY) > -1);
    }

    isItemPresentInInitialList(item: string): boolean {
        return this.initialValues.indexOf(item) > -1 || (_.isNull(item) && this.initialValues.indexOf(EMPTY) > -1);
    }

    isItemPresentInSelectedList(item: string): boolean {
        return this.selectedValues.indexOf(item) > -1 || (_.isNull(item) && this.selectedValues.indexOf(EMPTY) > -1);
    }

    isLastAvailableItem(item: string): boolean {
        return this.selectedValues.length === 1 &&
            (this.selectedValues.indexOf(item) === 0 || (_.isNull(item) && this.selectedValues.indexOf(EMPTY) === 0));
    }

    clear(): void {
        this.selectedValues = this.initialValues.slice();
        this.appliedSelectedValues = _.clone(this.selectedValues);
        this.numberOfSelectedFilters = this.initialValues.length;
    }

    clearNotAppliedSelectedValues(): void {
        this.selectedValues = _.clone(this.appliedSelectedValues);
    }

    change(item: string, checked: boolean): void {
        if (checked && this.selectedValues.indexOf(item) === -1) {
            this.selectedValues.push(item);
        } else if (!checked) {
            const index = this.selectedValues.indexOf(item);
            if (index > -1) {
                this.selectedValues.splice(index, 1);
            }
        }
    }

    ready(): boolean {
        //if initial is null, then apply when SelectedValue.length > 0, else 0 < SelectedValue.length < initial.length
        return (!this.initialValues && !this.selectedValues) ||
            (0 < this.selectedValues.length && this.selectedValues.length < this.initialValues.length);
    }

    formattedSelectedValues(): string[] {
        return this.selectedValues.map((v) => {
            return this.name + '--' + v;
        });
    }

    formattedDeselectedValues(manuallyApplied = false): string[] {
        if (manuallyApplied) {
            this.appliedSelectedValues = _.clone(this.selectedValues);
        }
        return this.initialValues.reduce((diff, current) => {
            if ((current && this.appliedSelectedValues.indexOf(current) === -1)
                || (_.isNull(current) && this.appliedSelectedValues.indexOf(current) === -1 && this.appliedSelectedValues.indexOf(EMPTY) === -1)) {
                diff.push(this.name + '--' + current);
            }

            return diff;
        }, []);
    }

    updateNumberOfSelectedFilters(): void {
        this.numberOfSelectedFilters = this.availableValues.length < this.initialValues.length ? this.availableValues.length : 0;
    }
}

export interface StudySpecificFilterEntry {
    name: string;
    option: string;
}

export class StudySpecificFilterModel extends BaseFilterItemModel {

    filters: StudySpecificFilter[];

    constructor(key: string, displayName: string) {
        super(FILTER_TYPE.STUDY_SPECIFIC_FILTERS, key, displayName);
        this.reset();
    }

    reset(): void {
        this.filters = [];
    }

    clear(name?: string): void {
        if (name) {
            const filter = _.find(this.filters, (filter) => {
                return filter.name === name;
            });
            filter.clear();
        } else {
            this.filters.forEach((f) => {
                f.clear();
            });
        }

    }

    clearNotAppliedSelectedValues(): void {
        this.filters.forEach((f) => {
            f.clearNotAppliedSelectedValues();
        });
    }

    change(filterName: string, item: string, checked: boolean): void {
        const filter = _.find(this.filters, {'name': filterName});
        filter.change(item, checked);
    }

    /**
     * Transform the filter field data into server object.
     *
     *  eg.
     *    {
     *       values: ["filter1--option1", "filter2--option1"]
     *       }
     */
    toServerObject(manuallyApplied = false): Values {
        if (this.filters && _.some(this.filters, (f) => {
                return f.ready();
            })) {
            const serverObject: Values = {};
            serverObject.values = _.flatten(this.filters.map((f) => {
                return f.formattedDeselectedValues(manuallyApplied);
            }));
            return serverObject;
        } else {
            return null;
        }
    }

    /**
     * returnedServerObject: { values ["filter1--option1", "filter2--option1"] }
     */
    fromServerObject(returnedServerObject: Values): void {
        if (returnedServerObject) {

            const valuesWithoutNulls = _.reject(returnedServerObject.values, _.isNull);

            // group filter options by name
            const filters = _.groupBy(valuesWithoutNulls.map((v) => {
                const filter: StudySpecificFilterEntry = {name: v.split('--')[0], option: v.split('--')[1]};
                return filter;
            }), 'name');

            if (this.filters.length > 0) {
                this.filters.forEach((f) => {
                    let options = [];
                    if (filters[f.name]) {
                        options = filters[f.name].map((o) => {
                            if (o.option === 'null') {
                                return null;
                            } else {
                                return o.option;
                            }
                        });
                    }
                    f.update(options);
                });
            } else {
                _.forEach(filters, (options, filtername) => {
                    this.filters.push(new StudySpecificFilter(filtername, options.map((o) => {
                        if (o.option === 'null') {
                            return null;
                        } else {
                            return o.option;
                        }
                    })));
                });
            }
        } else {
            if (this.filters) {
                this.filters.forEach((f) => {
                    f.availableValues = [];
                });
            }
        }
    }

    updateNumberOfSelectedFilters(): void {
        // For study specific filters, this has been abstracted to StudySpecificFilter
        this.filters.forEach((f) => {
            f.updateNumberOfSelectedFilters();
        });
        this.numberOfSelectedFilters = _.filter(this.filters, 'numberOfSelectedFilters').length;
    }

    setSelectedValues(values: any): void {
        this.fromServerObject(values);
        this.updateNumberOfSelectedFilters();
    }
}
