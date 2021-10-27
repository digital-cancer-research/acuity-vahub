import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {LabsSummaryTrackLegendConfigService} from './labs/LabsSummaryTrackLegendConfigService';
import {LabsDetailTrackLegendConfigService} from './labs/LabsDetailTrackLegendConfigService';
import {ExacerbationsTrackLegendConfigService} from './exacerbations/ExacerbationsTrackLegendConfigService';
import {SpirometrySummaryTrackLegendConfigService} from './spirometry/SpirometrySummaryTrackLegendConfigService';
import {SpirometryDetailTrackLegendConfigService} from './spirometry/SpirometryDetailTrackLegendConfigService';
import {VitalsSummaryTrackLegendConfigService} from './vitals/VitalsSummaryTrackLegendConfigService';
import {VitalsDetailTrackLegendConfigService} from './vitals/VitalsDetailTrackLegendConfigService';
import {PatientDataSummaryTrackLegendConfigService} from './patientdata/PatientDataSummaryTrackLegendConfigService';
import {PatientDataDetailTrackLegendConfigService} from './patientdata/PatientDataDetailTrackLegendConfigService';
import {EcgSummaryTrackLegendConfigService} from './ecg/EcgSummaryTrackLegendConfigService';
import {EcgMeasurementTrackLegendConfigService} from './ecg/EcgMeasurementTrackLegendConfigService';
import {EcgDetailTrackLegendConfigService} from './ecg/EcgDetailTrackLegendConfigService';
import {TimelineLegendComponent} from './TimelineLegendComponent';
import {StatusTrackLegendConfigService} from './status/StatusTrackLegendConfigService';
import {AesTrackLegendConfigService} from './aes/AesTrackLegendConfigService';
import {DoseSummaryTrackLegendConfigService} from './dose/DoseSummaryTrackLegendConfigService';
import {DoseDrugDetailTrackLegendConfigService} from './dose/DoseDrugDetailTrackLegendConfigService';
import {ConmedsTrackLegendConfigService} from './conmeds/ConmedsTrackLegendConfigService';
import {TimelineTrackLegendComponentModule} from './track/TimelineTrackLegendComponent.module';

@NgModule({
    imports: [CommonModule, FormsModule, TimelineTrackLegendComponentModule],
    exports: [TimelineLegendComponent],
    declarations: [TimelineLegendComponent],
    providers: [
        StatusTrackLegendConfigService,
        AesTrackLegendConfigService,
        DoseSummaryTrackLegendConfigService,
        DoseDrugDetailTrackLegendConfigService,
        ConmedsTrackLegendConfigService,
        LabsSummaryTrackLegendConfigService,
        LabsDetailTrackLegendConfigService,
        ExacerbationsTrackLegendConfigService,
        SpirometrySummaryTrackLegendConfigService,
        SpirometryDetailTrackLegendConfigService,
        VitalsSummaryTrackLegendConfigService,
        VitalsDetailTrackLegendConfigService,
        PatientDataSummaryTrackLegendConfigService,
        PatientDataDetailTrackLegendConfigService,
        EcgSummaryTrackLegendConfigService,
        EcgMeasurementTrackLegendConfigService,
        EcgDetailTrackLegendConfigService
    ],
})
export class TimelineLegendComponentModule {
}
