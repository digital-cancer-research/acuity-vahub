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

import {DeathFiltersModel} from './DeathFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {ListFilterItemModel} from '../../components/module';
import {FilterEventService, FilterHttpService} from '../../module';

import {SessionHttpService} from '../../../session/module';
import {MockDatasetViews, MockEnvService, MockFilterEventService} from '../../../common/MockClasses';
import {EnvService} from '../../../env/module';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a DeathFiltersModel class', () => {
    let deathFiltersModel;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                SessionHttpService,
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: EnvService, useValue: new MockEnvService()},
                {provide: FilterHttpService, useValue: new MockFilterHttpService()},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: DeathFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService,
                                 e: FilterEventService, d: DatasetViews): DeathFiltersModel =>
                        new DeathFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    beforeEach(inject([DeathFiltersModel], (_deathFiltersModel: DeathFiltersModel) => {
        deathFiltersModel = _deathFiltersModel;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {
            expect(deathFiltersModel.itemsModels.length).toBe(9);

            expect((<ListFilterItemModel>deathFiltersModel.itemsModels[0]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>deathFiltersModel.itemsModels[0]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>deathFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', () => {

            expect(deathFiltersModel.transformFiltersToServer()).toEqual({});
        });
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name death', () => {

            expect(deathFiltersModel.getName()).toEqual('death');
        });
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setDeathFilter',
            inject([FilterEventService], (filterEventService: FilterEventService) => {
                const validator = jasmine.createSpyObj('validator', ['called']);

                filterEventService.deathFilter.subscribe(
                    (object: any) => {
                        validator.called(object);
                    }
                );

                deathFiltersModel.emitEvent({});

                expect(validator.called).toHaveBeenCalledWith({});
            })
        );
    });

    describe('WHEN transforming model from server', () => {

        it('SHOULD be set correctly', () => {

            deathFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                deathCause: {
                    values: ['cause1', 'cause2']
                }
            });

            expect((<ListFilterItemModel>deathFiltersModel.itemsModels[0]).availableValues).toEqual(['cause1', 'cause2']);
            expect((<ListFilterItemModel>deathFiltersModel.itemsModels[0]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>deathFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be set correctly', () => {

            deathFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                deathCause: {
                    values: ['cause1', 'cause2']
                }
            });

            (<ListFilterItemModel>deathFiltersModel.itemsModels[0]).selectedValues = ['cause1', 'cause2'];
            (<ListFilterItemModel>deathFiltersModel.itemsModels[0]).appliedSelectedValues = ['cause1', 'cause2'];

            const serverObject = deathFiltersModel.transformFiltersToServer();

            expect(serverObject.deathCause.values).toEqual(['cause1', 'cause2']);
        });
    });

    describe('WHEN resetting model', () => {

        it('SHOULD be reset correctly', () => {

            deathFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                deathCause: {
                    values: ['cause1', 'cause2']
                }
            });

            deathFiltersModel.reset();

            const serverObject = deathFiltersModel.transformFiltersToServer();

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
                spyOn(datasetViews, 'hasDeathData').and.returnValue(false);
                expect(deathFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasDeathData').and.returnValue(true);
                expect(deathFiltersModel.isVisible()).toBeTruthy();
            });
        });

    });

});
