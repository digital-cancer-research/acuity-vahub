import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ProgressComponent} from './textloader/ProgressComponent';
import {SimpleLoadingComponent} from './simpleloading/SimpleLoadingComponent';
import {ProgressService} from './textloader/ProgressService';

/**
 * @module for loaders
 */
@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [ProgressComponent, SimpleLoadingComponent],
    declarations: [ProgressComponent, SimpleLoadingComponent],
    providers: [ProgressService]
})
export class ProgressComponentModule {
}
