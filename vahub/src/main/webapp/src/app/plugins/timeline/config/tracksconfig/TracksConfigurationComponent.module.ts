import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {LabsConfigurationComponent} from './labs/LabsConfigurationComponent';
import {EcgYAxisConfigurationComponent} from './ecg/yAxisValue/EcgYAxisConfigurationComponent';
import {EcgWarningsConfigurationComponent} from './ecg/warnings/EcgWarningsConfigurationComponent';
import {SpirometryConfigurationComponent} from './spirometry/SpirometryConfigurationComponent';
import {VitalsConfigurationComponent} from './vitals/VitalsConfigurationComponent';
import {TracksConfigurationComponent} from './TracksConfigurationComponent';
import {TimelineConfigurationService} from './TracksConfigurationService';

@NgModule({
    imports: [CommonModule, FormsModule],
    exports: [TracksConfigurationComponent],
    declarations: [TracksConfigurationComponent,
        LabsConfigurationComponent,
        EcgYAxisConfigurationComponent,
        EcgWarningsConfigurationComponent,
        VitalsConfigurationComponent,
        SpirometryConfigurationComponent],
    providers: [TimelineConfigurationService]
})
export class TracksConfigurationComponentModule {
}
