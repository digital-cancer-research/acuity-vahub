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
    Component, Input, Output, EventEmitter, OnInit, OnDestroy, OnChanges,
    SimpleChanges, ChangeDetectionStrategy
} from '@angular/core';
import {isEmpty} from 'lodash';
import {animate, state, style, transition, trigger} from '@angular/animations';

import {AxisLabelComponent} from '../AxisLabelComponent';
import {AxisLabelService} from '../AxisLabelService';
import {TabId, DynamicAxis} from '../../store/ITrellising';
import {XAxisLabelService} from './XAxisLabelService';

@Component({
    selector: 'trellis-xaxis',
    templateUrl: 'AxisLabelComponent.html',
    styleUrls: ['XStyle.css'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    animations: [
        trigger('openClose', [
            state('collapsed, void', style({opacity: '0', bottom: '0px'})),
            state('expanded', style({opacity: '1', bottom: '120px'})),
            transition('collapsed <=> expanded', [animate(500, style({opacity: '1', bottom: '120px'}))])
        ])
    ]
})
export class XAxisLabelComponent extends AxisLabelComponent implements OnInit, OnDestroy, OnChanges {

    @Input() option: string | DynamicAxis;
    @Input() options: any;
    @Input() tabId: TabId;
    @Input() customTooltip: string;
    @Output() update: EventEmitter<DynamicAxis | string> = new EventEmitter<DynamicAxis | string>();

    localOption: DynamicAxis;
    availableValueStrings: { value: string, stringarg: string }[];
    additionalSelection: any[];
    public hasAdditionalSelection = false;

    constructor(protected axisLabelService: AxisLabelService,
                protected xAxisLabelService: XAxisLabelService) {
        super(axisLabelService);
        this.tooltip = 'X-Axis settings';
        //  this.timeScaleOption = this.option.value;
    }

    ngOnInit(): void {
        this.onInit();
    }

    ngOnDestroy(): void {
        this.onDestroy();
    }

    ngOnChanges(changes: SimpleChanges): void {
        this.localOption = <DynamicAxis>this.option;
        if (this.options && this.localOption) {
            this.availableValueStrings = this.xAxisLabelService.generateAvailableValueStrings(this.options);
            this.updateAdditionalSelection();
        }
    }

    protected updateAdditionalSelection(): void {
        this.additionalSelection = this.xAxisLabelService.generateAdditionalSelection(this.localOption, this.options);
        this.hasAdditionalSelection = !isEmpty(this.additionalSelection);
    }

    public updateValueString(event): void {
        this.localOption = this.xAxisLabelService.optionFromValueSelection(event, this.options);
        this.updateAdditionalSelection();
        if (!this.hasAdditionalSelection) {
            this.onApply();
        }
    }

    public updateAdditionalSelectionValue(event): void {
        this.localOption = this.xAxisLabelService.optionFromAdditionalSelection(event, this.localOption, this.options);
    }

    public onApply(): void {
        this.update.emit(this.localOption.toJS());
        this.isOpen = false;
    }

    public updateSelection(event): void {
        const selection = JSON.parse(event.target.value);
        this.update.emit(selection);
        this.isOpen = false;
    }

    public toString(value: any): string {
        return this.axisLabelService.fromDynamicAxisToString(value);
    }

    public toObject(value: DynamicAxis): string {
        return JSON.stringify({value: value.get('value'), stringarg: value.get('stringarg')});
    }

    public toAdditionalSelection(value: DynamicAxis): any {
        return value.get('intarg') === null ? 1 : value.get('intarg');
    }
}
