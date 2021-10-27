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
    animate,
    ChangeDetectionStrategy,
    Component,
    EventEmitter,
    Input,
    OnChanges, OnDestroy, OnInit,
    Output,
    SimpleChanges,
    state,
    style,
    transition,
    trigger
} from '@angular/core';
import {TimelineAxisLabelService} from './TimelineAxisLabelService';
import {DynamicAxis, TabId} from '../../../common/trellising/store/ITrellising';
import {AxisLabelService} from '../../../common/trellising/axislabel/AxisLabelService';
import {AxisLabelComponent} from '../../../common/trellising/axislabel/AxisLabelComponent';
import * as _ from 'lodash';

@Component({
    selector: 'timeline-axis-label',
    templateUrl: 'TimelineAxisLabelComponent.html',
    styleUrls: ['TimelineAxisLabelComponent.css'],
    animations: [
        trigger('openClose', [
            state('collapsed, void', style({opacity: '0', bottom: '0px'})),
            state('expanded', style({opacity: '1', bottom: '160px'})),
            transition('collapsed <=> expanded', [animate(500, style({opacity: '1', bottom: '160px'}))])
        ])
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class TimelineAxisLabelComponent extends AxisLabelComponent implements OnChanges, OnInit, OnDestroy {
    @Input() option: string | DynamicAxis;
    @Input() options: any;
    @Input() tabId: TabId;
    @Input() customTooltip: string;
    @Output() update: EventEmitter<DynamicAxis | string> = new EventEmitter<DynamicAxis | string>();

    localOption: DynamicAxis;
    availableValueStrings: string[];
    additionalSelection: any[];

    constructor(protected axisLabelService: AxisLabelService,
                protected xAxisLabelService: TimelineAxisLabelService) {
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
    }

    public hasAdditionalSelection(): boolean {
        return !_.isEmpty(this.additionalSelection);
    }

    public valueStringArgToString(value: { value: string, stringarg: string }): string {
        if (!value.stringarg) {
            return value.value;
        } else {
            return value.value + ' ' + value.stringarg;
        }
    }

    public valueStringArgToObject(value: { value: string, stringarg: string }): string {
        return JSON.stringify(value);
    }

    public updateValueString(event): void {
        this.localOption = this.xAxisLabelService.optionFromValueSelection(event, this.options);
        this.updateAdditionalSelection();
        if (this.hasAdditionalSelection() === false) {
            this.onApply();
        }
    }

    public updateAdditionalSelectionValue(event): void {
        this.localOption = this.xAxisLabelService.optionFromAdditionalSelection(event, this.localOption, this.options);
    }

    public onApply(): void {
        this.update.emit(this.localOption);
        this.isOpen = false;
    }

    public updateSelection(event): void {
        const selection = JSON.parse(event.target.value);
        this.update.emit(selection);
        this.isOpen = false;
    }

    /**
     * Get Dynamic axis and return it's value
     * @param value {DynamicAxis}
     * @returns {string}
     */
    toString(value: DynamicAxis): string {
        if (value) {
            return value.get('value');
        } else {
            return;
        }
    }

    /**
     * Get Dynamic axis and return string representation of an object
     * @param value {DynamicAxis}
     * @returns {string}
     */
    toObject(value: DynamicAxis): string {
        return JSON.stringify(value.toJS());
    }

    /**
     * Get Dynamic axis and return stringarg
     * @param value
     * @returns {any}
     */
    toAdditionalSelection(value: DynamicAxis): string {
        return value.get('stringarg');
    }
}
