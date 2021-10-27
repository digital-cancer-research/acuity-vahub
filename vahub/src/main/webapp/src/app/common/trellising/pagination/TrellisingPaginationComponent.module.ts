import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {TrellisingPaginationComponent} from './TrellisingPaginationComponent';

@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [TrellisingPaginationComponent],
    declarations: [TrellisingPaginationComponent]
})
export class TrellisingPaginationComponentModule {
}
