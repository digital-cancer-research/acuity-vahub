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

import { isNull, isEmpty, clone, isEqual } from 'lodash';

import { FILTER_TYPE, RangeWithEmpty, Range } from '../dtos';
import { RangeModel } from '../../../common/zoombar/RangeModel';
import { BaseRangeFilterItemModel } from '../BaseRangeFilterItemModel';
import { GENE_PERCENTAGE } from '../../dataTypes/biomarkers/BiomarkersFiltersConstants';

export class RangeFilterItemModel extends BaseRangeFilterItemModel {

    public selectedValues: RangeWithEmpty;
    public appliedSelectedValues: RangeWithEmpty;
    public textBoxSelectedValues: RangeWithEmpty;
    public availableValues: RangeModel;
    public originalValues: Range;
    public stepSize: number;

    constructor(key: string, displayName: string, stepSize: number = 1) {
        super(FILTER_TYPE.RANGE, key, displayName);
        this.reset();
        this.stepSize = stepSize;
    }

    public reset(): void {
        this.selectedValues = { from: 0, to: 0, includeEmptyValues: true };
        this.appliedSelectedValues = { from: null, to: null, includeEmptyValues: true };
        this.textBoxSelectedValues = { from: 0, to: 0, includeEmptyValues: true };
        this.availableValues = { absMin: 0, absMax: 0, min: 0, max: 0 };
        super.reset();
    }

    public clear(): void {
        this.selectedValues = { from: this.availableValues.min, to: this.availableValues.max, includeEmptyValues: true };
        this.appliedSelectedValues = {
            from: this.availableValues.min,
            to: this.availableValues.max,
            includeEmptyValues: true
        };
        this.textBoxSelectedValues = {
            from: this.availableValues.min,
            to: this.availableValues.max,
            includeEmptyValues: true
        };
        super.clear();
    }

    clearNotAppliedSelectedValues(): void {
        if (this.appliedSelectedValues.from) {
            this.selectedValues.from = this.appliedSelectedValues.from;
        } else {
            this.selectedValues.from = this.availableValues.min;
        }
        if (this.appliedSelectedValues.to) {
            this.selectedValues.to = this.appliedSelectedValues.to;
        } else {
            this.selectedValues.to = this.availableValues.max;
        }
        this.selectedValues.includeEmptyValues = this.appliedSelectedValues.includeEmptyValues;
    }

    /**
     * Transform the filter field data into server object.
     *  eg.
     * {
     *   startDay : {from: 71, to: 674, includeEmptyValues: true}
     * }
     */
    public toServerObject(manuallyApplied = false, isCohortFilter = false): any {
        const serverObject: any = {};
        if (manuallyApplied) {
            this.appliedSelectedValues = clone(this.selectedValues);
        }

        if (isNull(this.appliedSelectedValues.from) && isNull(this.appliedSelectedValues.to) && isCohortFilter) {
            return serverObject;
        }

        if (this.areSelectedAndAvailableValuesEqual()) {
            //  dont add anything if they are the same and includeEmptyValues = true
            if (this.appliedSelectedValues.includeEmptyValues === false) {
                serverObject.from = this.appliedSelectedValues.from;
                serverObject.to = this.appliedSelectedValues.to;
                serverObject.includeEmptyValues = false;
            }
        } else if (this.haveMadeChange) {
            serverObject.from = this.appliedSelectedValues.from;
            serverObject.to = this.appliedSelectedValues.to;
            serverObject.includeEmptyValues = this.appliedSelectedValues.includeEmptyValues;
        }
        if (!isCohortFilter) {
            if (isNull(this.selectedValues.from)) {
                serverObject.from = this.availableValues.min;
            }
            if (isNull(this.selectedValues.to)) {
                serverObject.to = this.availableValues.max;
            }
        }
        return serverObject;
    }

    /**
     * Transform the server object to local item model object
     *
     * @param {Range} serverObject - server filter data
     */
    public fromServerObject(serverObject: Range): void {
        if (isEmpty(this.originalValues)) {
            this.originalValues = serverObject;
        }
        if (this.doFilterValuesNeedToBeUpdated(serverObject)) {
            serverObject = serverObject || { from: 0, to: 0 };
            serverObject.to = serverObject.to !== 0 ? Math.round(serverObject.to * 100) / 100 : 0;
            serverObject.from = serverObject.from !== 0 ? Math.round(serverObject.from * 100) / 100 : 0;

            if (serverObject.from !== this.availableValues.min || serverObject.to !== this.availableValues.max) {
                if (this.isFilterLoadingForTheFirstTime()) {
                    this.availableValues.min = serverObject.from;
                    this.availableValues.max = serverObject.to;
                    this.haveMadeChange = false;
                }
            }

            if (this.key === GENE_PERCENTAGE) {
                this.availableValues.min = 0;
                this.availableValues.max = 100;
            } else {
                if (this.isFilterLoadingForTheFirstTime()
                    || this.availableValues.min >= serverObject.from) {
                    this.availableValues.min = serverObject.from;
                }

                if (this.isFilterLoadingForTheFirstTime()
                    || this.availableValues.max <= serverObject.to) {
                    this.availableValues.max = serverObject.to;
                }
            }

            this.selectedValues.from = serverObject.from;
            this.selectedValues.to = serverObject.to;
            this.textBoxSelectedValues.from = serverObject.from;
            this.textBoxSelectedValues.to = serverObject.to;
        }
        this.disabled = this.availableValues.min === 0 && this.availableValues.max === 0;
    }

    updateNumberOfSelectedFilters(): void {
        this.numberOfSelectedFilters = this.haveMadeChange ? 1 : 0;
    }

    setSelectedValues(values: any): void {
        this.selectedValues.from = values.from;
        this.selectedValues.to = values.to;
        this.textBoxSelectedValues.from = values.from || this.availableValues.min;
        this.textBoxSelectedValues.to = values.to || this.availableValues.max;
        this.haveMadeChange = true;
    }

    private doFilterValuesNeedToBeUpdated(serverObject: Range): boolean {
        return !this.haveMadeChange && !isEmpty(serverObject);
    }

    private isFilterLoadingForTheFirstTime(): boolean {
        return isEqual({ absMin: 0, absMax: 0, min: 0, max: 0 }, this.availableValues);
    }

    private areSelectedAndAvailableValuesEqual(): boolean {
        return +this.appliedSelectedValues.from === +this.availableValues.min
            && +this.appliedSelectedValues.to === +this.availableValues.max;
    }

    private areSelectedValuesNull(): boolean {
        return isNull(this.selectedValues.from) && isNull(this.selectedValues.to);
    }
}
