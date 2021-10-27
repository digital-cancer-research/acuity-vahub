import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GridComponent } from './GridComponent';
import { FormsModule } from '@angular/forms';
import {PlotComponentModule} from './plot/index';
import {YAxisRangeService} from './range/yaxis/YAxisRangeService';
import {XAxisRangeService} from './range/xaxis/XAxisRangeService';
import {CommonPipesModule} from '../../pipes/index';
@NgModule({
    imports: [ CommonModule, FormsModule, CommonPipesModule, PlotComponentModule],
    exports: [GridComponent],
    declarations: [GridComponent],
    providers: [XAxisRangeService, YAxisRangeService],
})
export class GridComponentModule { }
