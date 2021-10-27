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

import {CardiacFiltersModel} from './CardiacFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {CheckListFilterItemModel, ListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {MockFilterEventService, MockDatasetViews} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a CardiacFiltersModel class', () => {
    let cardiacFiltersModel;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                HttpClient,
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: FilterHttpService, useValue: new MockFilterHttpService()},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: CardiacFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService, e: FilterEventService, d: DatasetViews): CardiacFiltersModel =>
                        new CardiacFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    beforeEach(inject([CardiacFiltersModel], (_cardiacFiltersModel: CardiacFiltersModel) => {
        cardiacFiltersModel = _cardiacFiltersModel;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {
            expect(cardiacFiltersModel.itemsModels.length).toBe(34);

            expect((<CheckListFilterItemModel>cardiacFiltersModel.itemsModels[10]).availableValues).toEqual([]);
            expect((<CheckListFilterItemModel>cardiacFiltersModel.itemsModels[10]).selectedValues).toEqual([]);
            expect((<CheckListFilterItemModel>cardiacFiltersModel.itemsModels[10]).initialValues).toEqual([]);
            expect((<CheckListFilterItemModel>cardiacFiltersModel.itemsModels[10]).includeEmptyValues).toEqual(true);

            expect((<ListFilterItemModel>cardiacFiltersModel.itemsModels[6]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>cardiacFiltersModel.itemsModels[6]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>cardiacFiltersModel.itemsModels[6]).includeEmptyValues).toEqual(true);
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', () => {
            expect(cardiacFiltersModel.transformFiltersToServer()).toEqual({});
        });
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name cardiac', () => {
            expect(cardiacFiltersModel.getName()).toEqual('cardiac');
        });
    });

    describe('WHEN getting displayed name', () => {
        it('SHOULD get display name cardiac', () => {

            expect(cardiacFiltersModel.getDisplayName()).toEqual('Cardiac');
        });
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setCardiacFilter',
            inject([FilterEventService], (filterEventService: FilterEventService) => {
                const validator = jasmine.createSpyObj('validator', ['called']);
                filterEventService.cardiacFilter.subscribe(
                    (object: any) => {
                        validator.called(object);
                    }
                );
                cardiacFiltersModel.emitEvent({});

                expect(validator.called).toHaveBeenCalledWith({});
            }));
    });

    describe('WHEN transforming model from server', () => {

        it('SHOULD be set correctly', () => {

            cardiacFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                baselineFlag: {
                    values: ['yes', 'no']
                }
            });
            expect((<CheckListFilterItemModel>cardiacFiltersModel.itemsModels[10]).availableValues).toEqual(['yes', 'no']);
            expect((<CheckListFilterItemModel>cardiacFiltersModel.itemsModels[10]).selectedValues).toEqual(['yes', 'no']);
            expect((<CheckListFilterItemModel>cardiacFiltersModel.itemsModels[10]).initialValues).toEqual(['yes', 'no']);
            expect((<CheckListFilterItemModel>cardiacFiltersModel.itemsModels[10]).includeEmptyValues).toEqual(true);
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be set correctly', () => {

            cardiacFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                baselineFlag: {
                    values: ['yes', 'no']
                }
            });

            (<CheckListFilterItemModel>cardiacFiltersModel.itemsModels[10]).selectedValues = ['yes'];
            // (<CheckListFilterItemModel>cardiacFiltersModel.itemsModels[11]).availableValues = ['yes', 'no'];
            (<CheckListFilterItemModel>cardiacFiltersModel.itemsModels[10]).appliedSelectedValues = ['yes'];

            const serverObject = cardiacFiltersModel.transformFiltersToServer(true);

            expect(serverObject.baselineFlag.values).toEqual(['yes']);
        });
    });

    describe('WHEN resetting model', () => {

        it('SHOULD be reset correctly', () => {

            cardiacFiltersModel.transformFiltersFromServer({
                matchedItemsCount: 1900,
                resultUnit: {
                    values: ['Name1']
                },
                baselineFlag: {
                    values: ['yes', 'no']
                }
            });

            cardiacFiltersModel.reset();

            const serverObject = cardiacFiltersModel.transformFiltersToServer();

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
                spyOn(datasetViews, 'hasCardiacData').and.callFake(() => false);

                expect(cardiacFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasCardiacData').and.callFake(() => true);

                expect(cardiacFiltersModel.isVisible()).toBeTruthy();
            });
        });

    });
});
