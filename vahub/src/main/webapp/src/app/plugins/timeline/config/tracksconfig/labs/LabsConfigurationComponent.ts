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
import {LabsYAxisValue} from '../../../store/ITimeline';

@Component({
    selector: 'labs-config',
    templateUrl: 'LabsConfigurationComponent.html'
})
export class LabsConfigurationComponent implements OnChanges {
    @Input() labsYAxisValue: LabsYAxisValue;
    @Output() updateLabsYAxisValue: EventEmitter<LabsYAxisValue> = new EventEmitter<LabsYAxisValue>();
    labsYAxisValueOptions: LabsYAxisValue[] = [
        LabsYAxisValue.RAW,
        LabsYAxisValue.CHANGE_FROM_BASELINE,
        LabsYAxisValue.PERCENT_CHANGE_FROM_BASELINE,
        LabsYAxisValue.REF_RANGE_NORM,
        LabsYAxisValue.TIMES_UPPER_REF,
        LabsYAxisValue.TIMES_LOWER_REF
     ];
    currentLabsYAxisValue: LabsYAxisValue;

    ngOnChanges(changes: SimpleChanges): void {
        this.currentLabsYAxisValue = this.labsYAxisValue;
    }

    updateYAxisValueOption(yAxisValue: LabsYAxisValue): void {
        this.currentLabsYAxisValue = yAxisValue;
        const that = this;
        setTimeout(() => {
            that.updateLabsYAxisValue.emit(yAxisValue);
        }, 0);
    }
}
