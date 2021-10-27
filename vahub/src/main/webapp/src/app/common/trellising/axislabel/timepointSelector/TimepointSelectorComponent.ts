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
import {List} from 'immutable';
import {DisplayedGroupBySetting} from '../../store/actions/TrellisingActionCreator';

@Component({
    selector: 'timepoint-selector',
    templateUrl: 'TimepointSelectorComponent.html',
})
export class TimepointSelectorComponent {
    @Input() label: string;
    @Input() selectedOption: DisplayedGroupBySetting;
    @Input() availableTrellisAxisOptions: List<string>;

    @Output() apply: EventEmitter<any> = new EventEmitter<any>();

    applyValue() {
        this.apply.emit(this.selectedOption);
    }
}
