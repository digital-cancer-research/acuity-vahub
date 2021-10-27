import { Routes } from '@angular/router';
import {TumourRespPriorTherapyComponent} from './prior-therapy/TumourRespPriorTherapyComponent';

export const tumourTherapyRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'prior-therapy'},
    { path: 'prior-therapy', component: TumourRespPriorTherapyComponent}
];

export const tumourTherapyRoutingComponents = [TumourRespPriorTherapyComponent];
