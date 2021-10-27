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

import {ProgressComponentModule} from '../../common/loading/ProgressComponent.module';
import {RouterModule} from '@angular/router';
import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {SingleSubjectViewComponent} from './SingleSubjectViewComponent';
import {SSVSubjectSearchComponentModule} from './subject-search/SSVSubjectSearchComponent.module';
import {SingleSubjectViewEffects} from './store/effects/SingleSubjectViewEffects';
import { EffectsModule } from '@ngrx/effects';
import {SingleSubjectTabsComponent} from './tab-list-component/SingleSubjectTabsComponent';
import {DoseTabComponent} from './tabs/dose/DoseTabComponent';
import {SingleSubjectViewServiceFactory} from './http/SingleSubjectViewServiceFactory';
import {BaseSingleSubjectViewHttpService} from './http/BaseSingleSubjectViewHttpService';
import {SingleSubjectViewTableComponentModule} from './ssv-table-component/SingleSubjectViewTableComponent.module';
import {AgGridModule} from 'ag-grid-angular/main';
import {DoseDiscontinuationTabComponent} from './tabs/dose-discontinuation/DoseDiscontinuationTabComponent';
import {ConmedsTabComponent} from './tabs/conmeds/ConmedsTabComponent';
import {AdverseEventsTabComponent} from './tabs/adverse-events/AdverseEventsTabComponent';
import {DeathTabComponent} from './tabs/death/DeathTabComponent';
import {SurgicalHistoryTabComponent} from './tabs/surgical-history/SurgicalHistoryTabComponent';
import {SeriousAdverseEventsTabComponent} from './tabs/serious-adverse-evemts/SeriousAdverseEventsTabComponent';
import {NicotineTabComponent} from './tabs/nicotine/NicotineTabComponent';
import {MedicalHistoryTabComponent} from './tabs/medical-history/MedicalHistoryTabComponent';
import {LiverRiskFactorsTabComponent} from './tabs/liver-risk-factors/LiverRiskFactorsTabComponent';
import {LiverDiagInvestTabComponent} from './tabs/liver-diag-invest/LiverDiagInvestTabComponent';
import {ExacerbationsTabComponent} from './tabs/exacerbations/ExacerbationsTabComponent';
import {AlcoholTabComponent} from './tabs/alcohol/AlcoholTabComponent';
import {CardiacTabComponent} from './tabs/cardiac/CardiacTabComponent';
import {TrellisingComponentModule} from '../../common/trellising/trellising';
import {VitalsTabComponent} from './tabs/vitals/VitalsTabComponent';
import {LabsTabComponent} from './tabs/labs/LabsTabComponent';
import {LiverTabComponent} from './tabs/liver/LiverTabComponent';
import {RenalTabComponent} from './tabs/renal/RenalTabComponent';
import {LangfunctionTabComponent} from './tabs/lungunction/LangfunctionTabComponent';
import {SingleSubjectTimelineComponentModule} from './tabs/timeline/SingleSubjectTimelineComponent.module';
import {CommonDirectivesModule} from '../../common/directives/directives.module';
import {SingleSubjectViewSummaryHttpService} from './http/SingleSubjectViewSummaryHttpService';
import {SummaryTabComponentModule} from './tabs/summary/SummaryTabComponent.module';
import {SubjectSummaryTabComponentModule} from './tabs/new-summary/SubjectSummaryTabComponent.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        ProgressComponentModule,
        SSVSubjectSearchComponentModule,
        SingleSubjectViewTableComponentModule,
        EffectsModule.forFeature([SingleSubjectViewEffects]),
        AgGridModule.withComponents([
            SingleSubjectViewComponent,
            SSVSubjectSearchComponentModule,
            SingleSubjectViewTableComponentModule,
            SingleSubjectTabsComponent
        ]),
        TrellisingComponentModule,
        SingleSubjectTimelineComponentModule,
        SummaryTabComponentModule,
        SubjectSummaryTabComponentModule,
        CommonDirectivesModule
    ],
    declarations: [
        SingleSubjectViewComponent,
        SingleSubjectTabsComponent,
        DoseTabComponent,
        DoseDiscontinuationTabComponent,
        DeathTabComponent,
        AdverseEventsTabComponent,
        ConmedsTabComponent,
        AlcoholTabComponent,
        ExacerbationsTabComponent,
        LiverDiagInvestTabComponent,
        LiverRiskFactorsTabComponent,
        MedicalHistoryTabComponent,
        NicotineTabComponent,
        SeriousAdverseEventsTabComponent,
        SurgicalHistoryTabComponent,
        CardiacTabComponent,
        VitalsTabComponent,
        LabsTabComponent,
        LiverTabComponent,
        RenalTabComponent,
        LangfunctionTabComponent
    ],
    exports: [SingleSubjectViewComponent],
    providers: [
        SingleSubjectViewServiceFactory,
        BaseSingleSubjectViewHttpService,
        SingleSubjectViewSummaryHttpService
    ]
})
export class SingleSubjectViewComponentModule {
}
