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

import {TimelineFilterComponent} from '../../plugins/timeline/filter/TimelineFilterComponent';
import {AesFilterComponent} from './aes/AesFilterComponent';
import {AlcoholFilterComponent} from './alcohol/AlcoholFilterComponent';
import {BiomarkersFilterComponent} from './biomarkers/BiomarkersFilterComponent';
import {CardiacFiltersComponent} from './cardiac/CardiacFiltersComponent';
import {CerebrovascularFilterComponent} from './cerebrovascular/CerebrovascularFilterComponent';
import {CIEventsFilterComponent} from './cievents/CIEventsFilterComponent';
import {CohortFilterComponent} from './cohort/CohortFilterComponent';
import {ConmedsFilterComponent} from './conmeds/ConmedsFilterComponent';
import {CtDnaFilterComponent} from './ctdna/CtDnaFilterComponent';
import {CvotFilterComponent} from './cvot/CvotFilterComponent';
import {DeathFilterComponent} from './death/DeathFilterComponent';
import {DoseFilterComponent} from './dose/DoseFilterComponent';
import {DoseDiscontinuationFilterComponent} from './dosediscontinuation/DoseDiscontinuationFilterComponent';
import {ExacerbationsFilterComponent} from './exacerbations/ExacerbationsFilterComponent';
import {DoseProportionalityFilterComponent} from './dose-proportionality/DoseProportionalityFilterComponent';
import {ExposureFilterComponent} from './exposure/ExposureFilterComponent';
import {LabsFilterComponent} from './labs/LabsFilterComponent';
import {LiverRiskFactorsFilterComponent} from './liver-risk-factors/LiverRiskFactorsFilterComponent';
import {LiverDiagnosticInvestigationFilterComponent} from './liverdiaginvest/LiverDiagnosticInvestigationFilterComponent';
import {LiverFunctionFilterComponent} from './liverfunction/LiverFunctionFilterComponent';
import {LungFunctionFilterComponent} from './lungfunction/LungFunctionFilterComponent';
import {MedicalHistoryFilterComponent} from './medicalhistory/MedicalHistoryFiltersComponent';
import {NicotineFilterComponent} from './nicotine/NicotineFilterComponent';
import {PatientDataFiltersComponent} from './patientdata/PatientDataFiltersComponent';
import {PkOverallResponseFilterComponent} from './pk-overall-response/PkOverallResponseFilterComponent';
import {PopulationFilterComponent} from './population/PopulationFilterComponent';
import {RecistFilterComponent} from './recist/RecistFilterComponent';
import {RenalFilterComponent} from './renal/RenalFilterComponent';
import {SeriousAesFilterComponent} from './saes/SeriousAesFilterComponent';
import {SurgicalHistoryFilterComponent} from './surgicalhistory/SurgicalHistoryFiltersComponent';
import {TumourResponseFilterComponent} from './tumourresponse/TumourResponseFilterComponent';
import {VitalsFilterComponent} from './vitals/VitalsFilterComponent';

export {PopulationFiltersModel} from './population/PopulationFiltersModel';
export {AesFiltersModel} from './aes/AesFiltersModel';
export {LabsFiltersModel} from './labs/LabsFiltersModel';
export {LungFunctionFiltersModel} from './lungfunction/LungFunctionFiltersModel';
export {ExacerbationsFiltersModel} from './exacerbations/ExacerbationsFiltersModel';
export {VitalsFiltersModel} from './vitals/VitalsFiltersModel';
export {RenalFiltersModel} from './renal/RenalFiltersModel';
export {LiverFunctionFiltersModel} from './liverfunction/LiverFunctionFiltersModel';
export {TumourResponseFiltersModel} from './tumourresponse/TumourResponseFiltersModel';
export {ConmedsFiltersModel} from './conmeds/ConmedsFiltersModel';
export {CohortFiltersModel} from './cohort/CohortFiltersModel';
export {CardiacFiltersModel} from './cardiac/CardiacFiltersModel';
export {DoseFiltersModel} from './dose/DoseFiltersModel';
export {DeathFiltersModel} from './death/DeathFiltersModel';
export {RecistFiltersModel} from './recist/RecistFiltersModel';
export {NicotineFiltersModel} from './nicotine/NicotineFiltersModel';
export {SeriousAesFiltersModel} from './saes/SeriousAesFiltersModel';
export {AlcoholFiltersModel} from './alcohol/AlcoholFiltersModel';
export {DoseDiscontinuationFiltersModel} from './dosediscontinuation/DoseDiscontinuationFiltersModel';
export {MedicalHistoryFiltersModel} from './medicalhistory/MedicalHistoryFiltersModel';
export {LiverDiagnosticInvestigationFiltersModel} from './liverdiaginvest/LiverDiagnosticInvestigationFiltersModel';
export {LiverRiskFactorsFiltersModel} from './liver-risk-factors/LiverRiskFactorsFiltersModel';
export {SurgicalHistoryFiltersModel} from './surgicalhistory/SurgicalHistoryFiltersModel';
export {CIEventsFiltersModel} from './cievents/CIEventsFiltersModel';
export {CerebrovascularFiltersModel} from './cerebrovascular/CerebrovascularFiltersModel';
export {BiomarkersFiltersModel} from './biomarkers/BiomarkersFiltersModel';
export {CvotFiltersModel} from './cvot/CvotFiltersModel';
export {PatientDataFiltersModel} from './patientdata/PatientDataFiltersModel';
export {ExposureFiltersModel} from './exposure/ExposureFiltersModel';
export {DoseProportionalityFiltersModel} from './dose-proportionality/DoseProportionalityFiltersModel';
export {PkOverallResponseFiltersModel} from './pk-overall-response/PkOverallResponseFiltersModel';
export {CtDnaFiltersModel} from './ctdna/CtDnaFiltersModel';

export {AbstractEventFiltersModel} from './AbstractEventFiltersModel';
export {AbstractFiltersModel} from './AbstractFiltersModel';

export {FilterCollectionComponent} from './filtercollection/FilterCollectionComponent';
export {PopulationFilterComponent} from './population/PopulationFilterComponent';
export {AesFilterComponent} from './aes/AesFilterComponent';

export const filterComponents = [
    TimelineFilterComponent,
    AesFilterComponent,
    SeriousAesFilterComponent,
    PopulationFilterComponent,
    ConmedsFilterComponent,
    CohortFilterComponent,
    VitalsFilterComponent,
    PatientDataFiltersComponent,
    LabsFilterComponent,
    RenalFilterComponent,
    LiverFunctionFilterComponent,
    LungFunctionFilterComponent,
    CardiacFiltersComponent,
    DeathFilterComponent,
    DoseFilterComponent,
    DoseDiscontinuationFilterComponent,
    LiverDiagnosticInvestigationFilterComponent,
    MedicalHistoryFilterComponent,
    AlcoholFilterComponent,
    NicotineFilterComponent,
    LiverRiskFactorsFilterComponent,
    RecistFilterComponent,
    ExacerbationsFilterComponent,
    SurgicalHistoryFilterComponent,
    CIEventsFilterComponent,
    CerebrovascularFilterComponent,
    BiomarkersFilterComponent,
    CvotFilterComponent,
    ExposureFilterComponent,
    DoseProportionalityFilterComponent,
    PkOverallResponseFilterComponent,
    TumourResponseFilterComponent,
    CtDnaFilterComponent
];
