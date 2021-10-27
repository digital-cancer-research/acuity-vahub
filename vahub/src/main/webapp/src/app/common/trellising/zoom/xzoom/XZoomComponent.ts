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

import {
    ChangeDetectionStrategy,
    Component,
    ElementRef,
    EventEmitter,
    Input,
    OnChanges,
    OnInit,
    Output,
    SimpleChanges
} from '@angular/core';
import 'wnumb';
import {AbstractZoomComponent} from '../AbstractZoomComponent';
import {IZoom, TrellisDesign} from '../../index';
import {TabId, ZOOM_STEP} from '../../store/ITrellising';

@Component({
    selector: 'trellis-xzoom',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: 'XZoomComponent.html'
})
export class XZoomComponent extends AbstractZoomComponent implements OnInit, OnChanges {
    @Input() zoom: IZoom;
    @Input() noData: boolean;
    @Input() height: number;
    @Input() trellisDesign: TrellisDesign;
    @Input() tabId: TabId;
    @Input() zoomMargin: number;
    @Output() update: EventEmitter<IZoom> = new EventEmitter<IZoom>();

    constructor(protected elementRef: ElementRef) {
        super(elementRef);
    }

    public ngOnInit(): void {
        this.createSliderWithTrellisDesign();
    }

    public ngOnChanges(changes: SimpleChanges): void {
        this.createSliderWithTrellisDesign();
        if (this.zoom) {
            this.updateSlider();
        }
    }

    private createSliderWithTrellisDesign(): void {
        if (this.trellisDesign && this.zoom) {

            this.options = {
                step: this.stepSize(),
                start: [this.zoom.zoomMin, this.zoom.zoomMax],
                direction: 'ltr',
                range: { min: this.zoom.absMin, max: this.zoom.absMax },
                orientation: 'horizontal',
                connect: true,
                tooltips: this.tooltips(),
                behaviour: 'tap-drag'
            };
            if (this.zoom.absMax === this.zoom.absMin && this.zoom.absMax !== undefined) {
                this.options.start = [0, 1];
                this.options.range = { min: 0, max: 1 };

                // Biomarkers plot is a specific case which can't be handled by CATEGORICAL_COUNTS_AND_PERCENTAGES
                // trellisDesign
                if (this.tabId === TabId.BIOMARKERS_HEATMAP_PLOT) {
                    this.options.margin = 1;
                }
            }

            if (!this.zoomMargin && this.trellisDesign === TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES) {
                if (this.zoom.absMax - this.zoom.absMin <= 6) {
                    this.options.margin = this.zoom.absMax - this.zoom.absMin;
                } else if (this.zoom.absMax - this.zoom.absMin > 6) {
                    this.options.margin = 6;
                } else {
                    this.options.margin = 1;
                }
            } else if (this.zoomMargin) {
                this.options.margin = this.zoomMargin;
            }

            this.updateSlider();
            this.setRange();
        }
    }

    public stepSize(): number {
        if (this.trellisDesign === TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES
            || this.tabId === TabId.BIOMARKERS_HEATMAP_PLOT
            || this.tabId === TabId.TL_DIAMETERS_PLOT
            || this.tabId === TabId.TL_DIAMETERS_PER_SUBJECT_PLOT
            || this.tabId === TabId.TUMOUR_RESPONSE_PRIOR_THERAPY
            || this.tabId === TabId.ANALYTE_CONCENTRATION
            || this.tabId === TabId.CTDNA_PLOT) {
            return 1;
        }
        return ZOOM_STEP;
    }

    private tooltips(): any[] {
        switch (this.trellisDesign) {
            case TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES:
            case TrellisDesign.CATEGORICAL_OVER_TIME:
            case TrellisDesign.VARIABLE_Y_VARIABLE_X:
                return;
            default:
                return [wNumb({ decimals: 1 }), wNumb({ decimals: 1 })];
        }
    }

}
