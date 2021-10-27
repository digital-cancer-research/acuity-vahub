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
import {AesTableAgGridStrategy} from './aestable/ag-grid-strategy/AesTableAgGridStrategy';
import {AesTableServiceCommunity} from './aestable/ag-grid-strategy/AesTableServiceCommunity';
import {AesTableServiceEnterprise} from './aestable/ag-grid-strategy/AesTableServiceEnterprise';

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
        AesTableAgGridStrategy,
        AesTableServiceCommunity,
        AesTableServiceEnterprise
    ],
    exports: [AEsComponent, AEsTabsComponent]
})
export class AEsComponentModule {
}
