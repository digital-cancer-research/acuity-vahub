import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {TimelineConfigurationsComponent} from './TimelineConfigurationsComponent';
import {TimelineTrackSelectionComponentModule} from './trackselection/TimelineTrackSelectionComponent.module';
import {TracksConfigurationComponentModule} from './tracksconfig/TracksConfigurationComponent.module';

@NgModule({
    imports: [CommonModule, FormsModule, TimelineTrackSelectionComponentModule, TracksConfigurationComponentModule],
    exports: [TimelineConfigurationsComponent],
    declarations: [TimelineConfigurationsComponent]
})
export class TimelineConfigurationsComponentModule {
}
