import { Routes } from '@angular/router';
import {ExacerbationsOverTimeComponent} from './exacerbationsOverTime/ExacerbationsOverTimeComponent';
import {ExacerbationsOnSetComponent} from './exacerbationsonset/ExacerbationsOnSetComponent';
import { ExacerbationsCountComponent } from './exacerbationscounts/ExacerbationsCountComponent';

export const exacerbationsRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'exacerbations-counts'},
    { path: 'exacerbations-over-time', component: ExacerbationsOverTimeComponent },
    { path: 'exacerbations-onset', component: ExacerbationsOnSetComponent },
    { path: 'exacerbations-counts', component: ExacerbationsCountComponent}
];

export const exacerbationsRoutingComponents = [
    ExacerbationsOverTimeComponent,
    ExacerbationsOnSetComponent,
    ExacerbationsCountComponent
];
