import {Routes} from '@angular/router';
import {BiomarkersPlotComponent} from './biomarkersPlot/BiomarkersPlotComponent';
import {CanActivateBiomarkers} from './CanActivateBiomarkers';

export const biomarkersRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'plot' },
    { path: 'plot', component: BiomarkersPlotComponent, canActivate: [CanActivateBiomarkers] }
];

export const biomarkersRoutingComponents = [BiomarkersPlotComponent];
