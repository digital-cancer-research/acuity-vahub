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
