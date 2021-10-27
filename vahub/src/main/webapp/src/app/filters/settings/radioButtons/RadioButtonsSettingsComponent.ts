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

import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';

@Component({
    selector: 'radio-buttons-setting',
    templateUrl: 'RadioButtonsSettingsComponent.html',
    styleUrls: ['../../filters.css', '../dataTypes/PlotSettings.css']
})
export class RadioButtonsSettingsComponent implements OnChanges, OnInit {

    // you can pass currentOption or selectedValue to this component. It was made because of different types of options for settings
    @Input() currentOption: any;
    @Input() previousSelectedValue: any;
    @Input() availableValues: any[];
    @Input() selectedValue: string;
    @Input() name: string;

    @Output() onChange: EventEmitter<any> = new EventEmitter<any>();


    ngOnChanges(changes: SimpleChanges): void {
        // if there is no currentOption param then it won't carry out
        if (changes.currentOption && changes.currentOption.currentValue) {
            this.selectedValue = changes.currentOption.currentValue.get('trellisedBy');
        }
    }

    ngOnInit(): void {
        this.selectedValue = this.previousSelectedValue;
    }

    changeHandler() {
        this.onChange.emit({currentOption: this.currentOption, selectedValue: this.selectedValue});
    }
}
