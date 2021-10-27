import { Routes } from '@angular/router';
import {LungFunctionBoxPlotComponent} from './lungFunctionBoxPlot/LungFunctionBoxPlotComponent';
import {AcuityRespiratorySpotfireComponent} from './spotfire/AcuityRespiratorySpotfireComponent';
import {DetectRespiratorySpotfireComponent} from './spotfire/DetectRespiratorySpotfireComponent';
import {CanActivateRespiratorySpotfire} from './spotfire/CanActivateRespiratorySpotfire';
import {CanActivateDetectRespiratorySpotfire} from './spotfire/CanActivateDetectRespiratorySpotfire';
import {CanActivateLungFunctionBoxPlot} from './lungFunctionBoxPlot/CanActivateLungFunctionBoxPlot';

export const respiratoryRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'lung-function-box-plot'},
    { path: 'lung-function-box-plot', component: LungFunctionBoxPlotComponent, canActivate: [CanActivateLungFunctionBoxPlot] },
    { path: 'acuity-spotfire', component: AcuityRespiratorySpotfireComponent, canActivate: [CanActivateRespiratorySpotfire]  },
    { path: 'detect-spotfire', component: DetectRespiratorySpotfireComponent, canActivate: [CanActivateDetectRespiratorySpotfire]  }

];

export const respiratoryRoutingComponents = [
    LungFunctionBoxPlotComponent,
    AcuityRespiratorySpotfireComponent,
    DetectRespiratorySpotfireComponent
];
