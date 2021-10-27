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

import {Component, Input, Output, EventEmitter, OnChanges, SimpleChanges} from '@angular/core';
import {EcgWarnings, ECG_WARNING_OPTIONS} from '../../../../store/ITimeline';
import {fromJS} from 'immutable';
import * as  _ from 'lodash';

@Component({
    selector: 'ecg-warnings-config',
    templateUrl: 'EcgWarningsConfigurationComponent.html'
})
export class EcgWarningsConfigurationComponent implements OnChanges {
    @Input() ecgWarnings: EcgWarnings;
    @Output() updateEcgWarnings: EventEmitter<EcgWarnings> = new EventEmitter<EcgWarnings>();
    ecgWarningsOptions = ECG_WARNING_OPTIONS;
    currentEcgWarnings: EcgWarnings;

    ngOnChanges(changes: SimpleChanges): void {
        if (this.ecgWarnings) {
            this.currentEcgWarnings = this.ecgWarnings.toJS();
        }
    }

    anyWarningAvailable(): boolean {
        return _.some(ECG_WARNING_OPTIONS, (warning) => {
            return this.currentEcgWarnings[warning.key].available;
        });
    }

    updateEcgWarningsOption(ecgWarningItem: any): void {
        this.currentEcgWarnings[ecgWarningItem.key].selected = !this.currentEcgWarnings[ecgWarningItem.key].selected;
        const that = this;
        setTimeout(() => {
            that.updateEcgWarnings.emit(fromJS(this.currentEcgWarnings));
        }, 0);
    }
}
