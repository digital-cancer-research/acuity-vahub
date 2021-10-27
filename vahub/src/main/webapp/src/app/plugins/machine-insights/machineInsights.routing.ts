import { Routes } from '@angular/router';

import {QtProlongationComponent} from './components/qt-prolongation/qtProlongationComponent';

export const machineInsightsRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'qt-prolongation'},
    { path: 'qt-prolongation', component: QtProlongationComponent }
];

export const machineInsightsRoutingComponents = [
    QtProlongationComponent
];
