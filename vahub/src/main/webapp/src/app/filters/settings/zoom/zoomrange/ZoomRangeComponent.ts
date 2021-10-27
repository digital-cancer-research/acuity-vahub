import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnChanges, Output, SimpleChanges} from '@angular/core';

// The logic of the component should be as
// we get a range, if it's more than current max value - we need to set it to current max value
// if it's less - than we need to set max to that initial value
// if user tries to set the range to some value that's more than max - set the value to max
@Component({
    selector: 'zoomrange',
    changeDetection: ChangeDetectionStrategy.OnPush,
    templateUrl: 'ZoomRangeComponent.html',
    styleUrls: ['../../dataTypes/PlotSettings.css']
})
export class ZoomRangeComponent implements OnChanges {
    @Input() zoom: number;
    @Input() id: string;
    @Input() maxConst: number;
    @Output() handleUpdateRange: EventEmitter<number> = new EventEmitter<number>();

    currentValue: number;
    max: number;

    ngOnChanges(changes: SimpleChanges): void {
        if (changes['zoom'] && changes['zoom'].currentValue) {
            const zoom = changes['zoom'].currentValue;
            this.max = zoom.get('absMax') > this.maxConst ? this.maxConst : zoom.get('absMax');
            this.currentValue = Math.round((zoom.get('zoomMax') - zoom.get('zoomMin')) + 1);
        }
    }

    // Here we basically check if the value entered is more than max
    // if so - change the value to max and send it
    handleChange({target: {value}}): void {
        let actualValue = +value;
        if (actualValue > this.max) {
            actualValue = this.max;
        } else if (!actualValue || actualValue <= 1) {
            this.currentValue = actualValue = 1;
        }

        this.currentValue = actualValue;

        this.handleUpdateRange.emit(actualValue);
    }
}
