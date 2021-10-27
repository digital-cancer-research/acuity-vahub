import {Routes} from '@angular/router';

import {AesOverTimeComponent} from './overtime/AesOverTimeComponent';
import {AesTableComponent} from './aestable/AesTableComponent';
import {AesStackedBarChartComponent} from './stackedbarchart/AesStackedBarChartComponent';
import {TolerabilityComponent} from './spotfire/TolerabilityComponent';
import {
    ANY_CATEGORY,
    COMMON_CATEGORY,
    DEATH_OUTCOME_CATEGORY,
    DEFAULT_SUMMARY_CATEGORY,
    LDOST_CATEGORY
} from './aes-summary/AEsSummaryConstants';
import {AEsSummaryComponent as spotfireAEsSummaryComponent} from './spotfire/AEsSummaryComponent';
import {AEsSummaryComponent} from './aes-summary/AEsSummaryComponent';
import {AEsChordDiagramComponent} from './chord-diagram/AEsChordDiagramComponent';


export const aesRoutes: Routes = [
    {path: '', pathMatch: 'full', redirectTo: 'subject-counts'},
    {path: 'subject-counts', component: AesStackedBarChartComponent},
    {path: 'overtime', component: AesOverTimeComponent},
    {path: 'table', component: AesTableComponent},
    {path: 'spotfire', component: spotfireAEsSummaryComponent},
    {path: 'summary', redirectTo: `summary/${DEFAULT_SUMMARY_CATEGORY.link}`},
    {path: `summary/${ANY_CATEGORY.link}`, component: AEsSummaryComponent, data : {api : ANY_CATEGORY.api}},
    {path: `summary/${COMMON_CATEGORY.link}`, component: AEsSummaryComponent, data : {api : COMMON_CATEGORY.api}},
    {path: `summary/${DEATH_OUTCOME_CATEGORY.link}`, component: AEsSummaryComponent, data : {api : DEATH_OUTCOME_CATEGORY.api}},
    {path: `summary/${LDOST_CATEGORY.link}`, component: AEsSummaryComponent, data : {api : LDOST_CATEGORY.api}},
    {path: 'spotfireTolerability', component: TolerabilityComponent},
    {path: 'chord-diagram', component: AEsChordDiagramComponent},
];


