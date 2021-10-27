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
import * as  _ from 'lodash';

import {
    AesFiltersModel,
    AlcoholFiltersModel,
    BiomarkersFiltersModel,
    CardiacFiltersModel,
    CerebrovascularFiltersModel,
    CIEventsFiltersModel,
    CohortFiltersModel,
    ConmedsFiltersModel,
    CtDnaFiltersModel,
    CvotFiltersModel,
    DeathFiltersModel,
    DoseDiscontinuationFiltersModel,
    DoseFiltersModel,
    DoseProportionalityFiltersModel,
    ExacerbationsFiltersModel,
    ExposureFiltersModel,
    LabsFiltersModel,
    ListFilterItemModel,
    LiverDiagnosticInvestigationFiltersModel,
    LiverFunctionFiltersModel,
    LiverRiskFactorsFiltersModel,
    LungFunctionFiltersModel,
    MedicalHistoryFiltersModel,
    NicotineFiltersModel,
    PatientDataFiltersModel,
    PopulationFiltersModel,
    RecistFiltersModel,
    RenalFiltersModel,
    SelectedFiltersModel,
    SeriousAesFiltersModel,
    SurgicalHistoryFiltersModel,
    TumourResponseFiltersModel,
    VitalsFiltersModel
} from '../module';
import {CohortEditorService} from '../../plugins/cohorteditor/services/CohortEditorService';
import {FilterReloadService} from '../../plugins/cohorteditor/services/FilterReloadService';
import {FilterEventService} from '../event/FilterEventService';
import {SessionEventService} from '../../session/event/SessionEventService';
import {
    MockDatasetViews,
    MockFilterEventService,
    MockFilterHttpService,
    MockSessionEventService,
    MockTimelineConfigService
} from '../../common/MockClasses';
import {TrackName} from '../../plugins/timeline/store/ITimeline';
import {FiltersUtils} from '../utils/FiltersUtils';
import {FilterId} from '../../common/trellising/store';
import {TimelineDispatcher} from '../../plugins/timeline/store/dispatcher/TimelineDispatcher';
import {TrellisingDispatcher} from '../../common/trellising/store/dispatcher/TrellisingDispatcher';
import {FilterHttpService} from '../http/FilterHttpService';
import {DatasetViews} from '../../security/DatasetViews';
import {StoreModule} from '@ngrx/store';
import {TimelineConfigService} from '../../common/trellising/store/services/TimelineConfigService';
import {sharedStateReducer} from '../../common/store/reducers/SharedStateReducer';
import {trellisingReducer} from '../../common/trellising/store/reducer/TrellisingReducer';
import {PkOverallResponseFiltersModel} from '../dataTypes/module';
import {UserPermissions} from '../../security/module';

