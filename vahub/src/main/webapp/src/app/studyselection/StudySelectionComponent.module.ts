import { NgModule } from '@angular/core';
import {StudySelectionComponent} from './StudySelectionComponent';
import {StudyListComponent} from '../studylist/StudyListComponent';
import {StudySelectionControlsComponent} from '../studylistcontrols/StudyListControlsComponent';
import {CommonModule} from '@angular/common';
import {ProgressComponentModule} from '../common/loading/ProgressComponent.module';
import {ModalMessageComponentModule} from '../common/modals/modalMessage/ModalMessageComponent.module';
import {CommonPipesModule} from '../common/pipes/CommonPipes.module';
import {FormsModule} from '@angular/forms';

@NgModule({
    imports: [
        FormsModule,
        CommonModule,
        CommonPipesModule,
        ProgressComponentModule,
        ModalMessageComponentModule
    ],
    exports: [
        StudySelectionComponent,
        StudyListComponent,
        StudySelectionControlsComponent
    ],
    declarations: [
        StudySelectionComponent,
        StudyListComponent,
        StudySelectionControlsComponent
    ],
    providers: [],
})

export class StudySelectionComponentModule { }
