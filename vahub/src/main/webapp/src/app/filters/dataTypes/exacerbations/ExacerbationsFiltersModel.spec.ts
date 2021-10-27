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
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {HttpClient} from '@angular/common/http';

import {ExacerbationsFiltersModel} from './ExacerbationsFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {CheckListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {SessionEventService} from '../../../session/event/SessionEventService';
import {SessionHttpService} from '../../../session/http/SessionHttpService';
import {MockDatasetViews, MockFilterEventService} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a ExacerbationsFiltersModel class', () => {
    let exacerbationsFiltersModel;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                HttpClient,
                SessionHttpService,
                SessionEventService,
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useValue: new MockFilterEventService()},
                {
                    provide: ExacerbationsFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService, e: FilterEventService,
                                 d: DatasetViews): ExacerbationsFiltersModel =>
                        new ExacerbationsFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    beforeEach(inject([ExacerbationsFiltersModel], (_exacerbationsFiltersModel: ExacerbationsFiltersModel) => {
        exacerbationsFiltersModel = _exacerbationsFiltersModel;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {
            expect(exacerbationsFiltersModel.itemsModels.length).toBe(14);

            expect((<CheckListFilterItemModel>exacerbationsFiltersModel.itemsModels[0]).availableValues).toEqual([]);
            expect((<CheckListFilterItemModel>exacerbationsFiltersModel.itemsModels[0]).selectedValues).toEqual([]);
            expect((<CheckListFilterItemModel>exacerbationsFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);
        });
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name', () => {

            expect(exacerbationsFiltersModel.getName()).toEqual('exacerbation');
        });
    });

    describe('WHEN emitEvents', () => {
        let filterEventService: FilterEventService;
        let validator: any;

        beforeEach(inject([FilterEventService], (_filterEventService_) => {
            filterEventService = _filterEventService_;

            filterEventService.exacerbationsFilter.subscribe(
                (object: any) => {
                    validator.called(object);
                }
            );
            validator = jasmine.createSpyObj('validator', ['called']);
        }));

        it('SHOULD be set filterEventService.setExacerbationsFilter', inject([ExacerbationsFiltersModel],
            (exacerbationsFiltersMod: ExacerbationsFiltersModel) => {

                exacerbationsFiltersMod.emitEvent({});

                expect(validator.called).toHaveBeenCalledWith({});
            }));
    });

    describe('WHEN transforming model from server', () => {

        it('SHOULD be set correctly', () => {

            exacerbationsFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                exacerbationClassification: {
                    values: ['cls-1', 'cls-2']
                }
            });

            expect((<CheckListFilterItemModel>exacerbationsFiltersModel.itemsModels[0]).availableValues).toEqual(['cls-1', 'cls-2']);
            expect((<CheckListFilterItemModel>exacerbationsFiltersModel.itemsModels[0]).selectedValues).toEqual(['cls-1', 'cls-2']);
            expect((<CheckListFilterItemModel>exacerbationsFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be set correctly', () => {

            exacerbationsFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                exacerbationClassification: {
                    values: ['cls-1', 'cls-2']
                }
            });

            (<CheckListFilterItemModel>exacerbationsFiltersModel.itemsModels[0]).selectedValues = ['cls-1'];
            (<CheckListFilterItemModel>exacerbationsFiltersModel.itemsModels[0]).appliedSelectedValues = ['cls-1'];

            const serverObject = exacerbationsFiltersModel.transformFiltersToServer();

            expect(serverObject.exacerbationClassification.values).toEqual(['cls-1']);
        });
    });

    describe('WHEN resetting model', () => {

        it('SHOULD be reset correctly', () => {

            exacerbationsFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                exacerbationClassification: {
                    values: ['cls-1', 'cls-2']
                }
            });

            exacerbationsFiltersModel.reset();

            const serverObject = exacerbationsFiltersModel.transformFiltersToServer();

            expect(serverObject).toEqual({});
        });
    });

    describe('WHEN checking if visible', () => {
        let datasetViews;

        beforeEach(inject([DatasetViews], (_datasetViews: DatasetViews) => {
            datasetViews = _datasetViews;
        }));

        describe('AND datasetViews have no data', () => {
            it('THEN should not be visible', () => {
                spyOn(datasetViews, 'hasExacerbationsData').and.returnValue(false);
                expect(exacerbationsFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasExacerbationsData').and.returnValue(true);
                expect(exacerbationsFiltersModel.isVisible()).toBeTruthy();
            });
        });
    });
});
