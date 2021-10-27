import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {TrellisingPaginationComponentModule} from '../../../common/trellising/pagination/index';
import {TimelinePaginationComponent} from './TimelinePaginationComponent';
import {TimelinePageSizeComponent} from './TimelinePageSizeComponent';
import {DropdownComponentModule} from '../../../common/dropdown/DropdownComponent.module';

@NgModule({
    imports: [CommonModule, FormsModule, TrellisingPaginationComponentModule, DropdownComponentModule],
    exports: [TimelinePaginationComponent, TimelinePageSizeComponent],
    declarations: [TimelinePaginationComponent, TimelinePageSizeComponent]
})
export class TimelinePaginationComponentModule {
}
