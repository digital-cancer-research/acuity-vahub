import { Routes } from '@angular/router';
import {CtDNAPlotComponent} from './ctDNAPlot/CtDNAPlotComponent';

export const ctDNARoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'ctDNA-plot' },
    { path: 'ctDNA-plot', component: CtDNAPlotComponent }
];

export const ctDNARoutingComponents = [CtDNAPlotComponent];
