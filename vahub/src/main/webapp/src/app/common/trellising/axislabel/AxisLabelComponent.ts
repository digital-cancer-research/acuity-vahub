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

import {EventEmitter, Input, Output} from '@angular/core';
import {AxisLabelService} from './AxisLabelService';
import {Subscription} from 'rxjs/Subscription';
import {DynamicAxis, TabId} from '../store';
import {GroupBySetting, XAxisOptions} from '../store/actions/TrellisingActionCreator';

export class AxisLabelComponent {

    public isOpen: boolean;
    public hasEmitted: boolean;
    public tooltip: string;

    @Input() advancedMeasuredControlRequired: boolean;
    @Input() options: any;
    @Input() option: string | DynamicAxis | GroupBySetting | XAxisOptions;
    @Input() tabId: TabId;
    @Input() customTooltip: string;
    @Output() update: EventEmitter<string | DynamicAxis | GroupBySetting> = new EventEmitter<string | DynamicAxis | GroupBySetting>();

    protected axisLabelOpenedSubscription: Subscription;

    constructor(protected axisLabelService: AxisLabelService) {
        this.isOpen = false;
        this.hasEmitted = false;
    }

    protected onInit(): void {
        const that = this;
        this.axisLabelOpenedSubscription = this.axisLabelService.axisLabelOpened.subscribe(() => {
            if (that.hasEmitted) {
                that.hasEmitted = false;
            } else {
                this.closeControl();
            }
        });
    }

    protected onDestroy(): void {
        this.axisLabelOpenedSubscription.unsubscribe();
    }

    public openState(): string {
        return this.isOpen ? 'expanded' : 'collapsed';
    }

    public toggleOpen(): void {
        this.isOpen = !this.isOpen;
        if (this.isOpen) {
            this.hasEmitted = true;
            this.axisLabelService.closeOtherAxisLabels();
        }
    }

    public updateSelection(event): void {
        this.update.emit(event.target.value);
        this.isOpen = false;
    }

    public closeControl(): void {
        if (this.isOpen) {
            this.isOpen = false;
        }
    }

    public toString(value: any): string {
        return value;
    }

    public toObject(value: any): any {
        return value;
    }

}
