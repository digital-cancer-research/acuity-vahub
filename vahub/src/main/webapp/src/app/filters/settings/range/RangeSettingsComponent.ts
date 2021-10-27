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

import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
    selector: 'range-setting',
    templateUrl: 'RangeSettingsComponent.html',
    styleUrls: ['../dataTypes/PlotSettings.css']
})
export class RangeSettingsComponent implements OnInit {
    @Input() id: string;
    @Input() minConst: number;
    @Input() maxConst: number;
    @Input() currentValue: number;
    inputModel: number;

    @Output() onChange: EventEmitter<any> = new EventEmitter<any>();

    ngOnInit(): void {
        this.inputModel = this.currentValue;
    }

    handleChange({target: {value}}): void {
        let actualValue = +value;
        if (actualValue > this.maxConst) {
            actualValue = this.maxConst;
        } else if (actualValue < this.minConst) {
            actualValue = this.minConst;
        }
        this.inputModel = actualValue;
        this.onChange.emit(actualValue);
    }
}
