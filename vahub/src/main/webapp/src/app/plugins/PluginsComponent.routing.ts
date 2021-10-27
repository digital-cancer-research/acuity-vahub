/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Routes} from '@angular/router';
import {
    PkResultComponent,
    PkResultComponentModule,
    pkResultRoutes,
    pkResultRoutingComponents
} from './dose-proportionality/module';
import {
    PkResultWithResponseComponent,
    PkResultWithResponseComponentModule,
    pkResultWithResponseRoutes,
    pkResultWithResponseRoutingComponents
} from './pk-overall-response/module';
import {
    populationRoutes,
    PopulationSummaryComponent,
    PopulationSummaryComponentModule,
    populationSummaryRoutingComponents
} from './populationsummary/module';
import {AEsComponent, AEsComponentModule, aesRoutes} from './aes/module';
import {CvotComponent, CvotComponentModule, cvotRoutes, cvotRoutingComponents} from './cvot/module';
import {ConmedsComponent, ConmedsComponentModule, conmedsRoutes, conmedsRoutingComponents} from './conmeds/module';
import {CIEventsComponent, CIEventsComponentModule, ciEventsRoutes, ciEventsRoutingComponents} from './cievents/module';
import {
    CerebrovascularEventsComponent,
    CerebrovascularEventsComponentModule,
    cerebrovascularEventsRoutes,
    cerebrovascularEventsRoutingComponents
} from './cerebrovascular/module';
import {LabsComponent, LabsComponentModule, labsRoutes, labsRoutingComponents} from './labs/module';
import {
    LiverFunctionComponent,
    LiverFunctionComponentModule,
    liverFunctionRoutingComponents,
    liverRoutes
} from './liverfunction';
import {
    RespiratoryComponent,
    RespiratoryComponentModule,
    respiratoryRoutes,
    respiratoryRoutingComponents
} from './respiratory/module';
import {VitalsComponent, VitalsComponentModule, vitalsRoutes, vitalsRoutingComponents} from './vitals/module';
import {RenalComponent, RenalComponentModule, renalRoutes, renalRoutingComponents} from './renal/module';
import {CardiacComponent, CardiacComponentModule, cardiacRoutes, cardiacRoutingComponents} from './cardiac/module';
import {CompareSubjectComponent, TimelineComponentModule} from './timeline/module';

import {ExposureComponent, ExposureComponentModule, exposureRoutes, exposureRoutingComponents} from './exposure';
import {
    TumourResponseComponent,
    TumourResponseComponentModule,
    tumourResponseRoutes,
    tumourResponseRoutingComponents
} from './tumour-response';
import {
    TumourLesionComponent,
    TumourLesionComponentModule,
    tumourLesionRoutes,
    tumourLesionRoutingComponents
} from './tumour-lesion';
import {
    TumourTherapyComponent,
    TumourTherapyComponentModule,
    tumourTherapyRoutes,
    tumourTherapyRoutingComponents
} from './tumour-therapy';
import {CohortEditorComponent, CohortEditorModule} from './cohorteditor/module';

import {SingleSubjectViewComponentModule} from './refactored-singlesubject/SingleSubjectViewComponent.module';
import {BiomarkersComponent, BiomarkersComponentModule, biomarkersRoutes} from './biomarkers/module';
import {CtDNAComponent, CtDNAComponentModule, ctDNARoutes} from './ctDNA/module';
import {
    ExacerbationsComponent,
    ExacerbationsComponentModule,
    exacerbationsRoutes,
    exacerbationsRoutingComponents
} from './exacerbations';
import {
    MachineInsightsComponent,
    MachineInsightsModule,
    machineInsightsRoutes,
    machineInsightsRoutingComponents
} from './machine-insights/module';

