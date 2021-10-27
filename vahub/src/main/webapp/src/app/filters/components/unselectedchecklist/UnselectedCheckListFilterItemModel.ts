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

import {clone, cloneDeep, isUndefined} from 'lodash';
import {ListFilterItemModel} from '../list/ListFilterItemModel';
import {FILTER_TYPE, ValuesWithEmpty, Values} from '../dtos';
import {DatasetViews} from '../../../security/DatasetViews';

export class UnselectedCheckListFilterItemModel extends ListFilterItemModel {

    public initialValues: any[];
    public isDisabled = false;
    datasetViews: DatasetViews;

    constructor(key: string, displayName: string, datasetViews?: DatasetViews) {
        super(key, displayName);
        this.type = FILTER_TYPE.UNSELECTED_CHECK_LIST;
        this.reset();
        this.datasetViews = datasetViews;
    }

    reset(): void {
        super.reset();
        this.initialValues = [];
        this.appliedSelectedValues = [];
        this.isDisabled = false;
        this.selectedValues = [];
    }

    clear(): void {
        this.selectedValues = [];
        this.appliedSelectedValues = [];
    }

    clearNotAppliedSelectedValues(): void {
        if (this.appliedSelectedValues.length > 0) {
            this.selectedValues = clone(this.appliedSelectedValues);
        } else {
            this.selectedValues = [];
        }
    }

    showNumberOfSelectedFilters(): boolean {
        return this.numberOfSelectedFilters !== 0 && this.numberOfSelectedFilters < this.initialValues.length;
    }

    toServerObject(manuallyApplied = false): ValuesWithEmpty {

        if (manuallyApplied) {
            this.appliedSelectedValues = clone(this.selectedValues);
        }
        if (this.initialValues && 0 < this.appliedSelectedValues.length) {
            const serverObject: ValuesWithEmpty = {};

            //  check if has null or empty, if so add includeEmptyValues = true to the object
            if (this.hasEmpty()) {
                serverObject['includeEmptyValues'] = true;
            }

            // filter out the null/emptys
            serverObject['values'] = this.removeEmpty(this.appliedSelectedValues);

            return serverObject;
        } else {
            return null;
        }
    }

    /**
     * returnedServerObject: { values ['ABDOMINAL PAIN'] }
     */
    fromServerObject(returnedServerObject: Values): void {
        if (returnedServerObject) {
            this.availableValues = returnedServerObject.values;

            if (this.initialValues.length < returnedServerObject.values.length) {

                this.initialValues = returnedServerObject.values;
                this.selectedValues = [];
            } else if (!this.initialValues.length && this.datasetViews) {
                const tab = window.location.hash.slice(10).split('/')[1];
                const disabledFilterName = this.datasetViews.getFilterIfDisabled(tab);
                this.isDisabled = disabledFilterName !== null;
                this.initialValues = [disabledFilterName];
            }
        } else {
            this.availableValues = [];
        }
    }
}
