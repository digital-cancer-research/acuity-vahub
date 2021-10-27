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
import {Map} from 'immutable';

import {AggregationType, ErrorBarsType} from '../../../../common/trellising/store';
import {TrellisingDispatcher} from '../../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {ExposureSettings, PlotSettings, ScaleTypes} from '../../../../common/trellising/store/ITrellising';
import {TrellisingObservables} from '../../../../common/trellising/store/observable/TrellisingObservables';
import {Trellising} from '../../../../common/trellising/store/Trellising';
import {StudyService} from '../../../../common/StudyService';


@Component({
    selector: 'exposure-settings',
    templateUrl: 'ExposureSettingsComponent.html',
    styleUrls: ['../../../filters.css']
})
export class ExposureSettingsComponent implements OnInit {
    aggregationTypes: string[];

    settingsOpenedModel: any = {};
    exposureSettings = ExposureSettings;

    selectedAggregation: string;
    previousSelectedAggregation: string;

    selectedScale: ScaleTypes;
    previousSelectedScale: ScaleTypes;

    selectedErrorBars: Map<string, boolean>;
    previousSelectedErrorBars: Map<string, boolean>;

    plotSettings: PlotSettings;
    selectedPlotSettings: PlotSettings;
    AggregationType = AggregationType;

    constructor(public trellisingObservables: TrellisingObservables,
                public trellisingDispatcher: TrellisingDispatcher,
                public trellisingMiddleware: Trellising,
                private studyService: StudyService) {
    }

    ngOnInit(): void {
        this.trellisingObservables.scaleType.subscribe(s => this.selectedScale = s);
        this.trellisingObservables.plotSettings.subscribe(ps => this.plotSettings = ps);
        this.previousSelectedScale = this.selectedScale;
        this.previousSelectedAggregation = this.plotSettings.get('trellisedBy');
        this.selectedAggregation = this.plotSettings.get('trellisedBy');
        this.selectedPlotSettings = this.plotSettings;
        this.selectedErrorBars = this.plotSettings.get('errorBars');
        this.previousSelectedErrorBars = this.selectedErrorBars;
        this.aggregationTypes = this.studyService.metadataInfo['exposure']['aggregationTypes'];
    }

    setSelectedAggregation(event: any) {
        this.selectedPlotSettings = this.selectedPlotSettings.setIn(['trellisedBy'], event.selectedValue) as PlotSettings;
        this.selectedAggregation = event.selectedValue;
        if (this.selectedAggregation !== AggregationType.SUBJECT_CYCLE) {
            this.setDefaultErrorBarSetting();
        }
    }

    setSelectedScale(event: any): void {
        this.selectedScale = event.selectedValue;
    }

    changeSelectedBar(event: any): void {
        this.selectedErrorBars = this.selectedErrorBars.set(event.name, event.selected);
        this.selectedPlotSettings = this.selectedPlotSettings.setIn(['errorBars'], this.selectedErrorBars) as PlotSettings;
    }

    changeSettingVisibility(settingName: string) {
        this.settingsOpenedModel[settingName] = !this.settingsOpenedModel[settingName];
    }

    apply() {
        if (this.selectedScale !== this.previousSelectedScale) {
            this.trellisingDispatcher.updateScale(this.selectedScale);
            this.previousSelectedScale = this.selectedScale;
        }
        const selectedAggregation = this.selectedPlotSettings.get('trellisedBy');
        if (selectedAggregation !== this.previousSelectedAggregation) {
            const isLegendDisabled = this.previousSelectedAggregation === AggregationType.ANALYTE;
            this.previousSelectedAggregation = selectedAggregation;
            this.trellisingMiddleware.updatePlotSettings(this.selectedPlotSettings);
            if (this.selectedPlotSettings.get('trellisedBy') === AggregationType.ANALYTE) {
                this.trellisingDispatcher.updateLegendDisabled(true);
            } else if (isLegendDisabled) {
                this.trellisingDispatcher.updateLegendDisabled(false);
            }
        }
        if (this.selectedErrorBars && !this.selectedErrorBars.equals(this.previousSelectedErrorBars)) {
            this.previousSelectedErrorBars = this.selectedErrorBars;
            this.trellisingMiddleware.updatePlotSettings(this.selectedPlotSettings);
        }
    }

    setDefaultErrorBarSetting(): void {
        this.changeSelectedBar({name: ErrorBarsType.STANDARD_DEVIATION, selected: true});
    }
}
