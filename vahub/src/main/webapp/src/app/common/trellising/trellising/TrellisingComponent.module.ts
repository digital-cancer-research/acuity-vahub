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

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {TrellisingComponent} from './TrellisingComponent';
import {
    cardiacServices,
    labsServices,
    liverServices,
    respiratoryServices,
    renalServices,
    aesServices,
    vitalsServices,
    cvotServices,
    PopulationHttpService,
    ConmedsHttpService,
    ConmedsBarChartHttpService,
    exacerbationsServices,
    ExposureHttpService,
    DoseProportionalityHttpService,
    cieventsServices,
    tumorRespServices,
    cveServices,
    PkOverallResponseHttpService, machineInsightsServices
} from '../../../data/index';
import {XZoomComponent, YZoomComponent, XTextZoomComponent} from '../zoom/index';
import {AxisLabelComponentModule} from '../axislabel/AxisLabelComponent.module';
import {ProgressComponentModule} from '../../loading/ProgressComponent.module';
import {TrellisingPaginationComponentModule} from '../pagination/index';
import {TrellisingLegendComponentModule} from '../legend/index';
import {GridComponentModule} from '../grid/index';
import {DetailsOnDemandComponentModule} from '../detailsondemand/DetailsOnDemandComponent.module';
import {CommonPipesModule} from '../../pipes/index';
import {TimelineConfigService} from '../store/services/TimelineConfigService';
import {TrellisingJumpComponentModule} from '../jump/index';
import {SingleSubjectModel} from '../../../plugins/refactored-singlesubject/SingleSubjectModel';
import {Trellising} from '../store/Trellising';
import {DataService} from '../data/DataService';
import {TabStoreUtils} from '../store/utils/TabStoreUtils';
import {TrellisingObservables} from '../store/observable/TrellisingObservables';
import {TrellisingDispatcher} from '../store/dispatcher/TrellisingDispatcher';
import {PlotUtilsFactory} from '../store/utils/PlotUtilsFactory';
import {BiomarkersHttpService} from '../../../data';
import {HttpServiceFactory} from '../../../data/HttpServiceFactory';
import {CBioJumpComponentModule} from '../cBio-jump/CBioJumpComponent.module';
import {CtDnaHttpService} from '../../../data/biomarkers/CtDnaHttpService';
import {ExplanationModalComponentModule} from '../../modals/plotExplanation/module';
import {ExportChartService} from '../../../data/export-chart.service';
import {StateUtilsService} from '../../utils/StateUtilsService';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ProgressComponentModule,
        TrellisingPaginationComponentModule,
        TrellisingJumpComponentModule,
        GridComponentModule,
        DetailsOnDemandComponentModule,
        TrellisingLegendComponentModule,
        AxisLabelComponentModule,
        CommonPipesModule,
        CBioJumpComponentModule,
        ExplanationModalComponentModule
    ],
    exports: [TrellisingComponent],
    declarations: [TrellisingComponent,
        XZoomComponent,
        YZoomComponent,
        XTextZoomComponent
    ],
    providers: [DataService,
        TimelineConfigService,
        Trellising,
        TrellisingObservables,
        TrellisingDispatcher,
        TabStoreUtils,
        PlotUtilsFactory,
        ...cardiacServices,
        ...labsServices,
        ...aesServices,
        ...exacerbationsServices,
        ...liverServices,
        ...renalServices,
        ...respiratoryServices,
        ...cvotServices,
        ...cveServices,
        HttpServiceFactory,
        PopulationHttpService,
        ...vitalsServices,
        SingleSubjectModel,
        ConmedsHttpService,
        ConmedsBarChartHttpService,
        ExposureHttpService,
        DoseProportionalityHttpService,
        PkOverallResponseHttpService,
        BiomarkersHttpService,
        CtDnaHttpService,
        ...tumorRespServices,
        ...cieventsServices,
        ...cveServices,
        ...machineInsightsServices,
        ExportChartService,
        StateUtilsService,
    ],
})
export class TrellisingComponentModule {
}
