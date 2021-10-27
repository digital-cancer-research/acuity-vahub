import {NgModule} from '@angular/core';
import {AgGridModule} from 'ag-grid-angular/main';
import {CommonModule} from '@angular/common';
import {DetailsOnDemandComponent} from './DetailsOnDemandComponent';
import {DetailsOnDemandHeightService} from './services/DetailsOnDemandHeightService';
import {DetailsOnDemandEventTableService} from './services/DetailsOnDemandEventTableService';
import {DetailsOnDemandSubjectTableService} from './services/DetailsOnDemandSubjectTableService';
import {DetailsOnDemandSummaryService} from './services/DetailsOnDemandSummaryService';
import {ModalMessageComponentModule} from '../../modals/modalMessage/ModalMessageComponent.module';
import {FormsModule} from '@angular/forms';
import {ProgressComponentModule} from '../../loading/ProgressComponent.module';
import {AdverseEventsColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/adverse-events/AdverseEventsColumnModel';
import {ConmedsColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/conmeds/ConmedsColumnModel';
import {ExacerbationsColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/exacerbations/ExacerbationsColumnModel';
import {LabsColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/labs/LabsColumnModel';
import {RenalColumnsModel} from '../../../plugins/refactored-singlesubject/datatypes/renal/RenalColumnsModel';
import {VitalsColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/vitals/VitalsColumnModel';
import {CardiacColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/cardiac/CardiacColumnModel';
import {CerebrovascularColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/cerebrovascular/CerebrovascularColumnModel';
import {CIEventsColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/cievents/CIEventsColumnModel';
import {CvotColumnModel} from '../../../plugins/refactored-singlesubject/datatypes/cvot/CvotColumnModel';
import {DetailsOnDemandEffects} from './store/effects/DetailsOnDemandEffects';
import {EffectsModule} from '@ngrx/effects';
import {MultiDetailsOnDemandComponent} from './multiDetailsOnDemand/MultiDetailsOnDemandComponent';
import {CommonPipesModule} from '../../pipes';
import {QTProlongationColumnModel} from './services/model/QTProlongationColumnModel';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ModalMessageComponentModule,
        AgGridModule.withComponents([DetailsOnDemandComponent]),
        ProgressComponentModule,
        EffectsModule.forFeature([DetailsOnDemandEffects]),
        CommonPipesModule
    ],
    exports: [DetailsOnDemandComponent, MultiDetailsOnDemandComponent],
    declarations: [DetailsOnDemandComponent, MultiDetailsOnDemandComponent],
    providers: [
        AdverseEventsColumnModel,
        ConmedsColumnModel,
        ExacerbationsColumnModel,
        LabsColumnModel,
        RenalColumnsModel,
        VitalsColumnModel,
        CardiacColumnModel,
        CIEventsColumnModel,
        CerebrovascularColumnModel,
        CvotColumnModel,
        QTProlongationColumnModel,
        DetailsOnDemandSummaryService,
        DetailsOnDemandEventTableService,
        DetailsOnDemandSubjectTableService,
        DetailsOnDemandHeightService]
})
export class DetailsOnDemandComponentModule {
}
