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
    Component, Input, Output, EventEmitter, OnInit, OnDestroy,
    SimpleChanges, OnChanges
} from '@angular/core';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {AxisLabelComponent} from '../AxisLabelComponent';
import {AxisLabelService} from '../AxisLabelService';
import {DynamicAxis} from '../../store/ITrellising';

@Component({
    selector: 'trellis-yaxis',
    templateUrl: '../AxisLabelComponent.html',
    styleUrls: ['YStyle.css'],
    animations: [
        trigger('openClose', [
            state('collapsed, void', style({opacity: '0', left: '0%'})),
            state('expanded', style({opacity: '1', left: '50%'})),
            transition('collapsed <=> expanded', [animate(500, style({opacity: '1', left: '50%'}))])
        ])
    ]
})
export class YAxisLabelComponent extends AxisLabelComponent implements OnInit, OnDestroy, OnChanges {
    @Input() options: any;
    @Input() option: DynamicAxis | string;
    @Input() tabId: string;
    @Input() advancedMeasuredControlRequired: boolean;
    @Output() update: EventEmitter<DynamicAxis | string> = new EventEmitter<DynamicAxis | string>();

    protected measureNumberOption: string;
    protected measureNumberOptions: string[] = null;
    protected unitOption: string;
    protected unitOptions: string[] = null;

    constructor(protected axisLabelService: AxisLabelService) {
        super(axisLabelService);
        this.tooltip = 'Y-Axis settings';
    }

    ngOnInit(): void {
        this.onInit();
    }

    ngOnChanges(changes: SimpleChanges): void {
        if (this.advancedMeasuredControlRequired) {
            if (changes['option']) {
                this.measureNumberOption = this.axisLabelService.getdMeasureNumberOptionByFullOption(this.option);
                this.unitOption = this.axisLabelService.getUnitOptionByFullOption(this.option);
            }
            if (changes['options']) {
                this.measureNumberOptions = this.axisLabelService.getAvailableMeasureNumberOptions(this.options.toJS());
                this.unitOptions = this.axisLabelService.getAvailableUnitOptions(this.options.toJS());
            }
        }

    }

    ngOnDestroy(): void {
        this.onDestroy();
    }

    public updateMeasureNumberSelection(event: any): void {
        this.measureNumberOption = event.target.value;
    }

    public updateUnitSelection(event: any): void {
        this.unitOption = event.target.value;
    }
    public onApply(): void {
        const option = this.axisLabelService.constructAxisLabel(this.measureNumberOption, this.unitOption);
        this.update.emit(option);
        // this.option = option;
        this.isOpen = false;
    }
}
