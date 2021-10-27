import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
    selector: 'range-setting',
    templateUrl: 'RangeSettingsComponent.html',
    styleUrls: ['../dataTypes/PlotSettings.css']
})
export class RangeSettingsComponent implements OnInit {
    @Input() id: string;
    @Input() minConst: number;
    @Input() maxConst: number;
    @Input() currentValue: number;
    inputModel: number;

    @Output() onChange: EventEmitter<any> = new EventEmitter<any>();

    ngOnInit(): void {
        this.inputModel = this.currentValue;
    }

    handleChange({target: {value}}): void {
        let actualValue = +value;
        if (actualValue > this.maxConst) {
            actualValue = this.maxConst;
        } else if (actualValue < this.minConst) {
            actualValue = this.minConst;
        }
        this.inputModel = actualValue;
        this.onChange.emit(actualValue);
    }
}
