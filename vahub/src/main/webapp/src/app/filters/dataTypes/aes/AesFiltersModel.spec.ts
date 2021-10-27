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

import {AesFiltersModel} from './AesFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {CheckListFilterItemModel, ListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {MockDatasetViews, MockFilterEventService, MockHttpClient} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a AesFiltersModel class', () => {
    let aesFiltersModel;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: HttpClient, useClass: MockHttpClient},
                {
                    provide: DatasetViews, useClass: MockDatasetViews
                },
                {provide: FilterHttpService, useValue: new MockFilterHttpService()},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: AesFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService,
                                 e: FilterEventService, d: DatasetViews): AesFiltersModel =>
                        new AesFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    beforeEach(inject([AesFiltersModel], (_aesFiltersModel: AesFiltersModel) => {
        aesFiltersModel = _aesFiltersModel;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {
            expect(aesFiltersModel.itemsModels.length).toBe(34);

            expect((<CheckListFilterItemModel>aesFiltersModel.itemsModels[12]).availableValues).toEqual([]);
            expect((<CheckListFilterItemModel>aesFiltersModel.itemsModels[12]).selectedValues).toEqual([]);
            expect((<CheckListFilterItemModel>aesFiltersModel.itemsModels[12]).initialValues).toEqual([]);
            expect((<CheckListFilterItemModel>aesFiltersModel.itemsModels[12]).includeEmptyValues).toEqual(true);

            expect((<ListFilterItemModel>aesFiltersModel.itemsModels[1]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>aesFiltersModel.itemsModels[1]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>aesFiltersModel.itemsModels[1]).includeEmptyValues).toEqual(true);

            expect((<ListFilterItemModel>aesFiltersModel.itemsModels[0]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>aesFiltersModel.itemsModels[0]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>aesFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', () => {

            expect(aesFiltersModel.transformFiltersToServer()).toEqual({});
        });
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name aes', () => {

            expect(aesFiltersModel.getName()).toEqual('aes');
        });
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setAesFilter',
            inject([FilterEventService], (filterEventService: FilterEventService) => {
                const validator = jasmine.createSpyObj('validator', ['called']);
                filterEventService.aesFilter.subscribe(
                    (object: any) => {
                        validator.called(object);
                    }
                );
                aesFiltersModel.emitEvent({});

                expect(validator.called).toHaveBeenCalledWith({});
            }));
    });

    describe('WHEN transforming model from server', () => {

        it('SHOULD be set correctly', () => {

            aesFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                pt: {
                    values: ['PT1']
                },
                serious: {
                    values: ['serious1', 'serious2']
                }
            });

            expect((<CheckListFilterItemModel>aesFiltersModel.itemsModels[12]).availableValues).toEqual(['serious1', 'serious2']);
            expect((<CheckListFilterItemModel>aesFiltersModel.itemsModels[12]).selectedValues).toEqual(['serious1', 'serious2']);
            expect((<CheckListFilterItemModel>aesFiltersModel.itemsModels[12]).initialValues).toEqual(['serious1', 'serious2']);
            expect((<CheckListFilterItemModel>aesFiltersModel.itemsModels[12]).includeEmptyValues).toEqual(true);

            expect((<ListFilterItemModel>aesFiltersModel.itemsModels[1]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>aesFiltersModel.itemsModels[1]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>aesFiltersModel.itemsModels[1]).includeEmptyValues).toEqual(true);

            expect((<ListFilterItemModel>aesFiltersModel.itemsModels[0]).availableValues).toEqual(['PT1']);
            expect((<ListFilterItemModel>aesFiltersModel.itemsModels[0]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>aesFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be set correctly', () => {

            aesFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                pt: {
                    values: ['PT1', 'PT2']
                },
                serious: {
                    values: ['serious1', 'serious2']
                }
            });

            (<CheckListFilterItemModel>aesFiltersModel.itemsModels[12]).selectedValues = ['serious1'];
            (<CheckListFilterItemModel>aesFiltersModel.itemsModels[12]).appliedSelectedValues = ['serious1'];
            (<ListFilterItemModel>aesFiltersModel.itemsModels[0]).selectedValues = ['PT1'];
            (<ListFilterItemModel>aesFiltersModel.itemsModels[0]).appliedSelectedValues = ['PT1'];

            const serverObject = aesFiltersModel.transformFiltersToServer();

            expect(serverObject.pt.values).toEqual(['PT1']);
            expect(serverObject.serious.values).toEqual(['serious1']);
        });
    });

    describe('WHEN resetting model', () => {

        it('SHOULD be reset correctly', () => {

            aesFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                pt: {
                    values: ['PT1', 'PT2']
                },
                serious: {
                    values: ['serious1', 'serious2']
                }
            });

            aesFiltersModel.reset();

            const serverObject = aesFiltersModel.transformFiltersToServer();

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
                spyOn(datasetViews, 'hasAesData').and.callFake(() => false);

                expect(aesFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasAesData').and.callFake(() => true);

                expect(aesFiltersModel.isVisible()).toBeTruthy();
            });
        });

    });
});
