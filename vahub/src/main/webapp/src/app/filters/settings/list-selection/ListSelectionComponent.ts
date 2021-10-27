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

import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Map} from 'immutable';

@Component({
    selector: 'list-selection',
    templateUrl: './ListSelectionComponent.html',
    styleUrls: ['../../filters.css', '../dataTypes/PlotSettings.css']
})
export class ListSelectionComponent {
    @Input() inactive: boolean;
    @Input() elements: Map<string, boolean>;
    @Output() onSelect: EventEmitter<any> = new EventEmitter<any>();

    selectElement(item) {
        this.onSelect.emit({name: item, selected: !this.elements.get(item)});
    }
}
