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
import {Store} from '@ngrx/store';
import {Observable} from 'rxjs/Observable';
import {ApplicationState} from '../../../../common/store/models/ApplicationState';
import {getCurrentPlotSettings} from '../../../../common/trellising/store/reducer/TrellisingReducer';
import {PlotSettings, TherapiesType} from '../../../../common/trellising/store/ITrellising';
import {TrellisingDispatcher} from '../../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {TumourResponseFiltersModel} from '../../../dataTypes/tumourresponse/TumourResponseFiltersModel';


@Component({
    selector: 'prior-therapy-settings',
    templateUrl: 'PriorTherapySettingsComponent.html',
    styleUrls: ['../../../filters.css']
})
export class PriorTherapySettingsComponent implements OnInit {
    // as for now we have only one setting, we will use one boolean variable to check if it setting is opened
    isOpen = false;
    currentPlotSettings$: Observable<any>;
    therapiesOptions = Object.keys(TherapiesType);
    selectedTrellising: PlotSettings;
    previouslySelectedTrellising: string;

    constructor(private _store: Store<ApplicationState>,
                public trellisingDispatcher: TrellisingDispatcher,
                public filtersModel: TumourResponseFiltersModel) {
        this.currentPlotSettings$ = this._store.select(getCurrentPlotSettings);
    }

    ngOnInit(): void {
        this.currentPlotSettings$.subscribe(ps => this.selectedTrellising = ps);
        this.previouslySelectedTrellising = this.selectedTrellising.get('trellisedBy');
    }

    setTherapies(events: any): void {
        this.selectedTrellising = this.selectedTrellising.setIn(['trellisedBy'], events.selectedValue) as PlotSettings;
        // we need to update filters after this setting change, so plot will be updated because of the filters change
    }

    apply() {
        if (this.previouslySelectedTrellising !== this.selectedTrellising.get('trellisedBy')) {
            this.trellisingDispatcher.updatePlotSettings(this.selectedTrellising);
            this.trellisingDispatcher.updateZoom();
            this.filtersModel.getFilters(true);
            this.previouslySelectedTrellising = this.selectedTrellising.get('trellisedBy');
        }
    }
}
