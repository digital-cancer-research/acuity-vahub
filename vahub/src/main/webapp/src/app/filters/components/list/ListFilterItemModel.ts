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

import {FILTER_TYPE, Values, ValuesWithEmpty} from '../dtos';
import * as  _ from 'lodash';
import {BaseFilterItemModel} from '../BaseFilterItemModel';
import {EMPTY} from '../../../common/trellising/store';

export class ListFilterItemModel extends BaseFilterItemModel {

    selectedValues: any[];
    appliedSelectedValues: any[];
    availableValues: any[];
    includeEmptyValues: boolean;
    filterText: string;
    sizeDynamic = true;
    maxSize = 15;
    disabled: boolean;
    selectedItemsQueue: number[] = [];

    constructor(key: string, displayName: string, sizeDynamic?: boolean, maxSize?: number) {
        super(FILTER_TYPE.LIST, key, displayName);
        if (!_.isUndefined(sizeDynamic)) {
            this.sizeDynamic = sizeDynamic;
        }
        if (!_.isUndefined(maxSize)) {
            this.maxSize = maxSize;
        }
        this.reset();
    }

    reset(): void {
        this.selectedValues = [];
        this.appliedSelectedValues = [];
        this.availableValues = [];
        this.includeEmptyValues = true;
        this.filterText = '';
        this.selectedItemsQueue = [];
    }

    clear(): void {
        this.selectedValues = [];
        this.appliedSelectedValues = [];
        this.selectedItemsQueue = [];
    }

    clearNotAppliedSelectedValues(): void {
        this.selectedValues = _.clone(this.appliedSelectedValues);
    }

    /**
     * Transform the filter field data into server object.
     *  eg.
     *   {
     *      values: [ 'ABDOMINAL DISCOMFORT' ],
     *      includeEmptyValues: false
     *   }
     */
    toServerObject(manuallyApplied = false): ValuesWithEmpty {
        if (manuallyApplied) {
            this.appliedSelectedValues = _.clone(this.selectedValues);
        }
        if (this.appliedSelectedValues.length > 0) {
            const serverObject: ValuesWithEmpty = {};

            //  check if has null or empty, if so add includeEmptyValues = true to the object
            if (this.hasEmpty()) {
                /* tslint:disable:no-string-literal */
                serverObject['includeEmptyValues'] = true;
                /* tslint:enable:no-string-literal */
            }

            // filter out the null/emptys
            /* tslint:disable:no-string-literal */
            serverObject['values'] = this.removeEmpty(this.appliedSelectedValues);
            /* tslint:enable:no-string-literal */

            return serverObject;
        } else {
            return null;
        }
    }

    /**
     * returnedServerObject: { values ['ABDOMINAL PAIN'] }
     */
    fromServerObject(returnedServerObject: Values): void {
        if (returnedServerObject && !_.isEmpty(returnedServerObject.values)) {
            this.availableValues = returnedServerObject.values;
            const index = this.availableValues.indexOf(null);
            if (index !== -1) {
                this.availableValues.splice(index, 1, EMPTY);
            }
            this.availableValues = this.availableValues.map(val => val.toString());
        } else {
            this.availableValues = [];
        }
    }

    hasEmpty(): boolean {
        if (this.isInSelectedList(null) || this.isInSelectedList(EMPTY)) {
            return true;
        }
    }

    removeEmpty(values: any[]): any[] {
        if (values) {
            return values.filter((item): boolean => {
                return item !== null && item !== undefined && item !== EMPTY;
            });
        } else {
            return values;
        }
    }

    /**
     * From the UI, when looping over all items need to check if its selected
     */
    isInSelectedList(item: any): boolean {
        if (_.isNull(item)) {
            return this.selectedValues.indexOf(EMPTY) !== -1 ||
                this.selectedValues.indexOf(item) !== -1;
        } else {
            return this.selectedValues.indexOf(item) !== -1;
        }
    }

    /**
     * From the UI, when looping over all items need to check if its avaliable
     */
    isInAvaliableList(item: any): boolean {
        return this.availableValues.indexOf(item) !== -1;
    }

    updateNumberOfSelectedFilters(): void {
        this.numberOfSelectedFilters = this.selectedValues.length;
    }

    setSelectedValues(values: any): void {
        this.selectedValues = values.values;
    }
}
