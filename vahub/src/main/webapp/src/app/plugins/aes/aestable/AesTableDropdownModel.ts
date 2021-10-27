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

import {Injectable} from '@angular/core';
import * as _ from 'lodash';

import { DropdownItem } from '../../../common/module';

@Injectable()
export class AesTableDropdownModel {

    private static readonly CUSTOM = 'CUSTOM';
    private static readonly CUSTOM_DROPDOWN: DropdownItem = {
        displayName: 'Special Interest Groups',
        serverName: AesTableDropdownModel.CUSTOM
    };

    aeLevels: Array<DropdownItem> = [
        { displayName: 'PT', serverName: 'PT' },
        { displayName: 'SOC', serverName: 'SOC' },
        { displayName: 'HLT', serverName: 'HLT' }
    ];

    selectedAeLevel: DropdownItem = this.aeLevels[0];

    addSpecialInterestGroupToDropdown(): void {
        if (!this.containsCustom()) {
            this.aeLevels.push(AesTableDropdownModel.CUSTOM_DROPDOWN);
        }
    }

    removeSpecialInterestGroupFromDropdown(): void {
        if (this.containsCustom()) {

            // if current is custom but it doesnt contain custom set to first one
            if (this.selectedAeLevel === AesTableDropdownModel.CUSTOM_DROPDOWN) {
                this.selectedAeLevel = this.aeLevels[0];
            }
            _.remove(this.aeLevels, (aeLevel) => aeLevel === AesTableDropdownModel.CUSTOM_DROPDOWN);
        }
    }

    private containsCustom(): boolean {
        return _.some(this.aeLevels, (aeLevel) => aeLevel === AesTableDropdownModel.CUSTOM_DROPDOWN);
    }
}
