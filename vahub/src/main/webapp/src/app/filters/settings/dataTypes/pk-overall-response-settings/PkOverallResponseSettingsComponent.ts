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

import { Component, OnInit } from '@angular/core';
import {TrellisingDispatcher} from '../../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {TrellisingObservables} from '../../../../common/trellising/store/observable/TrellisingObservables';
import {ScaleTypes} from '../../../../common/trellising/store';

@Component({
  selector: 'pk-overall-response-settings',
  templateUrl: './PkOverallResponseSettingsComponent.html',
  styleUrls: ['../../../filters.css']
})
export class PkOverallResponseSettingsComponent implements OnInit {

    constructor(public trellisingObservables: TrellisingObservables,
                public trellisingDispatcher: TrellisingDispatcher) {}

    isOpen = false;
    selectedScale: ScaleTypes;
    previousSelectedScale: ScaleTypes;

    ngOnInit(): void {
        this.trellisingObservables.scaleType.subscribe(s => {
            this.selectedScale = s;
            this.previousSelectedScale = s;
        });
    }

    setSelectedScale(event: any): void {
        this.selectedScale = event.selectedValue;
    }

    apply() {
        if (this.selectedScale !== this.previousSelectedScale) {
            this.trellisingDispatcher.updateScale(this.selectedScale);
            this.previousSelectedScale = this.selectedScale;
        }
    }
}
