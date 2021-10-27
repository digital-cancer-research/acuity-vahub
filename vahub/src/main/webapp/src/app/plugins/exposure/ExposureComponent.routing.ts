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

import { Routes } from '@angular/router';

import {ExposureSpotfireComponent} from './spotfire/ExposureSpotfireComponent';
import {AnalyteConcentrationComponent} from './analyte-concentration/AnalyteConcentrationComponent';
import {CanActivateExposureSpotfire} from './spotfire/CanActivateExposureSpotfire';

export const exposureRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'spotfire'},
    { path: 'spotfire', component: ExposureSpotfireComponent, canActivate: [CanActivateExposureSpotfire] },
    { path: 'analyte-concentration', component: AnalyteConcentrationComponent },
];

export const exposureRoutingComponents = [ExposureSpotfireComponent];