describe('GIVEN a SelectedFiltersModel class', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [HttpClientTestingModule,
                StoreModule.forRoot({sharedStateReducer: sharedStateReducer, trellisingReducer: trellisingReducer})],
            providers: [
                SelectedFiltersModel,
                CohortEditorService,
                FilterReloadService,
                TimelineDispatcher,
                TrellisingDispatcher,
                FiltersUtils,
                SeriousAesFiltersModel,
                CardiacFiltersModel,
                PopulationFiltersModel,
                AesFiltersModel,
                ConmedsFiltersModel,
                DoseFiltersModel,
                LabsFiltersModel,
                LiverFunctionFiltersModel,
                ExacerbationsFiltersModel,
                LungFunctionFiltersModel,
                RenalFiltersModel,
                DeathFiltersModel,
                VitalsFiltersModel,
                DoseDiscontinuationFiltersModel,
                MedicalHistoryFiltersModel,
                LiverDiagnosticInvestigationFiltersModel,
                AlcoholFiltersModel,
                LiverRiskFactorsFiltersModel,
                SurgicalHistoryFiltersModel,
                NicotineFiltersModel,
                RecistFiltersModel,
                CIEventsFiltersModel,
                CerebrovascularFiltersModel,
                BiomarkersFiltersModel,
                CvotFiltersModel,
                CohortFiltersModel,
                ExposureFiltersModel,
                DoseProportionalityFiltersModel,
                TumourResponseFiltersModel,
                PatientDataFiltersModel,
                CtDnaFiltersModel,
                PkOverallResponseFiltersModel,
                UserPermissions,
                {provide: TimelineConfigService, useClass: MockTimelineConfigService},
                {provide: FilterHttpService, useClass: MockFilterHttpService},
                {provide: DatasetViews, useClass: MockDatasetViews},
                {provide: FilterEventService, useClass: MockFilterEventService},
                {provide: SessionEventService, useClass: MockSessionEventService}
            ]
        });
    });

    describe('WHEN constructing', () => {

        it('THEN it should have instance var set', inject([SelectedFiltersModel], (selectedFiltersModel: SelectedFiltersModel) => {
            expect(selectedFiltersModel.numberOfSubjects).toBe('(All)');
            expect(selectedFiltersModel.numberOfEvents).toBe('(All)');
            expect(selectedFiltersModel.consideredFilter).toBeNull();
            expect(selectedFiltersModel.timelineSelectedTracks).toEqual([TrackName.SUMMARY]);
        }));
    });

    describe('WHEN there is a Cohort filter', () => {
        it('THEN the Subject ID filter is hidden from view', inject([SelectedFiltersModel, PopulationFiltersModel],
            (selectedFiltersModel: SelectedFiltersModel, populationFiltersModel: PopulationFiltersModel) => {
            const subjectIds = ['subj-1', 'subj-2'];
            populationFiltersModel.addCohortFilter(subjectIds, 'a cohort name');
            (<ListFilterItemModel> _.find(populationFiltersModel.itemsModels, {key: PopulationFiltersModel.SUBJECT_IDS_KEY}))
                .selectedValues = subjectIds;
            (<ListFilterItemModel> _.find(populationFiltersModel.itemsModels, {key: PopulationFiltersModel.SUBJECT_IDS_KEY}))
                .appliedSelectedValues = subjectIds;

            selectedFiltersModel.transformFilters(FilterId.POPULATION);

            expect(selectedFiltersModel.selectedPopulationFilters.length).toBe(1);
            expect(selectedFiltersModel.selectedPopulationFilters[0].key.indexOf(PopulationFiltersModel.COHORT_EDITOR_KEY) > -1)
                .toBeTruthy();
        }));

        it('THEN the population filters are reloaded in case a cohort is renamed', inject([SelectedFiltersModel, PopulationFiltersModel],
                (selectedFiltersModel: SelectedFiltersModel, populationFiltersModel: PopulationFiltersModel) => {
            populationFiltersModel.addCohortFilter(['subj-1'], 'a cohort');
            const cohortFilter = populationFiltersModel.getCohortEditorFilters();
            spyOn(selectedFiltersModel, 'getSelectedFilters').and.returnValue(cohortFilter);
            spyOn(selectedFiltersModel, 'getCurrentPageName').and.returnValue('Adverse Events -> AEs Subject Counts');

            selectedFiltersModel.transformFilters(FilterId.AES);

            expect(selectedFiltersModel.selectedPopulationFilters).toEqual(cohortFilter);
        }));
    });

    describe('WHEN there is no Cohort filter', () => {

        it('THEN the Subject ID filter is visible', inject([SelectedFiltersModel, PopulationFiltersModel],
                (selectedFiltersModel: SelectedFiltersModel, populationFiltersModel: PopulationFiltersModel) => {
            (<ListFilterItemModel> _.find(populationFiltersModel.itemsModels, {key: PopulationFiltersModel.SUBJECT_IDS_KEY}))
                .appliedSelectedValues = ['subj-1', 'subj-2'];
            (<ListFilterItemModel> _.find(populationFiltersModel.itemsModels, {key: PopulationFiltersModel.SUBJECT_IDS_KEY}))
                .selectedValues = ['subj-1', 'subj-2'];

            selectedFiltersModel.transformFilters(FilterId.POPULATION);

            expect(selectedFiltersModel.selectedPopulationFilters[0].key).toBe(PopulationFiltersModel.SUBJECT_IDS_KEY);
        }));
    });
});
