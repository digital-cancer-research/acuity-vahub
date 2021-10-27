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

import {Component, OnInit} from '@angular/core';
import {AesChordSettings, TermLevelType} from '../../../../common/trellising/store';
import {TrellisingObservables} from '../../../../common/trellising/store/observable/TrellisingObservables';
import {Trellising} from '../../../../common/trellising/store/Trellising';
import {TrellisingDispatcher} from '../../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {PlotSettings, TrellisCategory} from '../../../../common/trellising/store/ITrellising';

@Component({
    selector: 'aes-chord-settings',
    templateUrl: 'AesChordSettingsComponent.html',
    styleUrls: ['../../../filters.css']
})
export class AesChordSettingsComponent implements OnInit {
    minDays = 0;
    maxDays = 30;
    settingsOpenedModel: any = {};
    aesChordSettings = AesChordSettings;

    termLevelTypes = Object.keys(TermLevelType);
    previousSelectedTermLevel: string;

    previousTimeFrame: number;

    plotSettings: PlotSettings;

    minPercentageOfLinks = 0;
    maxPercentageOfLinks = 100;

    previousPercentageOfLinks: number;

    constructor(public trellisingObservables: TrellisingObservables,
                public trellisingDispatcher: TrellisingDispatcher,
                public trellisingMiddleware: Trellising) {
    }

    ngOnInit(): void {
        this.trellisingObservables.plotSettings.subscribe(ps => this.plotSettings = ps);
        this.previousSelectedTermLevel = this.plotSettings.get('trellisedBy');
        this.previousTimeFrame = this.plotSettings.getIn(['trellisOptions', 'timeFrame']);
        this.previousPercentageOfLinks = this.plotSettings.getIn(['trellisOptions', 'percentageOfLinks']);
    }

    changeSettingVisibility(settingName: string) {
        this.settingsOpenedModel[settingName] = !this.settingsOpenedModel[settingName];
    }
    setTermLevel(event: any): void {
        this.plotSettings = this.plotSettings.setIn(['trellisedBy'], event.selectedValue) as PlotSettings;
    }

    setDaysNumberBetweenAEs(event: any): void {
        this.plotSettings = this.plotSettings.setIn(['trellisOptions', 'timeFrame'], event) as PlotSettings;
    }

    setNumberOfLinks(event: any): void {
        this.plotSettings = this.plotSettings.setIn(['trellisOptions', 'percentageOfLinks'], event) as PlotSettings;
    }

    apply() {
        const selectedTermLvl = this.plotSettings.get('trellisedBy');
        const timeFrame = this.plotSettings.getIn(['trellisOptions', 'timeFrame']);
        const percentageOfLinks = this.plotSettings.getIn(['trellisOptions', 'percentageOfLinks']);

        let changed = false;
        if (timeFrame !== this.previousTimeFrame) {
            changed = true;
            this.previousTimeFrame = timeFrame;
        }
        if (percentageOfLinks !== this.previousPercentageOfLinks) {
            changed = true;
            this.previousPercentageOfLinks = percentageOfLinks;
        }
        if (changed) {
            this.trellisingMiddleware.updatePlotSettings(this.plotSettings);
        } else if (selectedTermLvl !== this.previousSelectedTermLevel) {
            // we do not need to make request to server here, as we are getting information for all term level settings at once
            this.trellisingDispatcher.updatePlotSettings(this.plotSettings);
            // TODO <any> was used to set 'category' field (it's absent in backend-based TrellisOptions class.
            // TODO please remove it when another class will be used instead
            this.trellisingDispatcher.updateTrellisColorBy([<any>{
                trellisedBy: selectedTermLvl,
                trellisOptions: [],
                category: TrellisCategory.NON_MANDATORY_SERIES
            }]);
            this.trellisingDispatcher.clearSelection();
        }
        if (selectedTermLvl !== this.previousSelectedTermLevel) {
            this.previousSelectedTermLevel = selectedTermLvl;
        }
    }
}
