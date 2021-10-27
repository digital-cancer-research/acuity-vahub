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
