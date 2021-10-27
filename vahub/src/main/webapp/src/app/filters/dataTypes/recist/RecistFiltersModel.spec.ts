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

import {TestBed, inject} from '@angular/core/testing';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {HttpClient} from '@angular/common/http';
import {Store, StoreModule} from '@ngrx/store';

import {RecistFiltersModel} from './RecistFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';
import {MockFilterEventService, MockDatasetViews} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';
import {ApplicationState} from '../../../common/store/models/ApplicationState';
import {sharedStateReducer} from '../../../common/store/reducers/SharedStateReducer';
import {trellisingReducer} from '../../../common/trellising/store/reducer/TrellisingReducer';

class MockFilterHttpService {
}

describe('GIVEN RecistFiltersModel', () => {

    let store: Store<ApplicationState>;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule,
                StoreModule.forRoot({trellisingReducer: trellisingReducer, sharedStateReducer: sharedStateReducer})],
            providers: [
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                HttpClient,
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useValue: new MockFilterEventService()},
                {
                    provide: RecistFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService, e: FilterEventService,
                                 d: DatasetViews, s: Store<ApplicationState>): RecistFiltersModel =>
                        new RecistFiltersModel(p, f, e, d, s),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews, Store]
                }
            ]
        });
    });

    beforeEach(inject([Store],
        (_store: Store<ApplicationState>) => {
            store = _store;
        }));

    describe('WHEN getName is called', () => {
        it('THEN tumour filter name is returned',
            inject([RecistFiltersModel], (model: RecistFiltersModel) => {
                expect(model.getName()).toEqual('tumour');
            })
        );
    });

    describe('WHEN checking if visible', () => {
        let datasetViews, recistFiltersModel;

        beforeEach(inject([DatasetViews, RecistFiltersModel], (_datasetViews: DatasetViews, _recistFiltersModel: RecistFiltersModel) => {
            datasetViews = _datasetViews;
            recistFiltersModel = _recistFiltersModel;
        }));

        describe('AND datasetViews have no data', () => {
            it('THEN should not be visible', () => {
                spyOn(datasetViews, 'hasTumourResponseData').and.returnValue(false);
                expect(recistFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasTumourResponseData').and.returnValue(true);
                expect(recistFiltersModel.isVisible()).toBeTruthy();
            });
        });

    });
});
