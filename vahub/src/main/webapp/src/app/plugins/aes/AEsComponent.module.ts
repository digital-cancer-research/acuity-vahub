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
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {RouterModule} from '@angular/router';
import {AgGridModule} from 'ag-grid-angular/main';

import {AEsComponent} from './AEsComponent';
import {AesTableComponent} from './aestable/AesTableComponent';
import {AesStackedBarChartComponent} from './stackedbarchart/AesStackedBarChartComponent';
import {AesOverTimeComponent} from './overtime/AesOverTimeComponent';
import {AEsSummaryComponent as spotfireAEsSummaryComponent} from './spotfire/AEsSummaryComponent';
import {TolerabilityComponent} from './spotfire/TolerabilityComponent';
import {DropdownComponentModule, SpotfireComponentModule} from '../../common/module';
import {TrellisingComponentModule} from '../../common/trellising/trellising';
import {AEsTabsComponent} from './aes-tabs/AEsTabsComponent';
import {CommonDirectivesModule} from '../../common/directives/directives.module';
import {ModalMessageComponentModule} from '../../common/modals/modalMessage/ModalMessageComponent.module';
import {AesSummaryHttpService} from '../../data/aes';
import {AEsSummaryComponent} from './aes-summary/AEsSummaryComponent';
import {AEsChordDiagramComponent} from './chord-diagram/AEsChordDiagramComponent';
import {AesTableServiceCommunity} from './aestable/AesTableServiceCommunity';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        RouterModule,
        AgGridModule.withComponents([
            AEsComponent,
            AesTableComponent,
            AesStackedBarChartComponent,
            AesOverTimeComponent,
            TolerabilityComponent,
            AEsTabsComponent
        ]),
        DropdownComponentModule,
        TrellisingComponentModule,
        SpotfireComponentModule,
        CommonDirectivesModule,
        ModalMessageComponentModule
    ],
    declarations: [
        AEsComponent,
        AesTableComponent,
        AesStackedBarChartComponent,
        AesOverTimeComponent,
        AEsSummaryComponent,
        spotfireAEsSummaryComponent,
        TolerabilityComponent,
        AEsTabsComponent,
        AEsChordDiagramComponent
    ],
    providers: [
        AesSummaryHttpService,
        AesTableServiceCommunity
    ],
    exports: [AEsComponent, AEsTabsComponent]
})
export class AEsComponentModule {
}
