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

import {VitalsFiltersModel} from './VitalsFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {CheckListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {SessionEventService} from '../../../session/event/SessionEventService';
import {MockDatasetViews, MockFilterEventService} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a VitalsFiltersModel class', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: DatasetViews, useClass: MockDatasetViews},
                SessionEventService,
                {provide: FilterHttpService, useValue: new MockFilterHttpService()},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: VitalsFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService,
                                 e: FilterEventService, d: DatasetViews): VitalsFiltersModel =>
                        new VitalsFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', inject([VitalsFiltersModel], (vitalsFiltersModel: VitalsFiltersModel) => {
            expect(vitalsFiltersModel.itemsModels.length).toBe(20);

            expect((<CheckListFilterItemModel>vitalsFiltersModel.itemsModels[0]).availableValues).toEqual([]);
            expect((<CheckListFilterItemModel>vitalsFiltersModel.itemsModels[0]).selectedValues).toEqual([]);
            expect((<CheckListFilterItemModel>vitalsFiltersModel.itemsModels[0]).initialValues).toEqual([]);
            expect((<CheckListFilterItemModel>vitalsFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);
        }));
    });

    describe('WHEN transforming empty model to server', () => {

        it('THEN it contains no filters', inject([VitalsFiltersModel], (vitalsFiltersModel: VitalsFiltersModel) => {

            expect(vitalsFiltersModel.transformFiltersToServer()).toEqual({});
        }));
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name vitals', inject([VitalsFiltersModel], (vitalsFiltersModel: VitalsFiltersModel) => {

            expect(vitalsFiltersModel.getName()).toEqual('vitals');
        }));
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setVitalsFilter',
            inject([FilterEventService, VitalsFiltersModel], (filterEventService: FilterEventService,
                                                              vitalsFiltersModel: VitalsFiltersModel) => {
                const validator = jasmine.createSpyObj('validator', ['called']);

                filterEventService.vitalsFilter.subscribe(
                    (object: any) => {
                        validator.called(object);
                    }
                );

                vitalsFiltersModel.emitEvent({});

                expect(validator.called).toHaveBeenCalledWith({});
            })
        );
    });

    describe('WHEN transforming model from server', () => {

        it('SHOULD be set correctly', inject([VitalsFiltersModel], (vitalsFiltersModel: VitalsFiltersModel) => {

            vitalsFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 22545,
                vitalsMeasurements: {
                    values: ['DIASTOLIC BLOOD PRESSURE', 'PULSE OXIMETRY']
                },
                measurementDate: {
                    from: '2001-01-01T00:00:00', to: '2002-01-01T00:00:00'
                }
            });

            expect((<CheckListFilterItemModel>vitalsFiltersModel.itemsModels[0]).availableValues)
                .toEqual(['DIASTOLIC BLOOD PRESSURE', 'PULSE OXIMETRY']);
            expect((<CheckListFilterItemModel>vitalsFiltersModel.itemsModels[0]).selectedValues)
                .toEqual(['DIASTOLIC BLOOD PRESSURE', 'PULSE OXIMETRY']);
            expect((<CheckListFilterItemModel>vitalsFiltersModel.itemsModels[0]).initialValues)
                .toEqual(['DIASTOLIC BLOOD PRESSURE', 'PULSE OXIMETRY']);
            expect((<CheckListFilterItemModel>vitalsFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);
        }));
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be set correctly', inject([VitalsFiltersModel], (vitalsFiltersModel: VitalsFiltersModel) => {

            vitalsFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 22545,
                vitalsMeasurements: {
                    values: ['DIASTOLIC BLOOD PRESSURE', 'PULSE OXIMETRY']
                },
                measurementDate: {
                    from: '2001-01-01T00:00:00', to: '2002-01-01T00:00:00'
                }
            });

            (<CheckListFilterItemModel>vitalsFiltersModel.itemsModels[0]).selectedValues = ['DIASTOLIC BLOOD PRESSURE'];
            (<CheckListFilterItemModel>vitalsFiltersModel.itemsModels[0]).appliedSelectedValues = ['DIASTOLIC BLOOD PRESSURE'];

            const serverObject = vitalsFiltersModel.transformFiltersToServer();

            expect(serverObject.vitalsMeasurements.values).toEqual(['DIASTOLIC BLOOD PRESSURE']);
        }));
    });

    describe('WHEN resetting model', () => {

        it('SHOULD be reset correctly', inject([VitalsFiltersModel], (vitalsFiltersModel: VitalsFiltersModel) => {

            vitalsFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                visitNumber: {
                    from: 0, to: 1
                },
                measurementDate: {
                    from: '2001-01-01T00:00:00', to: '2002-01-01T00:00:00'
                }
            });

            vitalsFiltersModel.reset();

            const serverObject = vitalsFiltersModel.transformFiltersToServer();

            expect(serverObject).toEqual({});
        }));
    });

    describe('WHEN checking if visible', () => {
        let datasetViews, vitalsFiltersModel;

        beforeEach(inject([DatasetViews, VitalsFiltersModel], (_datasetViews: DatasetViews, _vitalsFiltersModel: VitalsFiltersModel) => {
            datasetViews = _datasetViews;
            vitalsFiltersModel = _vitalsFiltersModel;
        }));

        describe('AND datasetViews have no data', () => {
            it('THEN should not be visible', () => {
                spyOn(datasetViews, 'hasVitalsData').and.returnValue(false);
                expect(vitalsFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasVitalsData').and.returnValue(true);
                expect(vitalsFiltersModel.isVisible()).toBeTruthy();
            });
        });
    });
});
