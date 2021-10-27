/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
