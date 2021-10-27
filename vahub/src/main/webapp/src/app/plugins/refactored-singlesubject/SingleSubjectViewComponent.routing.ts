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

import {Routes} from '@angular/router';
import {SummaryTabComponent} from './tabs/summary/SummaryTabComponent';
import {DoseTabComponent} from './tabs/dose/DoseTabComponent';
import {DoseDiscontinuationTabComponent} from './tabs/dose-discontinuation/DoseDiscontinuationTabComponent';
import {ConmedsTabComponent} from './tabs/conmeds/ConmedsTabComponent';
import {AdverseEventsTabComponent} from './tabs/adverse-events/AdverseEventsTabComponent';
import {DeathTabComponent} from './tabs/death/DeathTabComponent';
import {SeriousAdverseEventsTabComponent} from './tabs/serious-adverse-evemts/SeriousAdverseEventsTabComponent';
import {ExacerbationsTabComponent} from './tabs/exacerbations/ExacerbationsTabComponent';
import {LiverDiagInvestTabComponent} from './tabs/liver-diag-invest/LiverDiagInvestTabComponent';
import {LiverRiskFactorsTabComponent} from './tabs/liver-risk-factors/LiverRiskFactorsTabComponent';
import {MedicalHistoryTabComponent} from './tabs/medical-history/MedicalHistoryTabComponent';
import {NicotineTabComponent} from './tabs/nicotine/NicotineTabComponent';
import {SurgicalHistoryTabComponent} from './tabs/surgical-history/SurgicalHistoryTabComponent';
import {AlcoholTabComponent} from './tabs/alcohol/AlcoholTabComponent';
import {CardiacTabComponent} from './tabs/cardiac/CardiacTabComponent';
import {VitalsTabComponent} from './tabs/vitals/VitalsTabComponent';
import {LabsTabComponent} from './tabs/labs/LabsTabComponent';
import {LiverTabComponent} from './tabs/liver/LiverTabComponent';
import {RenalTabComponent} from './tabs/renal/RenalTabComponent';
import {LangfunctionTabComponent} from './tabs/lungunction/LangfunctionTabComponent';
import {SingleSubjectTimelineComponent} from './tabs/timeline/SingleSubjectTimelineComponent';
import {SubjectSummaryTabComponent} from './tabs/new-summary/SubjectSummaryTabComponent';

export const SingleSubjectViewRoutes: Routes = [
    {path: '', pathMatch: 'full', redirectTo: 'summary-tab'},
    {path: 'summary-tab', component: SummaryTabComponent},
    {path: 'new-summary-tab', component: SubjectSummaryTabComponent},
    {path: 'ae-tab', component: AdverseEventsTabComponent},
    {path: 'sae-tab', component: SeriousAdverseEventsTabComponent},
    {path: 'conmeds-tab', component: ConmedsTabComponent},
    {path: 'exacerbations-tab', component: ExacerbationsTabComponent},
    {path: 'dose-tab', component: DoseTabComponent},
    {path: 'dose-discontinuation-tab', component: DoseDiscontinuationTabComponent},
    {path: 'death-tab', component: DeathTabComponent},
    {path: 'medicalhistory-tab', component: MedicalHistoryTabComponent},
    {path: 'liver-risk-factors-tab', component: LiverRiskFactorsTabComponent},
    {path: 'surgicalhistory-tab', component: SurgicalHistoryTabComponent},
    {path: 'liverdiag-tab', component: LiverDiagInvestTabComponent},
    {path: 'nicotine-tab', component: NicotineTabComponent},
    {path: 'alcohol-tab', component: AlcoholTabComponent},
    {path: 'lab-tab', component: LabsTabComponent},
    {path: 'vitals-tab', component: VitalsTabComponent},
    {path: 'hys-law', component: LiverTabComponent},
    {path: 'renal-tab', component: RenalTabComponent},
    {path: 'lungfunction-tab', component: LangfunctionTabComponent},
    {path: 'cardiac-tab', component: CardiacTabComponent},
    {path: 'timeline-tab', component: SingleSubjectTimelineComponent},
];
