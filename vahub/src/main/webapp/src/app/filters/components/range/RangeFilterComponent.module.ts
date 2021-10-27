import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import { RangeFilterComponent } from './RangeFilterComponent';
import {ZoombarComponentModule} from '../../../common/zoombar/module';
import {FormsModule} from '@angular/forms';
@NgModule({
    imports: [CommonModule, FormsModule, ZoombarComponentModule],
    exports: [RangeFilterComponent],
    declarations: [RangeFilterComponent],
    providers: [],
})
export class RangeFilterComponentModule { }
