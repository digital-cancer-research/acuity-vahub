import { Routes } from '@angular/router';
import {VitalsPopulationBoxPlotComponent} from './vitalsPopulationBoxPlot/VitalsPopulationBoxPlotComponent';

export const vitalsRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'box-plot' },
    { path: 'box-plot', component: VitalsPopulationBoxPlotComponent }
];

export const vitalsRoutingComponents = [VitalsPopulationBoxPlotComponent];
