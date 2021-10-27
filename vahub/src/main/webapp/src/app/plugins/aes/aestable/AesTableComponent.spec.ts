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

import {HttpClientTestingModule} from '@angular/common/http/testing';
import {HttpClient} from '@angular/common/http';
import {ComponentFixture, ComponentFixtureAutoDetect, inject, TestBed} from '@angular/core/testing';
import {AgGridModule} from 'ag-grid-angular/main';

import {AesFiltersModel, FilterEventService, PopulationFiltersModel} from '../../../filters/module';
import {DropdownComponentModule, StudyService} from '../../../common/module';
import {
    MockFilterEventService,
    MockSessionEventService,
    MockStudyService,
    MockTimelineDispatcher,
    MockTrellisingDispatcher
} from '../../../common/MockClasses';
import {AesTableComponent} from './AesTableComponent';
import {AesTableDropdownModel} from './AesTableDropdownModel';
import {AesTableServiceCommunity} from './AesTableServiceCommunity';
import {AesTableHttpService} from '../../../data/aes';
import {TrellisingDispatcher} from '../../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {TimelineDispatcher} from '../../timeline/store/dispatcher/TimelineDispatcher';
import {ModalMessageComponentModule} from '../../../common/modals/modalMessage/ModalMessageComponent.module';
import {SessionEventService} from '../../../session/event/SessionEventService';

describe('GIVEN AesTableComponent', () => {

    let component: AesTableComponent;
    let fixture: ComponentFixture<AesTableComponent>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            declarations: [AesTableComponent],
            imports: [
                HttpClientTestingModule,
                DropdownComponentModule,
                ModalMessageComponentModule,
                AgGridModule.withComponents([
                    AesTableComponent
                ])
            ],
            providers: [
                AesTableDropdownModel,
                FilterEventService,
                HttpClient,
                {provide: SessionEventService, useClass: MockSessionEventService},
                {provide: ComponentFixtureAutoDetect, useValue: true},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {provide: TrellisingDispatcher, useClass: MockTrellisingDispatcher},
                {provide: TimelineDispatcher, useClass: MockTimelineDispatcher},
                {provide: StudyService, useClass: MockStudyService},
                {provide: PopulationFiltersModel, useClass: PopulationFiltersModel, deps: [FilterEventService]},
                {provide: AesFiltersModel, useClass: AesFiltersModel, deps: [FilterEventService]},
                {
                    provide: AesTableHttpService,
                    useClass: AesTableHttpService,
                    deps: [HttpClient, PopulationFiltersModel, AesFiltersModel]
                },
                AesTableServiceCommunity
            ]
        });

        fixture = TestBed.createComponent(AesTableComponent);
        component = fixture.componentInstance;
    });

    describe('WHEN the component is loaded', () => {
        it('THEN data is requested from the server', () => {
            spyOn(component, 'refreshDataFromServer');

            component.onTableInitialised();

            expect(component['refreshDataFromServer']).toHaveBeenCalled();
        });
    });

    describe('WHEN the filters change', () => {
        it('THEN data is requested from the server', inject([FilterEventService], (filterEventService: FilterEventService) => {
            spyOn(component, 'refreshDataFromServer');

            component.ngOnInit();
            filterEventService.setAesFilter({});

            expect(component['refreshDataFromServer']).toHaveBeenCalled();
        }));
    });

    describe('WHEN the AE Level dropdown is changed', () => {
        it('THEN data is requested from the server', () => {
            spyOn(component, 'refreshDataFromServer');

            component.ngOnInit();
            component.aeLevelClicked({displayName: 'PT', serverName: 'PT'});

            expect(component['refreshDataFromServer']).toHaveBeenCalled();
        });
    });
});
