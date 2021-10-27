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
import {SpirometryYAxisValue} from '../../../store/ITimeline';

@Component({
    selector: 'spirometry-config',
    templateUrl: 'SpirometryConfigurationComponent.html'
})
export class SpirometryConfigurationComponent implements OnChanges {
    @Input() spirometryYAxisValue: SpirometryYAxisValue;
    @Output() updateSpirometryYAxisValue: EventEmitter<SpirometryYAxisValue> = new EventEmitter<SpirometryYAxisValue>();
    spirometryYAxisValueOptions: SpirometryYAxisValue[] = [SpirometryYAxisValue.RAW, SpirometryYAxisValue.CHANGE_FROM_BASELINE, SpirometryYAxisValue.PERCENT_CHANGE_FROM_BASELINE];
    currentSpirometryYAxisValue: SpirometryYAxisValue;

    ngOnChanges(changes: SimpleChanges): void {
        this.currentSpirometryYAxisValue = this.spirometryYAxisValue;
    }

    updateYAxisValueOption(yAxisValue: SpirometryYAxisValue): void {
        this.currentSpirometryYAxisValue = yAxisValue;
        const that = this;
        setTimeout(() => {
            that.updateSpirometryYAxisValue.emit(yAxisValue);
        }, 0);
    }
}
