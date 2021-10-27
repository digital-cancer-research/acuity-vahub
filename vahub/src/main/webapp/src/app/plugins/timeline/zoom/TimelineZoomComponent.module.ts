import {NgModule} from '@angular/core';
import {TimelineZoomBarComponent} from './TimelineZoomBarComponent';
import {TimelineZoomComponent} from './TimelineZoomComponent';
import {CommonModule} from '@angular/common';
import {AxisChartComponent} from '../chart/axis/AxisChartComponent';
import {XAxisCoordinateService} from '../chart/axis/XAxisCoordinateService';

@NgModule({
    imports: [CommonModule],
    exports: [TimelineZoomComponent],
    declarations: [TimelineZoomComponent, TimelineZoomBarComponent, AxisChartComponent],
    providers: [XAxisCoordinateService],
})
export class TimelineZoomComponentModule {
}
