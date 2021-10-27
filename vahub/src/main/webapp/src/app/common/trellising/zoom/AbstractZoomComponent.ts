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

import {Input, Output, EventEmitter, ElementRef} from '@angular/core';
import {isFinite, isNull} from 'lodash';
import * as noUiSlider from 'nouislider';
import {IZoom, TrellisDesign} from '../index';
import {TabId} from '../store/ITrellising';

export interface SliderOptions {
    step: number;
    start: number[];
    direction: 'rtl' | 'ltr';
    range: { max: number, min: number };
    orientation: 'vertical' | 'horizontal';
    connect: boolean;
    tooltips?: any;
    margin?: number;
    behaviour?: string;
    format?: any;
}

export abstract class AbstractZoomComponent {
    options: SliderOptions;

    @Input() tabId: TabId;
    @Input() zoom: IZoom;
    @Input() noData: boolean;
    @Input() height: number;
    @Input() trellisDesign: TrellisDesign;
    @Output() update: EventEmitter<IZoom> = new EventEmitter<IZoom>();

    slider: any;

    private sliderElement: any;

    private oldZoomValues = new Array<string>();

    constructor(protected elementRef: ElementRef) { }

    protected updateSlider(): void {
        if (!this.slider && this.options) {
            this.createSlider(this.options);
        }
        if (this.slider) {
            this.setRange();
        }

        if (this.noData && this.sliderElement || (this.tabId === TabId.BIOMARKERS_HEATMAP_PLOT && this.zoom.absMax === this.zoom.absMin)) {
            this.sliderElement.setAttribute('disabled', true);
        } else {
            this.sliderElement.removeAttribute('disabled');
        }

    }

    protected setRange(): void {
        if (!this.zoom) {
            return;
        }
        if (isFinite(this.zoom.absMin) && isFinite(this.zoom.absMax) && this.zoom.absMax !== this.zoom.absMin) {
            this.slider.updateOptions({
                start: [this.zoom.zoomMin, this.zoom.zoomMax],
                range: {
                    min: this.zoom.absMin,
                    max: this.zoom.absMax
                }
            });
        }
    }

    protected createSlider(options: SliderOptions): void {
        this.sliderElement = this.elementRef.nativeElement.children[0];
        // TODO: Hack until we refactor
        if (isNull(this.options.range.max)) {
            this.options.range.max = this.options.range.min + 1;
        }
        noUiSlider.create(this.sliderElement, options);
        this.slider = this.sliderElement.noUiSlider;

        this.slider.on('change', (values: string[], handle: number) => {
            if (this.zoom && this.hasZoomChanged(values)) {
                this.update.emit(<IZoom>{
                    absMin: this.zoom.absMin,
                    absMax: this.zoom.absMax,
                    zoomMax: parseFloat(values[1]),
                    zoomMin: parseFloat(values[0])
                });
            }
        });

        this.slider.on('slide', (value: string[], handle: number) => {
            const tooltips = this.sliderElement.querySelectorAll('.noUi-tooltip');
            this.showTooltip(tooltips);
        });

        this.slider.on('end', (value: string[], handle: number) => {
            const tooltips = this.sliderElement.querySelectorAll('.noUi-tooltip');
            this.hideTooltip(tooltips);
        });
    }

    protected showTooltip(tooltips: any[]): void {
        if (tooltips && this.options.tooltips) {
            tooltips[0].classList.remove('hidden');
            tooltips[1].classList.remove('hidden');
        }
    }

    protected hideTooltip(tooltips: any[]): void {
        if (tooltips && this.options.tooltips) {
            tooltips[0].classList.add('hidden');
            tooltips[1].classList.add('hidden');
        }
    }

    private hasZoomChanged(values: string[]): boolean {
        if (this.oldZoomValues.length === 0) {
            this.rememberZoomValues(values);
            return true;
        }

        const hasChanged = this.oldZoomValues[0] !== values[0] || this.oldZoomValues[1] !== values[1];

        this.rememberZoomValues(values);

        return hasChanged;
    }

    private rememberZoomValues(values: string[]): void {
        this.oldZoomValues[0] = values[0];
        this.oldZoomValues[1] = values[1];
    }
}