import {DropdownComponentModule, SpotfireComponentModule} from '../common/module';
import {FiltersBarComponentModule, SelectedFiltersComponentModule} from '../filters/module';
import {
    CanDeactivateAEs,
    CanDeactivateBiomarkers,
    CanDeactivateCardiac,
    CanDeactivateCerebrovascular,
    CanDeactivateCIEvents,
    CanDeactivateCohortEditor,
    CanDeactivateConmeds,
    CanDeactivateCtDna,
    CanDeactivateCvot,
    CanDeactivateDoseProportionality,
    CanDeactivateExacerbations,
    CanDeactivateExposure,
    CanDeactivateLabs,
    CanDeactivateLiverFunction,
    CanDeactivatePkOverallResponse,
    CanDeactivatePriorTherapy,
    CanDeactivateRecist,
    CanDeactivateRenal,
    CanDeactivateRespiratory,
    CanDeactivateVitals
} from './deactivate';
import {AxisLabelComponentModule} from '../common/trellising/axislabel/AxisLabelComponent.module';
import {CommonPipesModule} from '../common/pipes';
import {TrellisingComponentModule} from '../common/trellising/trellising';
import {SingleSubjectViewComponent} from './refactored-singlesubject/SingleSubjectViewComponent';
import {SingleSubjectViewRoutes} from './refactored-singlesubject/SingleSubjectViewComponent.routing';
import {CanActivateMachineInsights} from './activate/CanActivateMachineInsights';

export const pluginsComponentRoutes: Routes = [
    {
        path: '',
        redirectTo: '/plugins/population-summary/summary-plot',
        pathMatch: 'full'
    },
    {
        path: 'population-summary',
        component: PopulationSummaryComponent,
        children: populationRoutes
    },
    {
        path: 'exposure',
        component: ExposureComponent,
        children: exposureRoutes,
        canDeactivate: [CanDeactivateExposure]
    },
    {
        path: 'dose-proportionality',
        component: PkResultComponent,
        children: pkResultRoutes,
        canDeactivate: [CanDeactivateDoseProportionality]
    },
    {
        path: 'pk-overall-response',
        component: PkResultWithResponseComponent,
        children: pkResultWithResponseRoutes,
        canDeactivate: [CanDeactivatePkOverallResponse]
    },
    {
        path: 'tumour-response', component: TumourResponseComponent,
        children: tumourResponseRoutes,
        canDeactivate: [CanDeactivateRecist]
    },
    {
        path: 'tumour-lesion', component: TumourLesionComponent,
        children: tumourLesionRoutes
    },
    {
        path: 'tumour-therapy', component: TumourTherapyComponent,
        children: tumourTherapyRoutes,
        canDeactivate: [CanDeactivatePriorTherapy]
    },
    {
        path: 'aes', component: AEsComponent,
        children: aesRoutes,
        canDeactivate: [CanDeactivateAEs]
    },
    {
        path: 'cievents', component: CIEventsComponent,
        children: ciEventsRoutes,
        canDeactivate: [CanDeactivateCIEvents]
    },
    {
        path: 'cerebrovascular', component: CerebrovascularEventsComponent,
        children: cerebrovascularEventsRoutes,
        canDeactivate: [CanDeactivateCerebrovascular]
    },
    {
        path: 'cvot', component: CvotComponent,
        children: cvotRoutes,
        canDeactivate: [CanDeactivateCvot]
    },
    {
        path: 'conmeds', component: ConmedsComponent,
        children: conmedsRoutes,
        canDeactivate: [CanDeactivateConmeds]
    },
    {
        path: 'labs', component: LabsComponent,
        children: labsRoutes,
        canDeactivate: [CanDeactivateLabs]
    },
    {
        path: 'liver', component: LiverFunctionComponent,
        children: liverRoutes,
        canDeactivate: [CanDeactivateLiverFunction]
    },
    {
        path: 'respiratory', component: RespiratoryComponent,
        children: respiratoryRoutes,
        canDeactivate: [CanDeactivateRespiratory]
    },
    {
        path: 'exacerbations', component: ExacerbationsComponent,
        children: exacerbationsRoutes,
        canDeactivate: [CanDeactivateExacerbations]
    },
    {
        path: 'vitals', component: VitalsComponent,
        children: vitalsRoutes,
        canDeactivate: [CanDeactivateVitals]
    },
    {
        path: 'renal', component: RenalComponent,
        children: renalRoutes,
        canDeactivate: [CanDeactivateRenal]
    },
    {
        path: 'cardiac', component: CardiacComponent,
        children: cardiacRoutes,
        canDeactivate: [CanDeactivateCardiac]
    },
    {
        path: 'singlesubject', component: SingleSubjectViewComponent,
        children: SingleSubjectViewRoutes
    },
    {
        path: 'biomarker', component: BiomarkersComponent,
        children: [
            ...biomarkersRoutes
        ],
        canDeactivate: [CanDeactivateBiomarkers]
    },
    {
        path: 'ctdna', component: CtDNAComponent,
        children: [
            ...ctDNARoutes
        ],
        canDeactivate: [CanDeactivateCtDna]
    },
    {
        path: 'timeline', component: CompareSubjectComponent
    },
    {
        path: 'cohort-editor',
        component: CohortEditorComponent,
        canDeactivate: [CanDeactivateCohortEditor]
    },
    {
        path: 'machine-insights',
        component: MachineInsightsComponent,
        children: [
            ...machineInsightsRoutes
        ],
        canActivate: [
            CanActivateMachineInsights
        ]
    }
];

