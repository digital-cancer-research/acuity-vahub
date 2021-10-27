import {CardiacBoxPlotComponent} from './boxplot/CardiacBoxPlotComponent';
import { Routes } from '@angular/router';

export const cardiacRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'boxplot' },
    { path: 'boxplot', component: CardiacBoxPlotComponent }
];

export const cardiacRoutingComponents = [CardiacBoxPlotComponent];
