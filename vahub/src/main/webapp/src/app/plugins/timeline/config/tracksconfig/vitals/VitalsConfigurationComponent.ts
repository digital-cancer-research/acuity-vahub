import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges} from '@angular/core';
import {VitalsYAxisValue} from '../../../store/ITimeline';

@Component({
    selector: 'vitals-config',
    templateUrl: 'VitalsConfigurationComponent.html'
})
export class VitalsConfigurationComponent implements OnChanges {
    @Input() vitalsYAxisValue: VitalsYAxisValue;
    @Output() updateVitalsYAxisValue: EventEmitter<VitalsYAxisValue> = new EventEmitter<VitalsYAxisValue>();
    vitalsYAxisValueOptions: VitalsYAxisValue[] = [VitalsYAxisValue.RAW, VitalsYAxisValue.CHANGE_FROM_BASELINE, VitalsYAxisValue.PERCENT_CHANGE_FROM_BASELINE];
    currentVitalsYAxisValue: VitalsYAxisValue;

    ngOnChanges(changes: SimpleChanges): void {
        this.currentVitalsYAxisValue = this.vitalsYAxisValue;
    }

    updateYAxisValueOption(yAxisValue: VitalsYAxisValue): void {
        this.currentVitalsYAxisValue = yAxisValue;
        const that = this;
        setTimeout(() => {
            that.updateVitalsYAxisValue.emit(yAxisValue);
        }, 0);
    }
}
