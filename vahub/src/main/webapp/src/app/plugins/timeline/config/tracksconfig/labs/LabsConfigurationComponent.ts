import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges} from '@angular/core';
import {LabsYAxisValue} from '../../../store/ITimeline';

@Component({
    selector: 'labs-config',
    templateUrl: 'LabsConfigurationComponent.html'
})
export class LabsConfigurationComponent implements OnChanges {
    @Input() labsYAxisValue: LabsYAxisValue;
    @Output() updateLabsYAxisValue: EventEmitter<LabsYAxisValue> = new EventEmitter<LabsYAxisValue>();
    labsYAxisValueOptions: LabsYAxisValue[] = [
        LabsYAxisValue.RAW,
        LabsYAxisValue.CHANGE_FROM_BASELINE,
        LabsYAxisValue.PERCENT_CHANGE_FROM_BASELINE,
        LabsYAxisValue.REF_RANGE_NORM,
        LabsYAxisValue.TIMES_UPPER_REF,
        LabsYAxisValue.TIMES_LOWER_REF
     ];
    currentLabsYAxisValue: LabsYAxisValue;

    ngOnChanges(changes: SimpleChanges): void {
        this.currentLabsYAxisValue = this.labsYAxisValue;
    }

    updateYAxisValueOption(yAxisValue: LabsYAxisValue): void {
        this.currentLabsYAxisValue = yAxisValue;
        const that = this;
        setTimeout(() => {
            that.updateLabsYAxisValue.emit(yAxisValue);
        }, 0);
    }
}
