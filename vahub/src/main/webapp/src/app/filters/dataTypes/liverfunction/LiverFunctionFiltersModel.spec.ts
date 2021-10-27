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

import {LiverFunctionFiltersModel} from './LiverFunctionFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {RangeDateFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {SessionEventService} from '../../../session/event/SessionEventService';
import {SessionHttpService} from '../../../session/http/SessionHttpService';
import {MockDatasetViews, MockFilterEventService} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a LiverFunctionFiltersModel class', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                SessionHttpService,
                SessionEventService,
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {
                    provide: LiverFunctionFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService,
                                 e: FilterEventService, d: DatasetViews): LiverFunctionFiltersModel =>
                        new LiverFunctionFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', inject([LiverFunctionFiltersModel], (liverFunctionFiltersModel: LiverFunctionFiltersModel) => {
            expect(liverFunctionFiltersModel.itemsModels.length).toBe(13);
        }));
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', inject([LiverFunctionFiltersModel], (liverFunctionFiltersModel: LiverFunctionFiltersModel) => {

            expect(liverFunctionFiltersModel.transformFiltersToServer()).toEqual({});
        }));
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name labs', inject([LiverFunctionFiltersModel], (liverFunctionFiltersModel: LiverFunctionFiltersModel) => {

            expect(liverFunctionFiltersModel.getName()).toEqual('liver');
        }));
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setLiverFunctionFilter',
            inject([FilterEventService, LiverFunctionFiltersModel],
                (filterEventService: FilterEventService, liverFunctionFiltersModel: LiverFunctionFiltersModel) => {
                const validator = jasmine.createSpyObj('validator', ['called']);

                filterEventService.liverFilter.subscribe(
                    (object: any) => {
                        validator.called(object);
                    }
                );

                liverFunctionFiltersModel.emitEvent({});

                expect(validator.called).toHaveBeenCalledWith({});
            })
        );
    });

    describe('WHEN transforming model from server', () => {

        it('SHOULD be set correctly', inject([LiverFunctionFiltersModel], (liverFunctionFiltersModel: LiverFunctionFiltersModel) => {

            liverFunctionFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                measurementTimePoint: {
                    from: '2013-09-18T00:00:00', to: '2015-02-12T23:59:59'
                }
            });

            expect((<RangeDateFilterItemModel>liverFunctionFiltersModel.itemsModels[0]).availableValues.from).not.toBe(0);
            expect((<RangeDateFilterItemModel>liverFunctionFiltersModel.itemsModels[0]).availableValues.to).not.toBe(0);
            expect((<RangeDateFilterItemModel>liverFunctionFiltersModel.itemsModels[0]).selectedValues.from).not.toBeNull();
            expect((<RangeDateFilterItemModel>liverFunctionFiltersModel.itemsModels[0]).selectedValues.to).not.toBeNull();
        }));
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be set correctly', inject([LiverFunctionFiltersModel], (liverFunctionFiltersModel: LiverFunctionFiltersModel) => {

            liverFunctionFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                measurementTimePoint: {
                    from: '2013-09-18T00:00:00', to: '2015-02-12T23:59:59'
                }
            });

            (<RangeDateFilterItemModel>liverFunctionFiltersModel.itemsModels[0]).selectedValues = {
                from: '19-Sep-2013',
                to: '20-Sep-2013',
                includeEmptyValues: true
            };
            (<RangeDateFilterItemModel>liverFunctionFiltersModel.itemsModels[0]).appliedSelectedValues = {
                from: '19-Sep-2013',
                to: '20-Sep-2013',
                includeEmptyValues: true
            };
            (<RangeDateFilterItemModel>liverFunctionFiltersModel.itemsModels[0]).haveMadeChange = true;

            const serverObject = liverFunctionFiltersModel.transformFiltersToServer();

            expect(serverObject.measurementTimePoint.from).toEqual('2013-09-19T00:00:00');
            expect(serverObject.measurementTimePoint.to).toEqual('2013-09-20T00:00:00');
        }));
    });

    describe('WHEN resetting model', () => {

        it('SHOULD be reset correctly', inject([LiverFunctionFiltersModel], (liverFunctionFiltersModel: LiverFunctionFiltersModel) => {

            liverFunctionFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                labcode: {
                    values: ['lab1', 'lab2']
                }
            });

            liverFunctionFiltersModel.reset();

            const serverObject = liverFunctionFiltersModel.transformFiltersToServer();

            expect(serverObject).toEqual({});
        }));
    });

    describe('WHEN checking if visible', () => {
        let datasetViews, liverFunctionFiltersModel;

        beforeEach(inject([DatasetViews, LiverFunctionFiltersModel],
            (_datasetViews: DatasetViews, _liverFunctionFiltersModel: LiverFunctionFiltersModel) => {
            datasetViews = _datasetViews;
            liverFunctionFiltersModel = _liverFunctionFiltersModel;
        }));

        describe('AND datasetViews have no data', () => {
            it('THEN should not be visible', () => {
                spyOn(datasetViews, 'hasLiverData').and.returnValue(false);
                expect(liverFunctionFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasLiverData').and.returnValue(true);
                expect(liverFunctionFiltersModel.isVisible()).toBeTruthy();
            });
        });
    });
});
