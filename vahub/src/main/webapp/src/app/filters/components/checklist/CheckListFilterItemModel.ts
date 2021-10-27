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

import {ListFilterItemModel} from '../list/ListFilterItemModel';
import * as _ from 'lodash';
import {FILTER_TYPE, ValuesWithEmpty, Values} from '../dtos';

export class CheckListFilterItemModel extends ListFilterItemModel {

    public initialValues: any[];

    constructor(key: string, displayName: string) {
        super(key, displayName);
        this.type = FILTER_TYPE.CHECK_LIST;
        this.reset();
    }

    reset(): void {
        super.reset();
        this.initialValues = [];
    }

    clear(): void {
        this.selectedValues = _.clone(this.initialValues);
        this.appliedSelectedValues = _.clone(this.initialValues);
    }

    clearNotAppliedSelectedValues(): void {
        if (this.appliedSelectedValues.length > 0) {
            this.selectedValues = _.clone(this.appliedSelectedValues);
        } else {
            this.selectedValues = _.clone(this.initialValues);
        }

    }

    /**
     * Transform the filter field data into server object.
     *
     *  eg.
     *    {
     *      includeEmptyValues: true,
     *       values: ["CTC Grade 1", "CTC Grade 3", "CTC Grade 4", "CTC Grade 5", "Mild", "Moderate", "Severe"]
     *       }
     */
    toServerObject(manuallyApplied = false): ValuesWithEmpty {
        if (manuallyApplied) {
            this.appliedSelectedValues = _.clone(this.selectedValues);
        }
        //if initial is null, then apply when SelectedValue.length > 0, else 0 < SelectedValue.length < initial.length
        if ((_.isUndefined(this.initialValues) && this.appliedSelectedValues.length > 0) ||
            (this.initialValues && 0 < this.appliedSelectedValues.length
                && this.appliedSelectedValues.length < this.initialValues.length)) {

            const serverObject: ValuesWithEmpty = {};

            //  check if has null or empty, if so add includeEmptyValues = true to the object
            if (this.hasEmpty()) {
                /* tslint:disable:no-string-literal */
                serverObject['includeEmptyValues'] = true;
                /* tsslint:enable:no-string-literal */
            }

            // filter out the null/emptys
            /* tslint:disable:no-string-literal */
            serverObject['values'] = this.removeEmpty(this.appliedSelectedValues);
            /* tsslint:enable:no-string-literal */

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

            if (this.initialValues && !this.initialValues.length ||
                this.initialValues.length < returnedServerObject.values.length) {

                this.initialValues = returnedServerObject.values;
                this.selectedValues = _.cloneDeep(returnedServerObject.values);
            }
        } else {
            this.availableValues = [];
        }
    }

    showNumberOfSelectedFilters(): boolean {
        return this.numberOfSelectedFilters !== 0 && this.numberOfSelectedFilters < this.initialValues.length;
    }
}
