import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {SpotfireComponent} from './SpotfireComponent';
import {StudyService} from '../StudyService';

@NgModule({
    imports: [
        CommonModule,
        FormsModule
    ],
    exports: [SpotfireComponent],
    declarations: [SpotfireComponent],
    providers: [StudyService],
})
export class SpotfireComponentModule {
}
