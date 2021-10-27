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
import {VitalsYAxisValue} from '../../../store/ITimeline';

@Component({
    selector: 'vitals-config',
    templateUrl: 'VitalsConfigurationComponent.html'
})
export class VitalsConfigurationComponent implements OnChanges {
    @Input() vitalsYAxisValue: VitalsYAxisValue;
    @Output() updateVitalsYAxisValue: EventEmitter<VitalsYAxisValue> = new EventEmitter<VitalsYAxisValue>();
    vitalsYAxisValueOptions: VitalsYAxisValue[] = [VitalsYAxisValue.RAW, VitalsYAxisValue.CHANGE_FROM_BASELINE, VitalsYAxisValue.PERCENT_CHANGE_FROM_BASELINE];
    currentVitalsYAxisValue: VitalsYAxisValue;

    ngOnChanges(changes: SimpleChanges): void {
        this.currentVitalsYAxisValue = this.vitalsYAxisValue;
    }

    updateYAxisValueOption(yAxisValue: VitalsYAxisValue): void {
        this.currentVitalsYAxisValue = yAxisValue;
        const that = this;
        setTimeout(() => {
            that.updateVitalsYAxisValue.emit(yAxisValue);
        }, 0);
    }
}
