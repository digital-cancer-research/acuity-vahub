import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {AboutContainerComponent} from './containers/AboutContainerComponent';
import {AboutComponent} from './AboutComponent';
import {AvailableStudiesComponent} from './availablestudies/AvailableStudiesComponent';
import {StudySelectionComponentModule} from '../studyselection/StudySelectionComponent.module';
import {MessageSubstitutionLine} from 'tslint/lib/verify/lines';
import {ProgressComponentModule} from '../common/loading/ProgressComponent.module';
import {ModalMessageComponentModule} from '../common/modals/modalMessage/ModalMessageComponent.module';
import {FormsModule} from '@angular/forms';
import {CommonPipesModule} from '../common/pipes/CommonPipes.module';
import {CommonModule} from '@angular/common';
import {DetailsTableComponentModule} from '../detailstable/DetailsTableComponent.module';

@NgModule({
    imports: [
        FormsModule,
        CommonPipesModule,
        CommonModule,
        RouterModule,
        StudySelectionComponentModule,
        ProgressComponentModule,
        ModalMessageComponentModule,
        DetailsTableComponentModule
    ],
    exports: [],
    declarations: [
        AboutContainerComponent,
        AboutComponent,
        AvailableStudiesComponent,
    ],
    providers: [],
})
export class AboutComponentContainerModule { }
