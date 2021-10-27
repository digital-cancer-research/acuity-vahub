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

import {inject, TestBed} from '@angular/core/testing';
import {Http, HttpModule} from '@angular/http';

import {LungFunctionFiltersModel} from './LungFunctionFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {ListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {SessionEventService} from '../../../session/event/SessionEventService';
import {SessionHttpService} from '../../../session/http/SessionHttpService';
import {MockDatasetViews, MockFilterEventService} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a LungFunctionFiltersModel class', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpModule],
            providers: [
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                Http,
                {provide: DatasetViews, useClass: MockDatasetViews},
                SessionHttpService,
                SessionEventService,
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useValue: new MockFilterEventService()},
                {
                    provide: LungFunctionFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService, e: FilterEventService, d: DatasetViews): LungFunctionFiltersModel =>
                        new LungFunctionFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', inject([LungFunctionFiltersModel], (lungfunctionFiltersModel: LungFunctionFiltersModel) => {
            expect(lungfunctionFiltersModel.itemsModels.length).toBe(11);

            expect((<ListFilterItemModel>lungfunctionFiltersModel.itemsModels[0]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>lungfunctionFiltersModel.itemsModels[0]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>lungfunctionFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);
        }));
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', inject([LungFunctionFiltersModel], (lungfunctionFiltersModel: LungFunctionFiltersModel) => {

            expect(lungfunctionFiltersModel.transformFiltersToServer()).toEqual({});
        }));
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name labs', inject([LungFunctionFiltersModel], (lungfunctionFiltersModel: LungFunctionFiltersModel) => {

            expect(lungfunctionFiltersModel.getName()).toEqual('lungFunction');
        }));
    });

    describe('WHEN emitEvents', () => {
        let filterEventService: FilterEventService;
        let validator: any;

        beforeEach(inject([FilterEventService], (_filterEventService_) => {
            filterEventService = _filterEventService_;

            filterEventService.lungFunctionFilter.subscribe(
                (object: any) => {
                    validator.called(object);
                }
            );
            validator = jasmine.createSpyObj('validator', ['called']);
        }));

        it('SHOULD be set filterEventService.setLungFunctionFilter', inject([LungFunctionFiltersModel], (lungfunctionFiltersModel: LungFunctionFiltersModel) => {

            lungfunctionFiltersModel.emitEvent({});

            expect(validator.called).toHaveBeenCalledWith({});
        }));
    });

    describe('WHEN transforming model from server', () => {

        it('SHOULD be set correctly', inject([LungFunctionFiltersModel], (lungfunctionFiltersModel: LungFunctionFiltersModel) => {

            lungfunctionFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                measurementName: {
                    values: ['lab1', 'lab2']
                }
            });

            expect((<ListFilterItemModel>lungfunctionFiltersModel.itemsModels[0]).availableValues).toEqual(['lab1', 'lab2']);
            expect((<ListFilterItemModel>lungfunctionFiltersModel.itemsModels[0]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>lungfunctionFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);
        }));
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be set correctly', inject([LungFunctionFiltersModel], (lungfunctionFiltersModel: LungFunctionFiltersModel) => {

            lungfunctionFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                measurementName: {
                    values: ['lab1', 'lab2']
                }
            });

            (<ListFilterItemModel>lungfunctionFiltersModel.itemsModels[0]).selectedValues = ['lab1', 'lab2'];
            (<ListFilterItemModel>lungfunctionFiltersModel.itemsModels[0]).appliedSelectedValues = ['lab1', 'lab2'];

            const serverObject = lungfunctionFiltersModel.transformFiltersToServer();

            expect(serverObject.measurementName.values).toEqual(['lab1', 'lab2']);
        }));
    });

    describe('WHEN resetting model', () => {

        it('SHOULD be reset correctly', inject([LungFunctionFiltersModel], (lungfunctionFiltersModel: LungFunctionFiltersModel) => {

            lungfunctionFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                labcode: {
                    values: ['lab1', 'lab2']
                }
            });

            lungfunctionFiltersModel.reset();

            const serverObject = lungfunctionFiltersModel.transformFiltersToServer();

            expect(serverObject).toEqual({});
        }));
    });

    describe('WHEN checking if visible', () => {
        let datasetViews, lungFunctionFiltersModel;

        beforeEach(inject([DatasetViews, LungFunctionFiltersModel], (_datasetViews: DatasetViews, _lungFunctionFiltersModel: LungFunctionFiltersModel) => {
            datasetViews = _datasetViews;
            lungFunctionFiltersModel = _lungFunctionFiltersModel;
        }));

        describe('AND datasetViews have no data', () => {
            it('THEN should not be visible', () => {
                spyOn(datasetViews, 'hasRespiratryData').and.returnValue(false);
                expect(lungFunctionFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasRespiratryData').and.returnValue(true);
                expect(lungFunctionFiltersModel.isVisible()).toBeTruthy();
            });
        });

    });

});
