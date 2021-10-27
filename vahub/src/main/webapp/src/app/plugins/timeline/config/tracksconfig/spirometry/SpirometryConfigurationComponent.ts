import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges} from '@angular/core';
import {SpirometryYAxisValue} from '../../../store/ITimeline';

@Component({
    selector: 'spirometry-config',
    templateUrl: 'SpirometryConfigurationComponent.html'
})
export class SpirometryConfigurationComponent implements OnChanges {
    @Input() spirometryYAxisValue: SpirometryYAxisValue;
    @Output() updateSpirometryYAxisValue: EventEmitter<SpirometryYAxisValue> = new EventEmitter<SpirometryYAxisValue>();
    spirometryYAxisValueOptions: SpirometryYAxisValue[] = [SpirometryYAxisValue.RAW, SpirometryYAxisValue.CHANGE_FROM_BASELINE, SpirometryYAxisValue.PERCENT_CHANGE_FROM_BASELINE];
    currentSpirometryYAxisValue: SpirometryYAxisValue;

    ngOnChanges(changes: SimpleChanges): void {
        this.currentSpirometryYAxisValue = this.spirometryYAxisValue;
    }

    updateYAxisValueOption(yAxisValue: SpirometryYAxisValue): void {
        this.currentSpirometryYAxisValue = yAxisValue;
        const that = this;
        setTimeout(() => {
            that.updateSpirometryYAxisValue.emit(yAxisValue);
        }, 0);
    }
}
