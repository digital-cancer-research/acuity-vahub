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

import { NgModule } from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import { SpirometryTrackComponent } from './SpirometryTrackComponent';
import {SharedTrackComponentsModule} from '../../chart/SharedTrackComponents.module';
import {BarChartEventService} from '../../chart/bar/BarChartEventService';
import {LineChartEventService} from '../../chart/line/LineChartEventService';
import {SpirometrySummaryTrackModel} from './SpirometrySummaryTrackModel';
import {SpirometryCategoryTrackModel} from './SpirometryCategoryTrackModel';
import {SpirometryDetailTrackModel} from './SpirometryDetailTrackModel';
import {SpirometryTrackUtils} from './SpirometryTrackUtils';

@NgModule({
    imports: [CommonModule, FormsModule, SharedTrackComponentsModule],
    exports: [SpirometryTrackComponent],
    declarations: [SpirometryTrackComponent],
    providers: [
        SpirometrySummaryTrackModel,
        SpirometryCategoryTrackModel,
        SpirometryDetailTrackModel,
        BarChartEventService,
        LineChartEventService,
        SpirometryTrackUtils
    ]
})
export class SpirometryTrackComponentModule { }
