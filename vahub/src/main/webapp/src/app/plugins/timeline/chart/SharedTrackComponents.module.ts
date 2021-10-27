import {NgModule} from '@angular/core';
import {TimelineContextMenuComponentModule} from '../menu/TimelineContextMenuComponent.module';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {BarChartComponent} from './bar/BarChartComponent';
import {SteppedLineChartComponent} from './step/SteppedLineChartComponent';
import {LineChartComponent} from './line/LineChartComponent';

@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        TimelineContextMenuComponentModule
    ],
    exports: [
        BarChartComponent,
        SteppedLineChartComponent,
        LineChartComponent,
        TimelineContextMenuComponentModule],
    declarations: [
        BarChartComponent,
        SteppedLineChartComponent,
        LineChartComponent
    ],
    providers: [],
})
export class SharedTrackComponentsModule {
}
