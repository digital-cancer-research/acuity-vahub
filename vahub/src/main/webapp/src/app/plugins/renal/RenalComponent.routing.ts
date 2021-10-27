import {CreatinineBoxPlotComponent} from './creatinine/CreatinineBoxPlotComponent';
import {CkdStageBarChartComponent} from './ckdstage/CkdStageBarChartComponent';
import { Routes } from '@angular/router';

export const renalRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'creatinine' },
    { path: 'creatinine', component: CreatinineBoxPlotComponent },
    { path: 'ckdstage', component: CkdStageBarChartComponent },
];

export const renalRoutingComponents = [CreatinineBoxPlotComponent, CkdStageBarChartComponent];
