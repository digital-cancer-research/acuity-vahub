import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import { TimelineContextMenuComponent } from './TimelineContextMenuComponent';
import {TimelineContextMenuDirective} from './TimelineContextMenuDirective';
import {TimelineContextMenuModel} from './TimelineContextMenuModel';

@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [TimelineContextMenuComponent, TimelineContextMenuDirective],
    declarations: [TimelineContextMenuComponent, TimelineContextMenuDirective],
    providers: [TimelineContextMenuModel],
})
export class TimelineContextMenuComponentModule { }
