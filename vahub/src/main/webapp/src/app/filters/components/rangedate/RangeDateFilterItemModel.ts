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

import {ReflectiveInjector} from '@angular/core';
import {clone, isNull, isEmpty} from 'lodash';
import * as moment from 'moment';

import {FILTER_TYPE, RangeWithEmpty, Range} from '../dtos';
import {DateUtilsService} from '../../../common/utils/DateUtilsService';
import {BaseRangeFilterItemModel} from '../BaseRangeFilterItemModel';

export class RangeDateFilterItemModel extends BaseRangeFilterItemModel {

    public selectedValues: RangeWithEmpty;
    public appliedSelectedValues: RangeWithEmpty;
    public availableValues: Range;
    public originalValues: Range;
    public textBoxSelectedValues: Range;
    private dateUtilsService: DateUtilsService;

    constructor(key: string, displayName: string) {
        super(FILTER_TYPE.RANGE_DATE, key, displayName);
        this.dateUtilsService = ReflectiveInjector.resolveAndCreate([DateUtilsService]).get(DateUtilsService);
        this.reset();
    }

    public reset(): void {
        this.selectedValues = {
            from: null,
            to: null,
            includeEmptyValues: true
        };
        this.appliedSelectedValues = clone(this.selectedValues);
        this.textBoxSelectedValues = clone(this.selectedValues);
        this.availableValues = {from: 0, to: 0};
        super.reset();
    }

    public clear(): void {
        this.selectedValues = {
            from: this.availableValues.from,
            to: this.availableValues.to,
            includeEmptyValues: true
        };
        this.appliedSelectedValues = clone(this.selectedValues);
        this.textBoxSelectedValues = clone(this.selectedValues);
        super.clear();
    }

    clearNotAppliedSelectedValues(): void {
        if (this.appliedSelectedValues.from) {
            this.selectedValues.from = this.appliedSelectedValues.from;
        } else {
            this.selectedValues.from = this.availableValues.from;
        }
        if (this.appliedSelectedValues.to) {
            this.selectedValues.to = this.appliedSelectedValues.to;
        } else {
            this.selectedValues.to = this.availableValues.to;
        }
        this.selectedValues.includeEmptyValues = this.appliedSelectedValues.includeEmptyValues;
    }

    /**
     * Transform the filter field data into server object.
     *  eg.
     * {
     *   startDate: { from: "2011-04-14T00:00:00", to: "2011-04-22T00:00:00", includeEmptyValues: true}
     * }
     */
    public toServerObject(manuallyApplied = false, isCohortFilter = false): any {
        let serverObject: any = {};
        if (manuallyApplied) {
            this.appliedSelectedValues = clone(this.selectedValues);
        }

        if (isNull(this.appliedSelectedValues.from) && isNull(this.appliedSelectedValues.to) && isCohortFilter) {
            return serverObject;
        }

        if (this.haveMadeChange) {
            serverObject = {
                from: this.dateUtilsService.toTimestamp(this.appliedSelectedValues.from),
                to: this.dateUtilsService.toTimestamp(this.appliedSelectedValues.to),
                includeEmptyValues: this.appliedSelectedValues.includeEmptyValues
            };
        } else if (this.appliedSelectedValues.includeEmptyValues === false) {
            serverObject = {
                from: this.dateUtilsService.toTimestamp(this.appliedSelectedValues.from),
                to: this.dateUtilsService.toTimestamp(this.appliedSelectedValues.to),
                includeEmptyValues: false
            };
        }
        if (!isCohortFilter) {
            if (isNull(serverObject.from)) {
                serverObject.from = this.originalValues.from;
            }
            if (isNull(serverObject.to)) {
                serverObject.to = this.originalValues.to;
            }
        }
        return serverObject;
    }

    /**
     * Transform the server object format to local item model object
     *
     * @param {Range} serverObject - server filter data
     */
    public fromServerObject(serverObject: Range): void {
        if (isEmpty(this.originalValues)) {
            this.originalValues = serverObject;
        }
        if (!isEmpty(serverObject) && !isNull(serverObject.from) && !isNull(serverObject.to)) {
            const dateFrom = this.dateUtilsService.toShortDate(serverObject.from);
            const dateTo = this.dateUtilsService.toShortDate(serverObject.to);

            if (dateFrom
                && (this.isFilterLoadingForTheFirstTime(this.availableValues.from)
                    || this.isServerDateBeforeCurrentFromDate(serverObject.from))) {
                this.availableValues.from = dateFrom;
            }

            if (dateTo
                && (this.isFilterLoadingForTheFirstTime(this.availableValues.to)
                    || this.isServerDateAfterCurrentToDate(serverObject.to))) {
                this.availableValues.to = dateTo;
            }

            if (!this.haveMadeChange) {
                this.selectedValues.from = dateFrom;
                this.selectedValues.to = dateTo;
                this.textBoxSelectedValues.from = dateFrom;
                this.textBoxSelectedValues.to = dateTo;
            }
        }
    }

    updateNumberOfSelectedFilters(): void {
        this.numberOfSelectedFilters = this.haveMadeChange ? 1 : 0;
    }

    setSelectedValues(values: any): void {
        this.selectedValues.from = this.dateUtilsService.toShortDate(values.from);
        this.selectedValues.to = this.dateUtilsService.toShortDate(values.to);
        this.textBoxSelectedValues.from = this.getValueIfNull(values.from, this.availableValues.from);
        this.textBoxSelectedValues.to = this.getValueIfNull(values.to, this.availableValues.to);
        this.haveMadeChange = true;
    }

    private getValueIfNull(value: string, availableValue: string): string {
        const date = value || this.dateUtilsService.toTimestamp(availableValue);
        return this.dateUtilsService.toShortDate(date);
    }

    private isFilterLoadingForTheFirstTime(currentAvailableValue: any): boolean {
        return currentAvailableValue === 0;
    }

    private isServerDateBeforeCurrentFromDate(serverFromDate: string): boolean {
        const serverFrom = moment.utc(serverFromDate);
        const selectedFrom = moment.utc(this.availableValues.from, 'DD-MMM-YYYY');
        return serverFrom.isBefore(selectedFrom);
    }

    private isServerDateAfterCurrentToDate(serverToDate: string): boolean {
        const serverTo = moment.utc(serverToDate);
        const selectedTo = moment.utc(this.availableValues.to, 'DD-MMM-YYYY');
        return serverTo.isAfter(selectedTo);
    }
}
