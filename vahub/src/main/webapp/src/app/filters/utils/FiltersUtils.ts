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

import {Injectable} from '@angular/core';
import {
    PopulationFiltersModel,
    AesFiltersModel,
    CardiacFiltersModel,
    ConmedsFiltersModel,
    DoseFiltersModel,
    LabsFiltersModel,
    LiverFunctionFiltersModel,
    LungFunctionFiltersModel,
    ExacerbationsFiltersModel,
    RenalFiltersModel,
    VitalsFiltersModel,
    PatientDataFiltersModel,
    DeathFiltersModel,
    SeriousAesFiltersModel,
    DoseDiscontinuationFiltersModel,
    AlcoholFiltersModel,
    RecistFiltersModel,
    LiverDiagnosticInvestigationFiltersModel,
    LiverRiskFactorsFiltersModel,
    SurgicalHistoryFiltersModel,
    NicotineFiltersModel,
    CIEventsFiltersModel,
    CerebrovascularFiltersModel,
    MedicalHistoryFiltersModel,
    BiomarkersFiltersModel,
    CvotFiltersModel,
    CohortFiltersModel,
    ExposureFiltersModel,
    CtDnaFiltersModel, PkOverallResponseFiltersModel
} from '../dataTypes/module';
import {FilterId, TabId} from '../../common/trellising/store/ITrellising';
import {TrackName} from '../../plugins/timeline/store/ITimeline';
import {AbstractFiltersModel} from '../dataTypes/AbstractFiltersModel';
import {TumourResponseFiltersModel} from '../dataTypes/tumourresponse/TumourResponseFiltersModel';
import {DoseProportionalityFiltersModel} from '../dataTypes/dose-proportionality/DoseProportionalityFiltersModel';

/**
 * Basic utils for http calls to server
 */
@Injectable()
export class FiltersUtils {
    private timelineFilterModels = [
        this.aesFiltersModel,
        this.doseFiltersModel,
        this.labsFiltersModel,
        this.conmedsFiltersModel,
        this.vitalsFiltersModel,
        this.patientDataFiltersModel,
        this.cardiacFiltersModel,
        this.lungFunctionFiltersModel,
        this.exacerbationsFiltersModel
    ];

    private eventFilterModels = [
        this.aesFiltersModel,
        this.conmedsFiltersModel,
        this.seriousAesFiltersModel,
        this.labsFiltersModel,
        this.vitalsFiltersModel,
        this.patientDataFiltersModel,
        this.cardiacFiltersModel,
        this.liverFunctionFiltersModel,
        this.renalFiltersModel,
        this.lungFunctionFiltersModel,
        this.exacerbationsFiltersModel,
        this.recistFiltersModel,
        this.doseDiscontinuationFiltersModel,
        this.doseFiltersModel,
        this.medicalHistoryFiltersModel,
        this.liverDiagnosticInvestigationFiltersModel,
        this.alcoholFiltersModel,
        this.liverRiskFactorsFiltersModel,
        this.surgicalHistoryFiltersModel,
        this.deathFiltersModel,
        this.nicotineFiltersModel,
        this.ciEventsFiltersModel,
        this.cerebrovascularFiltersModel,
        this.biomarkersFiltersModel,
        this.cvotFiltersModel,
        this.exposureFiltersModel,
        this.doseProportionalityFiltersModel,
        this.pkOverallResponseFiltersModel,
        this.tumourResponseModel,
        this.ctDnaFiltersModel
    ];

    constructor(public populationFiltersModel: PopulationFiltersModel,
                public aesFiltersModel: AesFiltersModel,
                public seriousAesFiltersModel: SeriousAesFiltersModel,
                public cardiacFiltersModel: CardiacFiltersModel,
                public conmedsFiltersModel: ConmedsFiltersModel,
                public cohortFiltersModel: CohortFiltersModel,
                public doseFiltersModel: DoseFiltersModel,
                public labsFiltersModel: LabsFiltersModel,
                public liverFunctionFiltersModel: LiverFunctionFiltersModel,
                public lungFunctionFiltersModel: LungFunctionFiltersModel,
                public exacerbationsFiltersModel: ExacerbationsFiltersModel,
                public renalFiltersModel: RenalFiltersModel,
                public deathFiltersModel: DeathFiltersModel,
                public vitalsFiltersModel: VitalsFiltersModel,
                public patientDataFiltersModel: PatientDataFiltersModel,
                public doseDiscontinuationFiltersModel: DoseDiscontinuationFiltersModel,
                public medicalHistoryFiltersModel: MedicalHistoryFiltersModel,
                public liverDiagnosticInvestigationFiltersModel: LiverDiagnosticInvestigationFiltersModel,
                public alcoholFiltersModel: AlcoholFiltersModel,
                public liverRiskFactorsFiltersModel: LiverRiskFactorsFiltersModel,
                public surgicalHistoryFiltersModel: SurgicalHistoryFiltersModel,
                public nicotineFiltersModel: NicotineFiltersModel,
                public recistFiltersModel: RecistFiltersModel,
                public ciEventsFiltersModel: CIEventsFiltersModel,
                public biomarkersFiltersModel: BiomarkersFiltersModel,
                public cerebrovascularFiltersModel: CerebrovascularFiltersModel,
                public cvotFiltersModel: CvotFiltersModel,
                public exposureFiltersModel: ExposureFiltersModel,
                public doseProportionalityFiltersModel: DoseProportionalityFiltersModel,
                public pkOverallResponseFiltersModel: PkOverallResponseFiltersModel,
                public tumourResponseModel: TumourResponseFiltersModel,
                public ctDnaFiltersModel: CtDnaFiltersModel) {
    }

