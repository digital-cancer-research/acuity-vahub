import { Routes } from '@angular/router';
import {CIEventsBarChartComponent} from './eventcounts/CIEventsCountsChartComponent';
import {CIEventsOvertimeChartComponent} from './overtime/CIEventsOvertimeChartComponent';

export const ciEventsRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'event-counts' },
    { path: 'event-counts', component: CIEventsBarChartComponent },
    { path: 'overtime', component: CIEventsOvertimeChartComponent }
];

export const ciEventsRoutingComponents = [
    CIEventsBarChartComponent,
    CIEventsOvertimeChartComponent
];
