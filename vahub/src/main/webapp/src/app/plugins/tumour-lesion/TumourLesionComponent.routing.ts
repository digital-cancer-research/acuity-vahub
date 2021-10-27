import { Routes } from '@angular/router';
import {TLDiametersComponent} from './tl-diameters/TLDiametersComponent';
import {CanActivateTumourResponse} from '../tumour-response/CanActivateTumourResponse';

export const tumourLesionRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'target-lesion-diameters'},
    { path: 'target-lesion-diameters', component: TLDiametersComponent, canActivate: [CanActivateTumourResponse]}
];

export const tumourLesionRoutingComponents = [TLDiametersComponent];
