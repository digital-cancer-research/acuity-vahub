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

import * as  _ from 'lodash';

import {FILTER_TYPE, Values, ValuesWithEmpty} from '../dtos';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

export class CohortFilterItemModel extends BaseFilterItemModel {

    selectedValues: any[];

    constructor(key: string, displayName: string, sizeDynamic?: boolean, maxSize?: number) {
        super(FILTER_TYPE.COHORT_EDITOR, key, displayName);
        this.reset();
    }

    reset(): void {
        this.selectedValues = [];
    }

    clear(): void {
        this.selectedValues = [];
    }

    clearNotAppliedSelectedValues(): void {
        // Do nothing
    }

    toServerObject(manuallyApplied = false): ValuesWithEmpty {
        if (_.isEmpty(this.selectedValues)) {
            return null;
        } else {
            return {
                values: this.selectedValues
            };
        }
    }

    fromServerObject(returnedServerObject: Values): void {
        // Does not go to the server
    }

    updateNumberOfSelectedFilters(): void {
        // Do nothing
    }

    setSelectedValues(values: any): void {
        // Do nothing
    }
}
