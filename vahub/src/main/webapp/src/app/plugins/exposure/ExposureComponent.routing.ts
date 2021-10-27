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
