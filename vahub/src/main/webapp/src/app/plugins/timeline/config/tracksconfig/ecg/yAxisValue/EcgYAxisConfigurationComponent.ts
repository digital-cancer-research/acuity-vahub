import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges} from '@angular/core';
import {EcgYAxisValue} from '../../../../store/ITimeline';

@Component({
    selector: 'ecg-y-axis-config',
    templateUrl: 'EcgYAxisConfigurationComponent.html'
})
export class EcgYAxisConfigurationComponent implements OnChanges {
    @Input() ecgYAxisValue: EcgYAxisValue;
    @Output() updateEcgYAxisValue: EventEmitter<EcgYAxisValue> = new EventEmitter<EcgYAxisValue>();
    ecgYAxisValueOptions: EcgYAxisValue[] = [EcgYAxisValue.RAW, EcgYAxisValue.CHANGE_FROM_BASELINE, EcgYAxisValue.PERCENT_CHANGE_FROM_BASELINE];
    currentEcgYAxisValue: EcgYAxisValue;

    ngOnChanges(changes: SimpleChanges): void {
        this.currentEcgYAxisValue = this.ecgYAxisValue;
    }

    updateYAxisValueOption(yAxisValue: EcgYAxisValue): void {
        this.currentEcgYAxisValue = yAxisValue;
        const that = this;
        setTimeout(() => {
            that.updateEcgYAxisValue.emit(yAxisValue);
        }, 0);
    }
}
