import { Routes } from '@angular/router';
import {ConmedsPlotComponent} from './conmedsPlot/ConmedsPlotComponent';

export const conmedsRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'plot' },
    { path: 'plot', component: ConmedsPlotComponent }
];

export const conmedsRoutingComponents = [ConmedsPlotComponent];
