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

import {ConmedsFiltersModel} from './ConmedsFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {ListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {MockDatasetViews, MockFilterEventService, MockHttpClient} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {HttpClient} from '@angular/common/http';

class MockFilterHttpService {
}

describe('GIVEN a ConmedsFiltersModel class', () => {
    let conmedsFiltersModel;

    // beforeEach(() => {
    //     TestBed.configureTestingModule({
    //         imports: [HttpModule],
    //         providers: [
    //             {provide: DatasetViews, useClass: MockDatasetViews},
    //             {provide: XHRBackend, useClass: MockBackend},
    //             {provide: FilterHttpService, useClass: MockFilterHttpService},
    //             {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
    //             {provide: FilterEventService, useClass: MockFilterEventService},
    //             {
    //                 provide: ConmedsFiltersModel,
    //                 useFactory: (p: PopulationFiltersModel, f: FilterHttpService, e: FilterEventService, d: DatasetViews):
    //                 ConmedsFiltersModel =>
    //                     new ConmedsFiltersModel(p, f, e, d),
    //                 deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
    //             }
    //         ]
    //     });
    // });

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: HttpClient, useClass: MockHttpClient},
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: ConmedsFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService, e: FilterEventService, d: DatasetViews):
                        ConmedsFiltersModel => new ConmedsFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                },
            ]
        });
    });

    beforeEach(inject([ConmedsFiltersModel], (_conmedsFiltersModel: ConmedsFiltersModel) => {
        conmedsFiltersModel = _conmedsFiltersModel;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {

            expect(conmedsFiltersModel.itemsModels.length).toBe(15);

            expect((<ListFilterItemModel>conmedsFiltersModel.itemsModels[0]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>conmedsFiltersModel.itemsModels[0]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>conmedsFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);

            expect((<ListFilterItemModel>conmedsFiltersModel.itemsModels[1]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>conmedsFiltersModel.itemsModels[1]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>conmedsFiltersModel.itemsModels[1]).includeEmptyValues).toEqual(true);

            expect((<ListFilterItemModel>conmedsFiltersModel.itemsModels[3]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>conmedsFiltersModel.itemsModels[3]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>conmedsFiltersModel.itemsModels[3]).includeEmptyValues).toEqual(true);

            expect(conmedsFiltersModel.datasetViews).toBeDefined();
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', inject([ConmedsFiltersModel], (model: ConmedsFiltersModel) => {
            expect(model.transformFiltersToServer()).toEqual({});
        }));
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name conmeds', inject([ConmedsFiltersModel], (model: ConmedsFiltersModel) => {
            expect(model.getName()).toEqual('conmeds');
        }));
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setConmedsFilter',
            inject([FilterEventService, ConmedsFiltersModel],
                (filterEventService: FilterEventService, model: ConmedsFiltersModel) => {
            const validator = jasmine.createSpyObj('validator', ['called']);

            filterEventService.conmedsFilter.subscribe(
                (object: any) => {
                    validator.called(object);
                }
            );

            model.emitEvent({});

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
                spyOn(datasetViews, 'hasConmedsData').and.returnValue(false);
                expect(conmedsFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasConmedsData').and.returnValue(true);
                expect(conmedsFiltersModel.isVisible()).toBeTruthy();
            });
        });

    });
});