    public getFilterModelById(id: any): any {
        let filtersModel: any;
        switch (id) {
            case FilterId.POPULATION:
                filtersModel = this.populationFiltersModel;
                break;
            case FilterId.SAE:
                filtersModel = this.seriousAesFiltersModel;
                break;
            case FilterId.AES:
            case TrackName.AES:
                filtersModel = this.aesFiltersModel;
                break;
            case FilterId.CIEVENTS:
                filtersModel = this.ciEventsFiltersModel;
                break;
            case FilterId.CARDIAC:
            case TrackName.ECG:
                filtersModel = this.cardiacFiltersModel;
                break;
            case FilterId.CONMEDS:
            case TrackName.CONMEDS:
                filtersModel = this.conmedsFiltersModel;
                break;
            case FilterId.COHORT:
                filtersModel = this.cohortFiltersModel;
                break;
            case FilterId.DOSE:
            case TrackName.DOSE:
                filtersModel = this.doseFiltersModel;
                break;
            case FilterId.DOSE_DISCONTINUATION:
                filtersModel = this.doseDiscontinuationFiltersModel;
                break;
            case FilterId.ALCOHOL:
                filtersModel = this.alcoholFiltersModel;
                break;
            case FilterId.LAB:
            case TrackName.LABS:
                filtersModel = this.labsFiltersModel;
                break;
            case FilterId.LIVER:
                filtersModel = this.liverFunctionFiltersModel;
                break;
            case TrackName.SPIROMETRY:
            case FilterId.LUNG_FUNCTION:
                filtersModel = this.lungFunctionFiltersModel;
                break;
            case FilterId.EXACERBATIONS:
            case TrackName.EXACERBATION:
                filtersModel = this.exacerbationsFiltersModel;
                break;
            case FilterId.RENAL:
                filtersModel = this.renalFiltersModel;
                break;
            case FilterId.DEATH:
                filtersModel = this.deathFiltersModel;
                break;
            case FilterId.VITALS:
            case TrackName.VITALS:
                filtersModel = this.vitalsFiltersModel;
                break;
            case FilterId.PATIENT_REPORTED_DATA:
            case TrackName.PRD:
                filtersModel = this.patientDataFiltersModel;
                break;
            case FilterId.RECIST:
            case TabId.TUMOUR_RESPONSE_WATERFALL_PLOT:
                filtersModel = this.recistFiltersModel;
                break;
            case FilterId.MEDICAL_HISTORY:
                filtersModel = this.medicalHistoryFiltersModel;
                break;
            case FilterId.LIVER_DIAGNOSTIC_INVESTIGATION:
                filtersModel = this.liverDiagnosticInvestigationFiltersModel;
                break;
            case FilterId.LIVER_RISK_FACTORS:
                filtersModel = this.liverRiskFactorsFiltersModel;
                break;
            case FilterId.SURGICAL_HISTORY:
                filtersModel = this.surgicalHistoryFiltersModel;
                break;
            case FilterId.NICOTINE:
                filtersModel = this.nicotineFiltersModel;
                break;
            case FilterId.CEREBROVASCULAR:
                filtersModel = this.cerebrovascularFiltersModel;
                break;
            case FilterId.BIOMARKERS:
                filtersModel = this.biomarkersFiltersModel;
                break;
            case FilterId.CVOT:
                filtersModel = this.cvotFiltersModel;
                break;
            case FilterId.EXPOSURE:
                filtersModel = this.exposureFiltersModel;
                break;
            case FilterId.DOSE_PROPORTIONALITY:
                filtersModel = this.doseProportionalityFiltersModel;
                break;
            case FilterId.PK_RESULT_OVERALL_RESPONSE:
                filtersModel = this.pkOverallResponseFiltersModel;
                break;
            case FilterId.TUMOUR_RESPONSE:
                filtersModel = this.tumourResponseModel;
                break;
            case FilterId.CTDNA:
                filtersModel = this.ctDnaFiltersModel;
                break;
            default:
                break;
        }
        return filtersModel;
    }

    public getAvailableEventFilterModels(): any {
        return this.eventFilterModels.filter((eventFilterModel: AbstractFiltersModel) => {
            return eventFilterModel.isVisible();
        });
    }

    public getAvailableTimelineEventFilterModels(): any {
        return this.timelineFilterModels.filter((eventFilterModel: AbstractFiltersModel) => {
            return eventFilterModel.isVisible();
        });
    }
}
