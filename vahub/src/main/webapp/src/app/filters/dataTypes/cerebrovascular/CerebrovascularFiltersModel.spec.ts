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

import {CerebrovascularFiltersModel} from './CerebrovascularFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {CheckListFilterItemModel, ListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {MockFilterEventService, MockDatasetViews} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a CerebrovascularFiltersModel class', () => {
    let cerebrovascularFiltersModel;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                HttpClient,
                {
                    provide: DatasetViews, useClass: MockDatasetViews
                },
                {provide: FilterHttpService, useValue: new MockFilterHttpService()},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: CerebrovascularFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService, e: FilterEventService, d: DatasetViews): CerebrovascularFiltersModel =>
                        new CerebrovascularFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    beforeEach(inject([CerebrovascularFiltersModel], (_cerebrovascularFiltersModel: CerebrovascularFiltersModel) => {
        cerebrovascularFiltersModel = _cerebrovascularFiltersModel;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {
            expect(cerebrovascularFiltersModel.itemsModels.length).toBe(13);

            expect((<CheckListFilterItemModel>cerebrovascularFiltersModel.itemsModels[0]).availableValues).toEqual([]);
            expect((<CheckListFilterItemModel>cerebrovascularFiltersModel.itemsModels[0]).selectedValues).toEqual([]);
            expect((<CheckListFilterItemModel>cerebrovascularFiltersModel.itemsModels[0]).initialValues).toEqual([]);
            expect((<CheckListFilterItemModel>cerebrovascularFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);

            expect((<ListFilterItemModel>cerebrovascularFiltersModel.itemsModels[1]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>cerebrovascularFiltersModel.itemsModels[1]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>cerebrovascularFiltersModel.itemsModels[1]).includeEmptyValues).toEqual(true);
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', () => {

            expect(cerebrovascularFiltersModel.transformFiltersToServer()).toEqual({});
        });
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name cerebrovascular', () => {

            expect(cerebrovascularFiltersModel.getName()).toEqual('cerebrovascular');
        });
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setCerebrovascularFilter',
            inject([FilterEventService], (filterEventService: FilterEventService) => {
                const validator = jasmine.createSpyObj('validator', ['called']);
                filterEventService.cerebrovascularFilter.subscribe(
                    (object: any) => {
                        validator.called(object);
                    }
                );
                cerebrovascularFiltersModel.emitEvent({});

                expect(validator.called).toHaveBeenCalledWith({});
            }));
    });

    describe('WHEN checking if visible', () => {
        let datasetViews;

        beforeEach(inject([DatasetViews], (_datasetViews: DatasetViews) => {
            datasetViews = _datasetViews;
        }));

        describe('AND datasetViews have no data', () => {
            it('THEN should not be visible', () => {
                spyOn(datasetViews, 'hasCerebrovascularData').and.callFake(() => false);

                expect(cerebrovascularFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasCerebrovascularData').and.callFake(() => true);

                expect(cerebrovascularFiltersModel.isVisible()).toBeTruthy();
            });
        });

    });
});
