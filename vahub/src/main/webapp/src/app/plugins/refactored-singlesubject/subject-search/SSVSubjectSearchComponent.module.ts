import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {SSVSubjectSearchComponent} from './SSVSubjectSearchComponent';
import {SingleSubjectSearchInputComponent} from './single-subject-search-input/SingleSubjectSearchInputComponent';

@NgModule({
    imports: [
        CommonModule,
        FormsModule
    ],
    declarations: [
        SSVSubjectSearchComponent,
        SingleSubjectSearchInputComponent
    ],
    exports: [SSVSubjectSearchComponent],
    providers: [

    ]
})
export class SSVSubjectSearchComponentModule {

}
