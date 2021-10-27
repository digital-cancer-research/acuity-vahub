import {AesTrackComponentModule} from './aes/AesTrackComponent.module';
import {DoseTrackComponentModule} from './dose/DoseTrackComponent.module';
import {StatusTrackComponentModule} from './status/StatusTrackComponent.module';
import {ConmedsTrackComponentModule} from './conmeds/ConmedsTrackComponent.module';
import {VitalsTrackComponentModule} from './vitals/VitalsTrackComponent.module';
import {PatientDataTrackComponentModule} from './patientdata/PatientDataTrackComponent.module';
import {ExacerbationsTrackComponentModule} from './exacerbations/ExacerbationsTrackComponent.module';
import {EcgTrackComponentModule} from './ecg/EcgTrackComponent.module';
import {LabsTrackComponentModule} from './labs/LabsTrackComponent.module';
import {SpirometryTrackComponentModule} from './spirometry/SpirometryTrackComponent.module';

import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {TimelineTrackComponent} from './TimelineTrackComponent';

@NgModule({
    imports: [
        FormsModule,
        CommonModule,
        AesTrackComponentModule,
        DoseTrackComponentModule,
        StatusTrackComponentModule,
        ConmedsTrackComponentModule,
        VitalsTrackComponentModule,
        PatientDataTrackComponentModule,
        ExacerbationsTrackComponentModule,
        EcgTrackComponentModule,
        LabsTrackComponentModule,
        SpirometryTrackComponentModule
    ],
    exports: [TimelineTrackComponent],
    declarations: [TimelineTrackComponent],
    providers: [],
})
export class TimelineTrackComponentModule {
}
