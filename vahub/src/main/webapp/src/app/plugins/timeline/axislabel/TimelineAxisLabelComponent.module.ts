import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {TimelineAxisLabelComponent} from './index';
import {TimelineAxisLabelService} from './TimelineAxisLabelService';
import {CommonPipesModule} from '../../../common/pipes/CommonPipes.module';

@NgModule({
    imports: [CommonModule, FormsModule, CommonPipesModule],
    exports: [TimelineAxisLabelComponent],
    declarations: [TimelineAxisLabelComponent],
    providers: [TimelineAxisLabelService]
})
export class TimelineAxisLabelComponentModule {
}
