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
import {Observable} from 'rxjs/Observable';
import * as  _ from 'lodash';

import {FilterCollectionComponent} from './FilterCollectionComponent';
import {RangeFilterItemModel, ListFilterItemModel} from '../../components/module';
import {BaseMapFilterItemModel} from '../../components/BaseMapFilterItemModel';
import {
    AesFiltersModel,
    CardiacFiltersModel,
    PopulationFiltersModel,
    FilterHttpService,
    FilterEventService
} from '../../module';

import {MockFilterEventService} from '../../../common/MockClasses';

jasmine.DEFAULT_TIMEOUT_INTERVAL = 6000;

describe('GIVEN a FilterCollectionComponent', () => {

    class MockFilterHttpService {
        getEventFiltersObservable(): Observable<any> {
            const res = {visitNumber: {from: 1, to: 101}, resultValue: {from: 10, to: 99}};
            return Observable.of(res);
        }
    }

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {provide: PopulationFiltersModel, useClass: PopulationFiltersModel, deps: [FilterEventService]},
                {provide: AesFiltersModel, useClass: AesFiltersModel, deps: [FilterEventService]},
                {provide: 'MAP_LIST', useValue: 'MAP_LIST'},
                {provide: 'drugsDiscontinutaionReason', useValue: 'drugsDiscontinutaionReason'},
                {provide: 'Main Reason For Drug Discontinuation', useValue: 'Main Reason For Drug Discontinuation'},
                {
                    provide: BaseMapFilterItemModel,
                    useClass: BaseMapFilterItemModel,
                    deps: ['MAP_LIST', 'drugsDiscontinutaionReason', 'Main Reason For Drug Discontinuation']
                },
                {provide: Document, useValue: document},
                {
                    provide: CardiacFiltersModel,
                    useClass: CardiacFiltersModel,
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService]
                },
                {
                    provide: FilterCollectionComponent,
                    deps: [Document],
                    useFactory: (document: Document): FilterCollectionComponent => {
                        return new FilterCollectionComponent(document);
                    }
                }
            ]
        });
    });

    describe('WHEN Apply is pressed', () => {

        it('THEN the number of saved filters is updated',
            inject([FilterCollectionComponent, AesFiltersModel],
                (filterCollectionComponent: FilterCollectionComponent, aesFiltersModel: AesFiltersModel) => {
                    filterCollectionComponent.filtersModel = aesFiltersModel;
                    (<any>filterCollectionComponent.filtersModel.itemsModels[0]).selectedValues = ['pt-1', 'pt-2'];
                    spyOn(filterCollectionComponent.filtersModel, 'getFilters');
                    filterCollectionComponent.onApply();

                    expect(filterCollectionComponent.filtersModel.itemsModels[0].numberOfSelectedFilters)
                        .toBe((<any>filterCollectionComponent.filtersModel.itemsModels[0]).selectedValues.length);
                }));
    });

    describe('WHEN Clear is pressed', () => {

        it('THEN current filter item is cleared',
            inject([FilterCollectionComponent, AesFiltersModel],
                (filterCollectionComponent: FilterCollectionComponent, aesFiltersModel: AesFiltersModel) => {
                    filterCollectionComponent.filtersModel = aesFiltersModel;
                    filterCollectionComponent.openedFilterModel = filterCollectionComponent.filtersModel.itemsModels[0];
                    (<any>filterCollectionComponent.filtersModel.itemsModels[0]).selectedValues = ['pt-1', 'pt-2'];
                    (<any>filterCollectionComponent.filtersModel.itemsModels[0]).numberOfSelectedFilters = 2;
                    spyOn(filterCollectionComponent.filtersModel, 'getFilters');
                    filterCollectionComponent.onClear();

                    expect((<any>filterCollectionComponent.filtersModel.itemsModels[0]).numberOfSelectedFilters).toBe(0);
                    expect((<any>filterCollectionComponent.filtersModel.itemsModels[0]).selectedValues).toEqual([]);
                }));
    });
    describe('WHEN a filter is changed', () => {
        it('THEN the empty population map filters are populated',
            inject([BaseMapFilterItemModel],
                (baseMapFilterItemModel: BaseMapFilterItemModel) => {
                    baseMapFilterItemModel.filters = [
                        <ListFilterItemModel> {
                            appliedSelectedValues: [],
                            key: 'placebo',
                            availableValues: [0.1, 0.2],
                            selectedValues: []
                        },
                        <ListFilterItemModel> {
                            appliedSelectedValues: [],
                            key: 'SuperDex10mg',
                            availableValues: [0.1, 0.2],
                            selectedValues: []
                        }
                    ];
                    const returnedServerObject = {
                        map: {
                            placebo: {
                                values: [0.1]
                            }
                        }
                    };
                    const expectedResult = {
                        map: {
                            placebo: {
                                values: [0.1]
                            },
                            SuperDex10mg: {}
                        }
                    };
                    baseMapFilterItemModel.findEmptyFilters(baseMapFilterItemModel.filters, returnedServerObject);
                    expect(returnedServerObject).toEqual(expectedResult);
                }));
    });
    describe('WHEN a range filter is changed', () => {
        it('THEN the other range filters are updated', (done: Function) => { // Pass in done to tell Jasmine when to run the test
            inject([FilterCollectionComponent, CardiacFiltersModel, FilterEventService, FilterHttpService],
                (filterCollectionComponent: FilterCollectionComponent, cardiacFiltersModel: CardiacFiltersModel,
                 filterEventService: FilterEventService) => {
                    filterEventService.cardiacFilter.subscribe(() => {
                    });
                    // spyOn(cardiacFiltersModel, 'getFilters');
                    filterCollectionComponent.filtersModel = cardiacFiltersModel;
                    const visitNumberFilter = <RangeFilterItemModel> _.find(cardiacFiltersModel.itemsModels, {key: 'visitNumber'});
                    const resultValueFilter = <RangeFilterItemModel> _.find(cardiacFiltersModel.itemsModels, {key: 'resultValue'});
                    visitNumberFilter.selectedValues.from = 1;
                    visitNumberFilter.selectedValues.to = 101;
                    resultValueFilter.selectedValues.from = 0;
                    resultValueFilter.selectedValues.to = 0;
                    visitNumberFilter.haveMadeChange = true;
                    filterCollectionComponent.onApply();

                    // Need to set timeout as the filters are on a timeout of 1000ms
                    setTimeout(() => {
                        expect(resultValueFilter.selectedValues.from).toBe(10);
                        expect(resultValueFilter.selectedValues.to).toBe(99);
                        done(); // Tell Jasmine to run the test
                    }, 2000);

                })(); // Wrap in an IFFE to get async tests working
        }, 2500); // Tell Jasmine to wait 2500ms to run the test (i.e. less than the timeout, above)
    });
});
