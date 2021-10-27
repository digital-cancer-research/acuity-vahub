import {Routes} from '@angular/router';

import {DoseProportionalityPlotComponent} from './dose-proportionality-plot/DoseProportionalityPlotComponent';

export const pkResultRoutes: Routes = [
    {path: '', pathMatch: 'full', redirectTo: 'box-plot'},
    {path: 'box-plot', component: DoseProportionalityPlotComponent}
];

export const pkResultRoutingComponents = [DoseProportionalityPlotComponent];
