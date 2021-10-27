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

import 'wnumb';
import {Component, EventEmitter, Input, Output,
    ElementRef, OnChanges, SimpleChanges} from '@angular/core';
import {IZoom, ZoomRecord} from '../store/ITimeline';
import * as noUiSlider from 'nouislider';

@Component({
    selector: 'timeline-zoom-bar',
    template: '<div class="row" style="margin-left: 10px; margin-right: 0px"></div>',
})
export class TimelineZoomBarComponent implements OnChanges {

    slider: any;

    @Input() zoom: IZoom;
    @Input() vertical: boolean;
    @Output() updateZoom = new EventEmitter(false);

    constructor(private elementRef: ElementRef) { }

    ngOnChanges(changes: SimpleChanges): void {
        this.updateSlider();
    }

    updateSlider(): void {
        if (this.zoom) {
            if (!this.slider) {
                this.createSlider();
            } else {
                this.setRange();
            }
        }
    }

    setRange(): void {
        const _that = this;
        this.slider.updateOptions({
            start: [_that.zoom.zoomMin, _that.zoom.zoomMax],
            range: {
                min: _that.zoom.absMin,
                max: _that.zoom.absMax
            }
        });
    }

    createSlider(): void {
        const _that = this;
        const element = this.elementRef.nativeElement.children[0];
        let options: any;

        options = {
            start: [_that.zoom.zoomMin, _that.zoom.zoomMax],
            direction: _that.vertical ? 'rtl' : 'ltr',
            range: {
                min: _that.zoom.absMin,
                max: _that.zoom.absMax
            },
            tooltips: [wNumb({ decimals: 1 }), wNumb({ decimals: 1 })],
            orientation: _that.vertical ? 'vertical' : 'horizontal',
            connect: true,
            behaviour: 'tap-drag'
        };

        noUiSlider.create(element, options);
        this.slider = element.noUiSlider;

        this.slider.on('change', (values: string[], handle: number) => {
            this.slider.set(values);
            const min = parseFloat(values[0]);
            const max = parseFloat(values[1]);
            _that.updateZoom.emit(
                <IZoom>new ZoomRecord({
                    absMin: _that.zoom.absMin,
                    zoomMin: min,
                    zoomMax: max,
                    absMax: _that.zoom.absMax,
                    zoomed: _that.zoom.absMin !== min || _that.zoom.absMax !== max
                })
            );
        });

        this.slider.on('slide', (value: string[], handle: number) => {
            const tooltips = element.querySelectorAll('.noUi-tooltip');
            const tooltipNumber = _that.vertical ? (handle === 0 ? 1 : 0) : handle;
            if (tooltips) {
                tooltips[tooltipNumber].classList.remove('hidden');
            }
        });

        this.slider.on('set', (value: string[], handle: number) => {
            const tooltips = element.querySelectorAll('.noUi-tooltip');
            const tooltipNumber = _that.vertical ? (handle === 0 ? 1 : 0) : handle;
            if (tooltips) {
                tooltips[tooltipNumber].classList.add('hidden');
            }
        });
    }
}
