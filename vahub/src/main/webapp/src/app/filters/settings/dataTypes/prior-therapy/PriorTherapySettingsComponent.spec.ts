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

import {async, TestBed} from '@angular/core/testing';
import {FormsModule} from '@angular/forms';
import {Map} from 'immutable';

import {MockFilterModel, MockStore, MockTrellisingDispatcher} from '../../../../common/MockClasses';
import {TrellisingDispatcher} from '../../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {TumourResponseFiltersModel} from '../../../dataTypes/tumourresponse/TumourResponseFiltersModel';
import {RadioButtonsSettingsComponent} from '../../radioButtons/RadioButtonsSettingsComponent';
import {CapitalizePipe, SentenceCasePipe, SettingsPipe} from '../../../../common/pipes';
import {PriorTherapySettingsComponent} from './PriorTherapySettingsComponent';
import {Store} from '@ngrx/store';
import {PlotSettings} from '../../../../common/trellising/store';

describe('Given a control component', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [FormsModule],
            providers: [
                {provide: TumourResponseFiltersModel, useClass: MockFilterModel},
                {provide: TrellisingDispatcher, useClass: MockTrellisingDispatcher},
                {provide: Store, useClass: MockStore}
            ],
            declarations: [PriorTherapySettingsComponent, RadioButtonsSettingsComponent,
                CapitalizePipe, SentenceCasePipe, SettingsPipe]
        });
    });

    describe('WHEN therapies selection setting is applied', () => {
        it('THEN trellising is updated correctly',
            async(() => {
                TestBed.compileComponents().then(() => {
                    const component = TestBed.createComponent(PriorTherapySettingsComponent);
                    spyOn(component.componentInstance.trellisingDispatcher, 'updateZoom');
                    spyOn(component.componentInstance.trellisingDispatcher, 'updatePlotSettings');

                    component.componentInstance.therapiesOptions = ['FIRST_TRELLISING', 'SECOND_TRELLISING'];
                    const selectedTrellising = Map({
                        trellisedBy: 'FIRST_TRELLISING',
                        category: 'NON_MANDATORY_SERIES',
                        trellisOptions: ['d', 'e', 'f']
                    }) as PlotSettings;
                    component.componentInstance.selectedTrellising = selectedTrellising;
                    component.componentInstance.apply();

                    expect(component.componentInstance.trellisingDispatcher.updateZoom).toHaveBeenCalled();
                    expect(component.componentInstance.trellisingDispatcher.updatePlotSettings).toHaveBeenCalled();
                });
            }));
    });

});
