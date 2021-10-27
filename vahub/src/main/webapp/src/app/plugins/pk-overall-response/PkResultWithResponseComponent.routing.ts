import {Routes} from '@angular/router';

import {CanActivatePkResultOverallResponse} from './pk-overall-response-plot/CanActivatePkResultOverallResponse';
import {OverallResponsePlotComponent} from './pk-overall-response-plot/OverallResponsePlotComponent';

export const pkResultWithResponseRoutes: Routes = [
    {path: '', pathMatch: 'full', redirectTo: 'box-plot'},
    {
        path: 'box-plot',
        component: OverallResponsePlotComponent,
        canActivate: [CanActivatePkResultOverallResponse]
    }
];

export const pkResultWithResponseRoutingComponents = [OverallResponsePlotComponent];
