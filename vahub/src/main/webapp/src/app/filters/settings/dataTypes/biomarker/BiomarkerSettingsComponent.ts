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

import {Component} from '@angular/core';
import {TrellisingObservables} from '../../../../common/trellising/store/observable/TrellisingObservables';
import {TrellisingDispatcher} from '../../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {BIOMARKERS_X_MAX, BIOMARKERS_Y_MAX, BiomarkerSettings} from '../../../../common/trellising/store/ITrellising';

@Component({
    selector: 'biomarker-settings',
    templateUrl: './BiomarkerSettingsComponent.html',
    styleUrls: ['../../../filters.css']
})
export class BiomarkerSettingsComponent {
    settingsOpenedModel: any = {};
    maxX = BIOMARKERS_X_MAX;
    maxY = BIOMARKERS_Y_MAX;

    biomarkerSettings = BiomarkerSettings;

    constructor(public trellisingObservables: TrellisingObservables,
                public trellisingDispatcher: TrellisingDispatcher) {
        this.closeAllSettings();
    }

    changeSettingVisibility(settingName: string) {
        this.settingsOpenedModel[settingName] = !this.settingsOpenedModel[settingName];
    }

    private closeAllSettings(): void {
        Object.keys(this.biomarkerSettings).forEach(setting => this.settingsOpenedModel[setting] = false);
    }
}
