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
import {OncologySpotfireComponent} from './spotfire/OncologySpotfireComponent';
import {TumourRespWaterfallComponent} from './waterfall-plot/TumourRespWaterfallComponent';
import {TLDiametersComponent} from '../tumour-lesion/tl-diameters/TLDiametersComponent';
import {TumourRespPriorTherapyComponent} from '../tumour-therapy/prior-therapy/TumourRespPriorTherapyComponent';
import {CanActivateTumourResponse} from './CanActivateTumourResponse';

export const tumourResponseRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'waterfall'},
    { path: 'spotfire', component: OncologySpotfireComponent },
    { path: 'waterfall', component: TumourRespWaterfallComponent, canActivate: [CanActivateTumourResponse]},
    { path: 'target-lesion-diameters', component: TLDiametersComponent, canActivate: [CanActivateTumourResponse]},
    { path: 'prior-therapy', component: TumourRespPriorTherapyComponent}
];

export const tumourResponseRoutingComponents = [OncologySpotfireComponent, TumourRespWaterfallComponent, TLDiametersComponent];
