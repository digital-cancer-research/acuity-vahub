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
    Component, animate, state, style, trigger, transition, Input, OnChanges,
    Output, EventEmitter, OnInit, OnDestroy, SimpleChanges
} from '@angular/core';
import {XAxisLabelComponent} from '../xaxis/XAxisLabelComponent';
import {AxisLabelService} from '../AxisLabelService';
import {TabId, DynamicAxis} from '../../store/ITrellising';
import {XAxisLabelService} from '../xaxis/XAxisLabelService';

@Component({
    selector: 'trellis-table-xaxis',
    templateUrl: '../AxisLabelComponent.html',
    styleUrls: ['TableStyle.css'],
    animations: [
        trigger('openClose', [
            state('collapsed, void', style({opacity: '0', bottom: '0px'})),
            state('expanded', style({opacity: '1', bottom: '120px'})),
            transition('collapsed <=> expanded', [animate(500, style({opacity: '1', bottom: '120px'}))])
        ])
    ]
})
export class TableXAxisLabelComponent extends XAxisLabelComponent implements OnInit, OnDestroy, OnChanges {

    @Input() options: any;
    @Input() option: DynamicAxis | string;
    @Input() tabId: TabId;
    @Input() customTooltip: string;
    @Output() update: EventEmitter<DynamicAxis> = new EventEmitter<DynamicAxis>();

    constructor(protected axisLabelService: AxisLabelService,
                protected xAxisLabelService: XAxisLabelService) {
        super(axisLabelService, xAxisLabelService);
        this.tooltip = 'X-Axis settings';
    }

    ngOnInit(): void {
        this.onInit();

        this.tooltip = this.customTooltip || this.tooltip;
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
}
