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

import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges} from '@angular/core';
import {EcgYAxisValue} from '../../../../store/ITimeline';

@Component({
    selector: 'ecg-y-axis-config',
    templateUrl: 'EcgYAxisConfigurationComponent.html'
})
export class EcgYAxisConfigurationComponent implements OnChanges {
    @Input() ecgYAxisValue: EcgYAxisValue;
    @Output() updateEcgYAxisValue: EventEmitter<EcgYAxisValue> = new EventEmitter<EcgYAxisValue>();
    ecgYAxisValueOptions: EcgYAxisValue[] = [EcgYAxisValue.RAW, EcgYAxisValue.CHANGE_FROM_BASELINE, EcgYAxisValue.PERCENT_CHANGE_FROM_BASELINE];
    currentEcgYAxisValue: EcgYAxisValue;

    ngOnChanges(changes: SimpleChanges): void {
        this.currentEcgYAxisValue = this.ecgYAxisValue;
    }

    updateYAxisValueOption(yAxisValue: EcgYAxisValue): void {
        this.currentEcgYAxisValue = yAxisValue;
        const that = this;
        setTimeout(() => {
            that.updateEcgYAxisValue.emit(yAxisValue);
        }, 0);
    }
}
