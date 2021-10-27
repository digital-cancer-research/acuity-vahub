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

import {Component, ChangeDetectionStrategy, ElementRef, SimpleChanges, OnInit, OnChanges, Input, Output, EventEmitter} from '@angular/core';
import 'wnumb';

import {AbstractZoomComponent} from '../AbstractZoomComponent';
import {TrellisDesign, IZoom} from '../../index';
import {TabId, ZOOM_STEP} from '../../store/ITrellising';

@Component({
    selector: 'trellis-yzoom',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: 'YZoomComponent.html'
})
export class YZoomComponent extends AbstractZoomComponent implements OnInit, OnChanges {
    @Input() tabId: TabId;
    @Input() zoom: IZoom;
    @Input() noData: boolean;
    @Input() height: number;
    @Input() trellisDesign: TrellisDesign;
    @Output() update: EventEmitter<IZoom> = new EventEmitter<IZoom>();

    constructor(protected elementRef: ElementRef) {
        super(elementRef);
    }

    public ngOnInit(): void {
        this.createSliderWithTrellisDesign();
        this.updateSlider();
    }

    public ngOnChanges(changes: SimpleChanges): void {
        this.createSliderWithTrellisDesign();
        if (this.zoom && this.height) {
            this.updateSlider();
        }
    }

    private stepSize(): number {
        if (this.tabId === TabId.BIOMARKERS_HEATMAP_PLOT ||
            this.tabId === TabId.TUMOUR_RESPONSE_PRIOR_THERAPY) {
            return 1;
        }
        return ZOOM_STEP;
    }

    private createSliderWithTrellisDesign(): void {
        if (this.trellisDesign) {
            // if we have a small range we want to round by 3 decimals, not default 2
            const round = this.zoom.absMin < 0.1 && this.zoom.absMax > 0 ? 3 : 2;
            this.options = {
                step: this.stepSize(),
                start: [0, 100],
                direction: 'rtl',
                range: { min: 0, max: 100 },
                orientation: 'vertical',
                connect: true,
                tooltips: this.tooltips(),
                behaviour: 'tap-drag',
                format: wNumb({decimals: round})
            };

            this.updateSlider();
        }
    }

    private tooltips(): any[] {
        switch (this.tabId) {
            case TabId.ANALYTE_CONCENTRATION:
            return [wNumb({ decimals: 1 }), wNumb({ decimals: 1 })];
            default:
                return;
        }
    }
}
