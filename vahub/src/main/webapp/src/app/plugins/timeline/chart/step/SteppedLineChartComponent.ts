import {
    ChangeDetectionStrategy,
    Component,
    ElementRef,
    Input,
    NgZone,
    OnChanges,
    OnDestroy,
    SimpleChange
} from '@angular/core';
import {AbstractChartComponent} from '../AbstractChartComponent';
import {XAxisCoordinateService} from '../axis/XAxisCoordinateService';
import {SteppedLineChartPlotconfigService} from './SteppedLineChartPlotconfigService';
import {IHighlightedPlotArea, IZoom} from '../../store/ITimeline';
import {List} from 'immutable';

@Component({
    selector: 'timeline-steppedlinechart',
    template: '<div class="row"></div>',
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [SteppedLineChartPlotconfigService]
})
export class SteppedLineChartComponent extends AbstractChartComponent implements OnChanges, OnDestroy {
    // passed in to add additional configurations
    @Input() customPlotConfig: any;
    // data to be displayed in the chart, need to be formatted to data serises before used
    @Input() plotData: any[];
    // the zoom of the chart
    @Input() zoom: IZoom;
    // whether the plot is highlighted or not
    @Input() highlighted: boolean;

    @Input() plotBands: List<IHighlightedPlotArea>;

    constructor(protected elementRef: ElementRef,
                protected steppedLineChartPlotconfigService: SteppedLineChartPlotconfigService,
                protected ngZone: NgZone,
                protected xAxisCoordinateService: XAxisCoordinateService) {
        super(elementRef, steppedLineChartPlotconfigService, ngZone, xAxisCoordinateService);
    }

    ngOnChanges(changes: { [propertyName: string]: SimpleChange }): void {
        super.ngOnChanges(changes);
    }

    ngOnDestroy(): void {
        super.ngOnDestroy();
    }
}
