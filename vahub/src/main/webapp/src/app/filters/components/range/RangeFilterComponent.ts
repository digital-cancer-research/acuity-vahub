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

import {Component, Input, ViewChild, AfterViewInit} from '@angular/core';

import {RangeFilterItemModel} from './RangeFilterItemModel';
import {RangeModel} from '../../../common/zoombar/RangeModel';
import {ZoombarComponent} from '../../../common/zoombar/ZoombarComponent';
import {BaseRangeComponent} from '../BaseRangeComponent';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

@Component({
    selector: 'rangefilter',
    templateUrl: 'RangeFilterComponent.html',
    styleUrls: ['./RangeFilterComponent.css']
})
export class RangeFilterComponent extends BaseRangeComponent implements AfterViewInit {
    static counter = 0;

    @Input() model: RangeFilterItemModel;
    @Input() openedFilterModel: BaseFilterItemModel;

    @ViewChild(ZoombarComponent) zoombar: ZoombarComponent;

    private timeoutId: number;

    ngAfterViewInit(): void {
        this.zoombar.ensureSlider();
    }

    public onChange(newValue: RangeModel): void {
        clearTimeout(this.timeoutId);
        this.timeoutId = window.setTimeout(() => {
            if (!this.model.disabled && this.haveFiltersChanged(newValue)) {
                this.model.haveMadeChange = true;
                this.model.selectedValues.from = this.nullIfUnchanged(newValue.min, this.model.originalValues.from);
                this.model.selectedValues.to = this.nullIfUnchanged(newValue.max, this.model.originalValues.to);
                this.model.textBoxSelectedValues.from = newValue.min;
                this.model.textBoxSelectedValues.to = newValue.max;
                // this.model.updateNumberOfSelectedFilters();
            }
        }, 500);
    }

    public onSlide(newValue: RangeModel): void {
        this.model.textBoxSelectedValues.from = newValue.min;
        this.model.textBoxSelectedValues.to = newValue.max;
    }

    public onIncludeEmptyValuesChanged(event): void {
        this.model.haveMadeChange = true;
        // this.model.selectedValues.includeEmptyValues = event.target === 'on';
    }

    public onLowerTextBoxChange(event): void {
        if (!this.isValueWithinRange(event.target.value)) {
            event.target.value = this.model.availableValues.min;
        }
        event.target.value = this.fitToStepSize(event.target.value);
        clearTimeout(this.timeoutId);
        if (this.model.textBoxSelectedValues.from.toString() !== event.target.value) {
            this.timeoutId = window.setTimeout(() => {
                this.model.haveMadeChange = true;
                this.model.selectedValues.from = this.nullIfUnchanged(event.target.value, this.model.originalValues.from);
                this.model.selectedValues.to = this.nullIfUnchanged(this.model.textBoxSelectedValues.to, this.model.originalValues.to);
                this.model.textBoxSelectedValues.from = event.target.value;
            }, 500);
        }
    }

    public onUpperTextBoxChange(event): void {
        if (!this.isValueWithinRange(event.target.value)) {
            event.target.value = this.model.availableValues.max;
        }
        event.target.value = this.fitToStepSize(event.target.value);
        clearTimeout(this.timeoutId);
        if (this.model.textBoxSelectedValues.to.toString() !== event.target.value) {
            this.timeoutId = window.setTimeout(() => {
                this.model.haveMadeChange = true;
                this.model.selectedValues.to = this.nullIfUnchanged(event.target.value, this.model.originalValues.to);
                this.model.selectedValues.from = this.nullIfUnchanged(this.model.textBoxSelectedValues.from, this.model.originalValues.from);
                this.model.textBoxSelectedValues.to = event.target.value;
            }, 500);
        }
    }

    private isValueWithinRange(value: number): boolean {
        return value >= this.model.availableValues.min && value <= this.model.availableValues.max;
    }

    /**
     * Removes decimals that are smaller than the step size from input value
     * @param {number} value - value to be adapted to step size
     * @returns {number} - value with correct decimals
     */
    private fitToStepSize(value: number): number {
        //Create multiplier from step size by inverting step size(0.01 <=> 100, 0.1 <=> 10)
        const multiplier = Math.pow(10, -1 * Math.log10(this.model.stepSize));
        return Math.round(value * multiplier) / multiplier;
    }

    private haveFiltersChanged(newValue: RangeModel): boolean {
        const fromChanged = +this.model.selectedValues.from !== +newValue.min;
        const toChanged = +this.model.selectedValues.to !== +newValue.max;
        return fromChanged || toChanged;
    }
}
