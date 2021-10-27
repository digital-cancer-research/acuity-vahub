import { Routes } from '@angular/router';
import {CerebrovascularEventsCountsChartComponent} from './eventscounts/CerebrovascularEventsCountsChartComponent';
import {CVEOverTimeChartComponent} from './overtime/CVEOverTimeChartComponent';

export const cerebrovascularEventsRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'event-counts' },
    { path: 'event-counts', component: CerebrovascularEventsCountsChartComponent },
    { path: 'overtime', component: CVEOverTimeChartComponent}
];

export const cerebrovascularEventsRoutingComponents = [
    CerebrovascularEventsCountsChartComponent,
    CVEOverTimeChartComponent
];
