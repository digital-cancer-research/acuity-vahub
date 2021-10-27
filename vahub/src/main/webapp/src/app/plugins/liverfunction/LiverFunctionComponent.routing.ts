import { Routes } from '@angular/router';
import {HysLawComponent} from './hyslaw/HysLawComponent';
export const liverRoutes: Routes = [
    { path: '', pathMatch: 'full', redirectTo: 'hyslaw' },
    { path: 'hyslaw', component: HysLawComponent}
];

export const liverFunctionRoutingComponents = [HysLawComponent];
