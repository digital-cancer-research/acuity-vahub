import {Routes} from '@angular/router';
import {CvotEndpointsChartComponent} from './endpointscounts/CvotEndpointsChartComponent';
import {CvotEndpointsOverTimeChartComponent} from './overtime/CvotEndpointsOverTimeChartComponent';

export const cvotRoutes: Routes = [
    {path: '', pathMatch: 'full', redirectTo: 'event-counts'},
    {path: 'event-counts', component: CvotEndpointsChartComponent},
    {path: 'overtime', component: CvotEndpointsOverTimeChartComponent}
];

export const cvotRoutingComponents = [CvotEndpointsChartComponent, CvotEndpointsOverTimeChartComponent];
