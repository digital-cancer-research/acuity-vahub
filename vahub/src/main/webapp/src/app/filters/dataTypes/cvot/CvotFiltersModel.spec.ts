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

import {CvotFiltersModel} from './CvotFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {MockDatasetViews, MockFilterEventService, MockHttpClient} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a CvotFiltersModel class', () => {
    let cvotFiltersModel;

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
                    provide: CvotFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService, e: FilterEventService,
                                 d: DatasetViews): CvotFiltersModel =>
                        new CvotFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    beforeEach(inject([CvotFiltersModel], (_cvotFiltersModel: CvotFiltersModel) => {
        cvotFiltersModel = _cvotFiltersModel;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {
            expect(cvotFiltersModel.itemsModels.length).toBe(9);
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', () => {
            expect(cvotFiltersModel.transformFiltersToServer()).toEqual({});
        });
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name cvotEndpoint', () => {

            expect(cvotFiltersModel.getName()).toEqual('cvotEndpoint');
        });
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setCvotFilter',
            inject([FilterEventService], (filterEventService: FilterEventService) => {
                const validator = jasmine.createSpyObj('validator', ['called']);
                filterEventService.cvotFilter.subscribe(
                    (object: any) => {
                        validator.called(object);
                    }
                );
                cvotFiltersModel.emitEvent({});

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
                spyOn(datasetViews, 'hasCvotData').and.callFake(() => false);

                expect(cvotFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasCvotData').and.callFake(() => true);

                expect(cvotFiltersModel.isVisible()).toBeTruthy();
            });
        });

    });
});
