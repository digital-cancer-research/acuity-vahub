import {Routes} from '@angular/router';
import {AboutComponent} from './AboutComponent';
import {AvailableStudiesComponent} from './availablestudies/AvailableStudiesComponent';

export const aboutComponentRoutes: Routes = [
    {path: '', children: [
        {path: '', component: AboutComponent},
        {path: 'studies-in-acuity', component: AvailableStudiesComponent}
    ]},
];
