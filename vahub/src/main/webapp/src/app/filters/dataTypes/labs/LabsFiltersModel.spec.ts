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

import {LabsFiltersModel} from './LabsFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {ListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {SessionEventService} from '../../../session/event/SessionEventService';
import {MockDatasetViews, MockFilterEventService} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a LabsFiltersModel class', () => {
    let labsFiltersModel;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                SessionEventService,
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: FilterHttpService, useValue: new MockFilterHttpService()},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: LabsFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService,
                                 e: FilterEventService, d: DatasetViews): LabsFiltersModel =>
                        new LabsFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    beforeEach(inject([LabsFiltersModel], (_labsFiltersModel: LabsFiltersModel) => {
        labsFiltersModel = _labsFiltersModel;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {
            expect(labsFiltersModel.itemsModels.length).toBe(22);

            expect((<ListFilterItemModel>labsFiltersModel.itemsModels[1]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>labsFiltersModel.itemsModels[1]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>labsFiltersModel.itemsModels[1]).includeEmptyValues).toEqual(true);
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', () => {

            expect(labsFiltersModel.transformFiltersToServer()).toEqual({});
        });
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name labs', () => {

            expect(labsFiltersModel.getName()).toEqual('labs');
        });
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setLabsFilter',
            inject([FilterEventService], (filterEventService: FilterEventService) => {
                const validator = jasmine.createSpyObj('validator', ['called']);

                filterEventService.labsFilter.subscribe(
                    (object: any) => {
                        validator.called(object);
                    }
                );
                labsFiltersModel.emitEvent({});

                expect(validator.called).toHaveBeenCalledWith({});
            })
        );
    });

    describe('WHEN transforming model from server', () => {

        it('SHOULD be set correctly', () => {

            labsFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                labcode: {
                    values: ['lab1', 'lab2']
                }
            });

            expect((<ListFilterItemModel>labsFiltersModel.itemsModels[1]).availableValues).toEqual(['lab1', 'lab2']);
            expect((<ListFilterItemModel>labsFiltersModel.itemsModels[1]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>labsFiltersModel.itemsModels[1]).includeEmptyValues).toEqual(true);
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be set correctly', () => {

            labsFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                labcode: {
                    values: ['lab1', 'lab2']
                }
            });

            (<ListFilterItemModel>labsFiltersModel.itemsModels[1]).selectedValues = ['lab1', 'lab2'];
            (<ListFilterItemModel>labsFiltersModel.itemsModels[1]).appliedSelectedValues = ['lab1', 'lab2'];

            const serverObject = labsFiltersModel.transformFiltersToServer();

            expect(serverObject.labcode.values).toEqual(['lab1', 'lab2']);
        });
    });

    describe('WHEN resetting model', () => {

        it('SHOULD be reset correctly', () => {

            labsFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                labcode: {
                    values: ['lab1', 'lab2']
                }
            });

            labsFiltersModel.reset();

            const serverObject = labsFiltersModel.transformFiltersToServer();

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
                spyOn(datasetViews, 'hasLabsData').and.returnValue(false);
                expect(labsFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasLabsData').and.returnValue(true);
                expect(labsFiltersModel.isVisible()).toBeTruthy();
            });
        });
    });
});
