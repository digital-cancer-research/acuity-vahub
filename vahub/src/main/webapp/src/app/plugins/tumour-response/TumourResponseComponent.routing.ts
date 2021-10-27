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