// TODO: investigate if we need to add new plots here, as some of them are not added
export const pluginsComponentRoutingComponents = [
    CompareSubjectComponent,
    ...exposureRoutingComponents,
    ...tumourResponseRoutingComponents,
    ...tumourLesionRoutingComponents,
    ...tumourTherapyRoutingComponents,
    ...conmedsRoutingComponents,
    ...populationSummaryRoutingComponents,
    ...labsRoutingComponents,
    ...liverFunctionRoutingComponents,
    ...respiratoryRoutingComponents,
    ...exacerbationsRoutingComponents,
    ...vitalsRoutingComponents,
    ...renalRoutingComponents,
    ...cardiacRoutingComponents,
    ...ciEventsRoutingComponents,
    ...cerebrovascularEventsRoutingComponents,
    ...cvotRoutingComponents,
    ...pkResultRoutingComponents,
    ...pkResultWithResponseRoutingComponents,
    ...machineInsightsRoutingComponents
];

export const pluginsComponentImports = [
    // DashboardTableModule,
    TrellisingComponentModule,
    DropdownComponentModule,
    AxisLabelComponentModule,
    CommonPipesModule,
    CohortEditorModule,
    AEsComponentModule,
    CIEventsComponentModule,
    CerebrovascularEventsComponentModule,
    ConmedsComponentModule,
    CardiacComponentModule,
    LabsComponentModule,
    LiverFunctionComponentModule,
    PopulationSummaryComponentModule,
    RenalComponentModule,
    RespiratoryComponentModule,
    ExacerbationsComponentModule,
    VitalsComponentModule,
    FiltersBarComponentModule,
    TimelineComponentModule,
    SelectedFiltersComponentModule,
    SingleSubjectViewComponentModule,
    TumourResponseComponentModule,
    TumourLesionComponentModule,
    TumourTherapyComponentModule,
    ExposureComponentModule,
    SpotfireComponentModule,
    BiomarkersComponentModule,
    CtDNAComponentModule,
    CvotComponentModule,
    PkResultComponentModule,
    PkResultWithResponseComponentModule,
    MachineInsightsModule
];

export const canDeactivateServiceImports = [
    CanDeactivateAEs,
    CanDeactivateConmeds,
    CanDeactivateLabs,
    CanDeactivateCardiac,
    CanDeactivateLiverFunction,
    CanDeactivateRenal,
    CanDeactivateRespiratory,
    CanDeactivateExacerbations,
    CanDeactivateVitals,
    CanDeactivateRenal,
    CanDeactivateCohortEditor,
    CanDeactivateCIEvents,
    CanDeactivateCerebrovascular,
    CanDeactivateCvot,
    CanDeactivateCtDna,
    CanDeactivateBiomarkers,
    CanDeactivateExposure,
    CanDeactivateDoseProportionality,
    CanDeactivatePkOverallResponse,
    CanDeactivatePriorTherapy,
    CanDeactivateRecist
];
export const canActivateServiceImports = [
    CanActivateMachineInsights
];
