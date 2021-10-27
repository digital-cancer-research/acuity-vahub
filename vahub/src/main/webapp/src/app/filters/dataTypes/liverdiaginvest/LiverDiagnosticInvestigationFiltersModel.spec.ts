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

import {LiverDiagnosticInvestigationFiltersModel} from './LiverDiagnosticInvestigationFiltersModel';
import {PopulationFiltersModel} from '../population/PopulationFiltersModel';
import {ListFilterItemModel} from '../../components/module';
import {FilterHttpService} from '../../http/FilterHttpService';
import {FilterEventService} from '../../event/FilterEventService';

import {MockDatasetViews, MockFilterEventService} from '../../../common/MockClasses';
import {DatasetViews} from '../../../security/DatasetViews';

class MockFilterHttpService {
}

describe('GIVEN a LiverDiagnosticInvestigationFiltersModel class', () => {
    let liverDiagnosticInvestigationFiltersModel;

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule],
            providers: [
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: PopulationFiltersModel, deps: [FilterHttpService, FilterEventService]},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {
                    provide: LiverDiagnosticInvestigationFiltersModel,
                    useFactory: (p: PopulationFiltersModel, f: FilterHttpService, e: FilterEventService,
                                 d: DatasetViews): LiverDiagnosticInvestigationFiltersModel =>
                        new LiverDiagnosticInvestigationFiltersModel(p, f, e, d),
                    deps: [PopulationFiltersModel, FilterHttpService, FilterEventService, DatasetViews]
                }
            ]
        });
    });

    beforeEach(inject([LiverDiagnosticInvestigationFiltersModel],
            (_liverDiagnosticInvestigationFiltersModel: LiverDiagnosticInvestigationFiltersModel) => {
        liverDiagnosticInvestigationFiltersModel = _liverDiagnosticInvestigationFiltersModel;
    }));

    describe('WHEN constructing', () => {

        it('SHOULD have instance var set', () => {

            expect(liverDiagnosticInvestigationFiltersModel.itemsModels.length).toBe(6);

            expect((<ListFilterItemModel>liverDiagnosticInvestigationFiltersModel.itemsModels[0]).availableValues).toEqual([]);
            expect((<ListFilterItemModel>liverDiagnosticInvestigationFiltersModel.itemsModels[0]).selectedValues).toEqual([]);
            expect((<ListFilterItemModel>liverDiagnosticInvestigationFiltersModel.itemsModels[0]).includeEmptyValues).toEqual(true);

            expect(liverDiagnosticInvestigationFiltersModel.datasetViews).toBeDefined();
        });
    });

    describe('WHEN transforming model to server', () => {

        it('SHOULD be empty', () => {
            expect(liverDiagnosticInvestigationFiltersModel.transformFiltersToServer()).toEqual({});
        });
    });

    describe('WHEN getting name', () => {
        it('SHOULD get name liverDiagnosticInvestigation', () => {
            expect(liverDiagnosticInvestigationFiltersModel.getName()).toEqual('liverDiag');
        });
    });

    describe('WHEN emitEvents', () => {

        it('SHOULD be set filterEventService.setLiverDiagnosticInvestigationFilter', inject([FilterEventService],
            (filterEventService: FilterEventService) => {
            const validator = jasmine.createSpyObj('validator', ['called']);

            filterEventService.liverDiagnosticInvestigationFilter.subscribe(
                (object: any) => {
                    validator.called(object);
                }
            );

            liverDiagnosticInvestigationFiltersModel.emitEvent({});

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
                spyOn(datasetViews, 'hasLiverDiagnosticInvestigationData').and.returnValue(false);
                expect(liverDiagnosticInvestigationFiltersModel.isVisible()).toBeFalsy();
            });
        });

        describe('AND datasetViews have data', () => {
            it('THEN should be visible', () => {
                spyOn(datasetViews, 'hasLiverDiagnosticInvestigationData').and.returnValue(true);
                expect(liverDiagnosticInvestigationFiltersModel.isVisible()).toBeTruthy();
            });
        });

    });
});
