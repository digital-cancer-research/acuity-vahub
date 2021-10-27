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
import {SpyLocation} from '@angular/common/testing';
import {Router} from '@angular/router';
import {each, chain} from 'lodash';
import {HttpClient} from '@angular/common/http';
import {FiltersBarComponent} from './FiltersBarComponent';
import {FiltersBarService} from './FiltersBarService';
import {
    FilterHttpService,
    FilterEventService,
    CardiacFiltersModel,
    ConmedsFiltersModel,
    DoseFiltersModel,
    LabsFiltersModel,
    LiverFunctionFiltersModel,
    LungFunctionFiltersModel,
    ExacerbationsFiltersModel,
    RenalFiltersModel,
    VitalsFiltersModel,
    BaseFilterItemModel,
    SelectedFiltersModel,
    filtersProviders,
    TumourResponseFiltersModel
} from './module';
import {
    PopulationFiltersModel,
    AesFiltersModel
} from './dataTypes/module';
import {TimelineTrackService} from '../plugins/timeline/config/trackselection/TimelineTrackService';
import {SessionEventService} from '../session/module';
import {
    MockRouter,
    MockSessionEventService,
    MockFilterEventService,
    MockHttpClient,
    MockStudyService,
    MockCohortEditorService,
    MockStore,
    MockUserPermissions,
    MockFilterModel
} from '../common/MockClasses';
import {ModalAnswer} from '../common/trellising/store';
import {BaseMapFilterItemModel} from './components/BaseMapFilterItemModel';
import {FiltersUtils} from './utils/FiltersUtils';
import {FiltersExportService} from './FiltersExportService';
import {DatasetViews, UserPermissions} from '../security/module';
import {StudyService} from '../common/module';
import {CohortEditorService} from '../plugins/cohorteditor/services/CohortEditorService';
import {Store} from '@ngrx/store';

describe('GIVEN the Filter Menu', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                FiltersBarComponent,
                FiltersBarService,
                TimelineTrackService,
                FilterHttpService,
                SpyLocation,
                FiltersExportService,
                SelectedFiltersModel,
                ...filtersProviders,
                {provide: HttpClient, useClass: MockHttpClient},
                {provide: Store, useClass: MockStore},
                DatasetViews,
                {provide: CohortEditorService, useClass: MockCohortEditorService},
                {provide: StudyService, useClass: MockStudyService},
                {provide: UserPermissions, useClass: MockUserPermissions},
                FiltersUtils,
                {provide: FilterEventService, useClass: MockFilterEventService},
                {provide: PopulationFiltersModel, useClass: PopulationFiltersModel, deps: [FilterEventService]},
                {provide: AesFiltersModel, useClass: AesFiltersModel, deps: [FilterEventService]},
                {provide: TumourResponseFiltersModel, useClass: MockFilterModel},
                {provide: Router, useClass: MockRouter},
                {provide: SessionEventService, useClass: MockSessionEventService}
            ]
        });
    });

    describe('WHEN Apply All is pressed', () => {

        it('THEN the number of selected filters is reset',
            inject([FiltersBarComponent, PopulationFiltersModel, AesFiltersModel, CardiacFiltersModel, ConmedsFiltersModel,
                    DoseFiltersModel, LabsFiltersModel, LiverFunctionFiltersModel, LungFunctionFiltersModel, ExacerbationsFiltersModel,
                    RenalFiltersModel, VitalsFiltersModel
                ],
                (filtersBarComponent: FiltersBarComponent,
                 populationFiltersModel: PopulationFiltersModel,
                 aesFiltersModel: AesFiltersModel,
                 cardiacFiltersModel: CardiacFiltersModel,
                 conmedsFiltersModel: ConmedsFiltersModel,
                 doseFiltersModel: DoseFiltersModel,
                 labsFiltersModel: LabsFiltersModel,
                 liverFiltersModel: LiverFunctionFiltersModel,
                 lungFunctionfiltersModel: LungFunctionFiltersModel,
                 exacerbationsFiltersModel: ExacerbationsFiltersModel,
                 renalFiltersModel: RenalFiltersModel,
                 vitalsFiltersModel: VitalsFiltersModel) => {

                    const filterModels = [
                        populationFiltersModel,
                        aesFiltersModel,
                        cardiacFiltersModel,
                        conmedsFiltersModel,
                        doseFiltersModel,
                        labsFiltersModel,
                        liverFiltersModel,
                        lungFunctionfiltersModel,
                        exacerbationsFiltersModel,
                        renalFiltersModel,
                        vitalsFiltersModel
                    ];
                    const filters = filterModels.map(filter => filter.itemsModels);
                    spyOn(populationFiltersModel, 'getFilters');
                    each(filterModels, filter => {
                        spyOn(filter, 'isVisible').and.returnValue(true);
                    });
                    each(filters, (filter) => {
                        each(filter, (model: BaseFilterItemModel) => {
                            spyOn(model, 'reset');
                            if (model instanceof BaseMapFilterItemModel) {
                                return;
                            }
                            model.numberOfSelectedFilters = 1;
                        });
                    });
                    filtersBarComponent.clearAllFilters(ModalAnswer.YES);
                    const areAllSetToZero = chain(filters).flatten().map('numberOfSelectedFilters')
                        .every((val: number) => val === 0).value();
                    expect(areAllSetToZero).toBe(true);
                })
        );
    });
});
