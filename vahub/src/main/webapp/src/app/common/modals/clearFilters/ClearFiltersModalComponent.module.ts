import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {ClearFiltersModalComponent} from './ClearFiltersModalComponent';

@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [ClearFiltersModalComponent],
    declarations: [ClearFiltersModalComponent],
    providers: [],
})
export class ClearFiltersModalComponentModule {
}
