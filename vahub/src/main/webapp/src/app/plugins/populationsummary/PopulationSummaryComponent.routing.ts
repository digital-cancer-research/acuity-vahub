import {Routes} from '@angular/router';

import {PopulationSummaryPlotComponent} from './populationSummaryPlot/PopulationSummaryPlotComponent';
import {PopulationSummaryTableComponent} from './populationSummaryTable/PopulationSummaryTableComponent';

export const populationRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'summary-plot'},
    { path: 'summary-plot', component: PopulationSummaryPlotComponent },
    { path: 'summary-table', component: PopulationSummaryTableComponent }
];

export const populationSummaryRoutingComponents = [
    PopulationSummaryPlotComponent,
    PopulationSummaryTableComponent
];
