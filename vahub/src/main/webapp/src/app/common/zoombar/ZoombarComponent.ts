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

import {Component, Input, Output, ElementRef, OnChanges, SimpleChanges} from '@angular/core';
import {Subject} from 'rxjs/Subject';
import * as  _ from 'lodash';
import * as noUiSlider from 'nouislider';
import {RangeModel} from './RangeModel';

@Component({
    selector: 'zoombar',
    templateUrl: 'ZoombarComponent.html'
})
export class ZoombarComponent implements OnChanges {

    @Input() min: number;
    @Input() max: number;
    @Input() handleMin: number;
    @Input() handleMax: number;
    @Input() stepSize: number;
    @Output() onChange: Subject<RangeModel> = new Subject<RangeModel>();
    @Output() onSlide: Subject<RangeModel> = new Subject<RangeModel>();

    private slider: any;

    constructor(private elementRef: ElementRef) {
    }

    // Safe conversion of value to number with default.
    private toNumber(value: any, dflt = 0): number {
        if (_.isUndefined(value) || _.isNull(value)) {
            return dflt;
        }
        return +value;
    }

    // Create a range object that will always be acceptable to nouislider.
    private createRange(): {min: number, max: number} {
        const rangeMin = this.toNumber(this.min);
        const rangeMax = Math.max(this.toNumber(this.max), rangeMin + 1);
        return {min: rangeMin, max: rangeMax};
    }

    // Create a values object that will always be acceptable to nouislider.
    private createValues(rangeMin: number, rangeMax: number): [number, number] {
        const startMin = this.toNumber(this.handleMin, rangeMin);
        const startMax = this.toNumber(this.handleMax, rangeMax);
        return [startMin, startMax];
    }

    // Ensure the slider is created, if necessary using default values.
    ensureSlider(): boolean {
        if (!_.isUndefined(this.slider)) {
            return true;
        }
        if (_.isUndefined(this.elementRef)) {
            return false;
        }
        if (!_.isElement(this.elementRef.nativeElement) || this.elementRef.nativeElement.children.length === 0) {
            return false;
        }

        const range = this.createRange();
        const values = this.createValues(range.min, range.max);
        const options = {
            range: range,
            start: values,
            step: this.stepSize,
            direction: 'ltr',
            orientation: 'horizontal',
            connect: true
        };

        const element = this.elementRef.nativeElement.children[0];
        noUiSlider.create(element, options);
        this.slider = element.noUiSlider;

        const that = this;
        this.slider.on('change', (values: string[], handle: number) => {
            that.onChange.next(
                <RangeModel>
                {
                    absMin: that.min,
                    min: parseFloat(values[0]),
                    max: parseFloat(values[1]),
                    absMax: that.max
                }
            );
        });
        this.slider.on('slide', (values: string[], handle: number) => {
            that.onSlide.next(
                <RangeModel>
                {
                    absMin: that.min,
                    min: parseFloat(values[0]),
                    max: parseFloat(values[1]),
                    absMax: that.max
                }
            );
        });
        return true;
    }

    ngOnChanges(changes: SimpleChanges): void {
        // if (!_.isUndefined(this.min) && !_.isUndefined(this.max) && !_.isUndefined(this.elementRef.nativeElement.children[0])) {
        if (this.ensureSlider()) {
            if (_.isUndefined(this.min) || _.isUndefined(this.max)) {
                this.slider.target.setAttribute('disabled', true);
            } else {
                this.slider.target.removeAttribute('disabled');
                this.slider.updateOptions({range: this.createRange()});
                this.slider.set([this.toNumber(this.handleMin), this.toNumber(this.handleMax)]);
            }
        }
    }
}
