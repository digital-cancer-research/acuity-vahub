import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

import {CommonPipesModule} from '../../pipes/index';
import {XAxisLabelComponent} from './xaxis/XAxisLabelComponent';
import {YAxisLabelComponent} from './yaxis/YAxisLabelComponent';
import {TableAxisLabelComponent} from './tableaxis/TableAxisLabelComponent';
import {TableXAxisLabelComponent} from './tableaxis/TableXAxisLabelComponent';
import {NewXAxisLabelComponent} from './newxaxis/XAxisLabelComponent';

import {AxisLabelService} from './AxisLabelService';
import {XAxisLabelService} from './xaxis/XAxisLabelService';
import {NewXAxisLabelService} from './newxaxis/XAxisLabelService';
import {NewYAxisLabelComponent} from './newyaxis/YAxisLabelComponent';
import {TimepointSelectorComponent} from './timepointSelector/TimepointSelectorComponent';
import {ColorByModule} from './ColorBy.module';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        CommonPipesModule,
        ColorByModule
    ],
    exports: [
        XAxisLabelComponent,
        NewXAxisLabelComponent,
        NewYAxisLabelComponent,
        YAxisLabelComponent,
        TableAxisLabelComponent,
        TableXAxisLabelComponent,
        TimepointSelectorComponent,
        ColorByModule],
    declarations: [
        XAxisLabelComponent,
        NewXAxisLabelComponent,
        NewYAxisLabelComponent,
        YAxisLabelComponent,
        TableAxisLabelComponent,
        TableXAxisLabelComponent,
        TimepointSelectorComponent
    ],
    providers: [AxisLabelService, XAxisLabelService, NewXAxisLabelService],
})
export class AxisLabelComponentModule {
}
