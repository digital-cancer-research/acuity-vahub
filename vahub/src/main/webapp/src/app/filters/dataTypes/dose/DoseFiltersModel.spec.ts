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

import {DoseFiltersModel} from './DoseFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {ListFilterItemModel} from '../../components/module';
import {FilterEventService, FilterHttpService} from '../../module';

import {SessionEventService, SessionHttpService} from '../../../session/module';
import {MockDatasetViews, MockEnvService, MockFilterEventService} from '../../../common/MockClasses';
import {EnvService} from '../../../env/module';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a DoseFiltersModel class', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                SessionEventService,
                SessionHttpService,
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: EnvService, useValue: new MockEnvService()},
                {provide: FilterHttpService, useValue: new MockFilterHttpService()},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: DoseFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService,
                                 e: FilterEventService, d: DatasetViews): DoseFiltersModel =>
                        new DoseFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService]
                }
            ]
        });
    });

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', inject([DoseFiltersModel], (doseFiltersModel: DoseFiltersModel) => {
            expect(doseFiltersModel.itemsModels.length).toBe(31);

            expect((<ListFilterItemModel>doseFiltersModel.itemsModels[0]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>doseFiltersModel.itemsModels[0]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>doseFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);
        }));
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', inject([DoseFiltersModel], (doseFiltersModel: DoseFiltersModel) => {

            expect(doseFiltersModel.transformFiltersToServer()).toEqual({});
        }));
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name dose', inject([DoseFiltersModel], (doseFiltersModel: DoseFiltersModel) => {

            expect(doseFiltersModel.getName()).toEqual('dose');
        }));
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setDoseFilter',
            inject([FilterEventService, DoseFiltersModel], (filterEventService: FilterEventService, doseFiltersModel: DoseFiltersModel) => {
                const validator = jasmine.createSpyObj('validator', ['called']);

                filterEventService.doseFilter.subscribe(
                    (object: any) => {
                        validator.called(object);
                    }
                );

                doseFiltersModel.emitEvent({});

                expect(validator.called).toHaveBeenCalledWith({});
            })
        );
    });

    describe('WHEN transforming model from server', () => {

        it('SHOULD be set correctly', inject([DoseFiltersModel], (doseFiltersModel: DoseFiltersModel) => {

            doseFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                studyDrug: {
                    values: ['drug1', 'drug2']
                }
            });

            expect((<ListFilterItemModel>doseFiltersModel.itemsModels[0]).availableValues).toEqual(['drug1', 'drug2']);
            expect((<ListFilterItemModel>doseFiltersModel.itemsModels[0]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>doseFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);
        }));
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be set correctly', inject([DoseFiltersModel], (doseFiltersModel: DoseFiltersModel) => {

            doseFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                studyDrug: {
                    values: ['drug1', 'drug2']
                }
            });

            (<ListFilterItemModel>doseFiltersModel.itemsModels[0]).selectedValues = ['drug1', 'drug2'];
            (<ListFilterItemModel>doseFiltersModel.itemsModels[0]).appliedSelectedValues = ['drug1', 'drug2'];

            const serverObject = doseFiltersModel.transformFiltersToServer();

            expect(serverObject.studyDrug.values).toEqual(['drug1', 'drug2']);
        }));
    });

    describe('WHEN resetting model', () => {

        it('SHOULD be reset correctly', inject([DoseFiltersModel], (doseFiltersModel: DoseFiltersModel) => {

            doseFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                studyDrug: {
                    values: ['drug1', 'drug2']
                }
            });

            doseFiltersModel.reset();

            const serverObject = doseFiltersModel.transformFiltersToServer();

            expect(serverObject).toEqual({});
        }));
    });

});
