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
import {Response} from '@angular/http';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {SessionEventService} from '../../../session/event/SessionEventService';
import * as utils from '../../../common/utils/Utils';
import {AesFiltersModel} from '../../../filters/dataTypes/aes/AesFiltersModel';
import {AlcoholFiltersModel} from '../../../filters/dataTypes/alcohol/AlcoholFiltersModel';
import {CardiacFiltersModel} from '../../../filters/dataTypes/cardiac/CardiacFiltersModel';
import {ConmedsFiltersModel} from '../../../filters/dataTypes/conmeds/ConmedsFiltersModel';
import {DeathFiltersModel} from '../../../filters/dataTypes/death/DeathFiltersModel';
import {DoseFiltersModel} from '../../../filters/dataTypes/dose/DoseFiltersModel';
import {DoseDiscontinuationFiltersModel} from '../../../filters/dataTypes/dosediscontinuation/DoseDiscontinuationFiltersModel';
import {ExacerbationsFiltersModel} from '../../../filters/dataTypes/exacerbations/ExacerbationsFiltersModel';
import {LabsFiltersModel} from '../../../filters/dataTypes/labs/LabsFiltersModel';
import {LiverRiskFactorsFiltersModel} from '../../../filters/dataTypes/liver-risk-factors/LiverRiskFactorsFiltersModel';
import {LiverDiagnosticInvestigationFiltersModel} from '../../../filters/dataTypes/liverdiaginvest';
import {LungFunctionFiltersModel} from '../../../filters/dataTypes/lungfunction/LungFunctionFiltersModel';
import {MedicalHistoryFiltersModel} from '../../../filters/dataTypes/medicalhistory/MedicalHistoryFiltersModel';
import {NicotineFiltersModel} from '../../../filters/dataTypes/nicotine/NicotineFiltersModel';
import {RenalFiltersModel} from '../../../filters/dataTypes/renal/RenalFiltersModel';
import {SeriousAesFiltersModel} from '../../../filters/dataTypes/saes/SeriousAesFiltersModel';
import {SurgicalHistoryFiltersModel} from '../../../filters/dataTypes/surgicalhistory/SurgicalHistoryFiltersModel';
import {VitalsFiltersModel} from '../../../filters/dataTypes/vitals/VitalsFiltersModel';
import {TabId} from '../../../common/trellising/store/ITrellising';

@Injectable()
export class BaseSingleSubjectViewHttpService {
    constructor(protected http: HttpClient,
                protected sessionEventService: SessionEventService,
                protected aesFiltersModel: AesFiltersModel,
                protected alcoholFiltersModel: AlcoholFiltersModel,
                protected cardiacFiltersModel: CardiacFiltersModel,
                protected conmedsFiltersModel: ConmedsFiltersModel,
                protected deathFiltersModel: DeathFiltersModel,
                protected doseFiltersModel: DoseFiltersModel,
                protected doseDiscontinuationFiltersModel: DoseDiscontinuationFiltersModel,
                protected exacerbationsFiltersModel: ExacerbationsFiltersModel,
                protected labsFiltersModel: LabsFiltersModel,
                protected liverRiskFactorsFiltersModel: LiverRiskFactorsFiltersModel,
                protected liverDiagnosticInvestigationFiltersModel: LiverDiagnosticInvestigationFiltersModel,
                protected lungFunctionFiltersModel: LungFunctionFiltersModel,
                protected medicalHistoryFiltersModel: MedicalHistoryFiltersModel,
                protected nicotineFiltersModel: NicotineFiltersModel,
                protected renalFiltersModel: RenalFiltersModel,
                protected seriousAesFiltersModel: SeriousAesFiltersModel,
                protected surgicalHistoryFiltersModel: SurgicalHistoryFiltersModel,
                protected vitalsFiltersModel: VitalsFiltersModel
    ) {
    }

    getTableData(subjectId: string, tabId: string): Observable<Response> {
        const path = utils.getSingleSubjectViewTabEndpoint(tabId);

        const postData: any = {
            subjectId: subjectId,
            datasets: this.sessionEventService.currentSelectedDatasets,
            eventFilters: this.getEventFiltersData(tabId)
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as Response);
    }

    getEventFiltersData(tabId: string): any {
        switch (tabId) {
            case TabId.SINGLE_SUBJECT_AE_TAB:
                return this.aesFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_ALCOHOL_TAB:
                return this.alcoholFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_CARDIAC_LINEPLOT:
                return this.cardiacFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_CONMEDS_TAB:
                return this.conmedsFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_DEATH_TAB:
                return this.deathFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_DOSE_TAB:
                return this.doseFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_DOSE_DISCONTINUATION_TAB:
                return this.doseDiscontinuationFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_EXACERBATIONS_TAB:
                return this.exacerbationsFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_LAB_LINEPLOT:
                return this.labsFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_LIVER_RISK_FACTORS_TAB:
                return this.liverRiskFactorsFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_LIVER_DIAG_INVEST_TAB:
                return this.liverDiagnosticInvestigationFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_LUNG_LINEPLOT:
                return this.lungFunctionFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_MEDICAL_HISTORY_TAB:
                return this.medicalHistoryFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_NICOTINE_TAB:
                return this.nicotineFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_RENAL_LINEPLOT:
                return this.renalFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_SAE_TAB:
                return this.seriousAesFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_SURGICAL_HISTORY_TAB:
                return this.surgicalHistoryFiltersModel.transformFiltersToServer();
            case TabId.SINGLE_SUBJECT_VITALS_LINEPLOT:
                return this.vitalsFiltersModel.transformFiltersToServer();
            default:
                return {};
        }
    }
}
