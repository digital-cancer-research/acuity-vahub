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

import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

import {FiltersBarComponent} from './FiltersBarComponent';
import {ModalMessageComponentModule} from '../common/modals/modalMessage/ModalMessageComponent.module';
import {TimelineConfigurationsComponentModule} from '../plugins/timeline/module';
import {FiltersBarService} from './FiltersBarService';
import {ClearFiltersModalComponentModule} from '../common/modals/clearFilters/ClearFiltersModalComponent.module';
import {
    PopulationFiltersModel,
    AesFiltersModel,
    ConmedsFiltersModel,
    CohortFiltersModel,
    DoseFiltersModel,
    VitalsFiltersModel,
    PatientDataFiltersModel,
    LabsFiltersModel,
    LungFunctionFiltersModel,
    RenalFiltersModel,
    LiverFunctionFiltersModel,
    ExacerbationsFiltersModel,
    CardiacFiltersModel,
    SeriousAesFiltersModel,
    DeathFiltersModel,
    DoseDiscontinuationFiltersModel,
    LiverDiagnosticInvestigationFiltersModel,
    MedicalHistoryFiltersModel,
    AlcoholFiltersModel,
    LiverRiskFactorsFiltersModel,
    SurgicalHistoryFiltersModel,
    NicotineFiltersModel,
    RecistFiltersModel,
    CIEventsFiltersModel,
    CerebrovascularFiltersModel,
    PopulationFilterComponent,
    BiomarkersFiltersModel,
    CvotFiltersModel,
    AesFilterComponent,
    ExposureFiltersModel,
    DoseProportionalityFiltersModel,
    PkOverallResponseFiltersModel,
    TumourResponseFiltersModel,
    CtDnaFiltersModel,
    filterComponents
} from './dataTypes/module';
import {settingsComponents} from './settings/module';
import {SelectedFiltersModel} from './selectedFilters/SelectedFiltersModel';

import {FilterEventService} from './event/FilterEventService';
import {FilterHttpService} from './http/FilterHttpService';
import {FilterCollectionComponentModule} from './dataTypes/filtercollection/FilterCollectionComponent.module';
import {FiltersExportService} from './FiltersExportService';
import {CommonPipesModule} from '../common/pipes/CommonPipes.module';
import {ZoomRangeComponent} from './settings/zoom/zoomrange/ZoomRangeComponent';
import {CommonDirectivesModule} from '../common/directives/directives.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        BrowserModule,
        TimelineConfigurationsComponentModule,
        CommonDirectivesModule,
        FilterCollectionComponentModule,
        ClearFiltersModalComponentModule,
        ModalMessageComponentModule,
        CommonPipesModule
    ],
    exports: [FiltersBarComponent, PopulationFilterComponent, AesFilterComponent],
    declarations: [
        ...filterComponents,
        ...settingsComponents,
        FiltersBarComponent,
        ZoomRangeComponent
    ],
    providers: [FiltersBarService, FiltersExportService],
})
export class FiltersBarComponentModule {
}

export const filtersProviders = [
    FilterEventService,
    PopulationFiltersModel,
    AesFiltersModel,
    ConmedsFiltersModel,
    CohortFiltersModel,
    DoseFiltersModel,
    VitalsFiltersModel,
    PatientDataFiltersModel,
    LabsFiltersModel,
    LungFunctionFiltersModel,
    ExacerbationsFiltersModel,
    FilterHttpService,
    RenalFiltersModel,
    LiverFunctionFiltersModel,
    DoseDiscontinuationFiltersModel,
    CardiacFiltersModel,
    DeathFiltersModel,
    RecistFiltersModel,
    SeriousAesFiltersModel,
    LiverDiagnosticInvestigationFiltersModel,
    MedicalHistoryFiltersModel,
    LiverRiskFactorsFiltersModel,
    AlcoholFiltersModel,
    SurgicalHistoryFiltersModel,
    NicotineFiltersModel,
    CIEventsFiltersModel,
    SelectedFiltersModel,
    CerebrovascularFiltersModel,
    BiomarkersFiltersModel,
    CvotFiltersModel,
    ExposureFiltersModel,
    DoseProportionalityFiltersModel,
    PkOverallResponseFiltersModel,
    TumourResponseFiltersModel,
    CtDnaFiltersModel
];
