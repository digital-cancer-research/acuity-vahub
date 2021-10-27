import { Routes } from '@angular/router';
import {LabsPopulationBoxPlotComponent} from './labsPopulationBoxPlot/LabsPopulationBoxPlotComponent';
import {LabsRangePlotComponent} from './labsRangePlot/LabsRangePlotComponent';
import {LabsShiftPlotComponent} from './shift-plot/LabsShiftPlotComponent';

export const labsRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'box-plot' },
    { path: 'box-plot', component: LabsPopulationBoxPlotComponent },
    { path: 'shift-plot', component: LabsShiftPlotComponent },
    { path: 'line-plot', component: LabsRangePlotComponent }
];

export const labsRoutingComponents = [LabsPopulationBoxPlotComponent, LabsShiftPlotComponent, LabsRangePlotComponent];
