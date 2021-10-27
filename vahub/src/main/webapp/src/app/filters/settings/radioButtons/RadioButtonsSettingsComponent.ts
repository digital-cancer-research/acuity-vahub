import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';

@Component({
    selector: 'radio-buttons-setting',
    templateUrl: 'RadioButtonsSettingsComponent.html',
    styleUrls: ['../../filters.css', '../dataTypes/PlotSettings.css']
})
export class RadioButtonsSettingsComponent implements OnChanges, OnInit {

    // you can pass currentOption or selectedValue to this component. It was made because of different types of options for settings
    @Input() currentOption: any;
    @Input() previousSelectedValue: any;
    @Input() availableValues: any[];
    @Input() selectedValue: string;
    @Input() name: string;

    @Output() onChange: EventEmitter<any> = new EventEmitter<any>();


    ngOnChanges(changes: SimpleChanges): void {
        // if there is no currentOption param then it won't carry out
        if (changes.currentOption && changes.currentOption.currentValue) {
            this.selectedValue = changes.currentOption.currentValue.get('trellisedBy');
        }
    }

    ngOnInit(): void {
        this.selectedValue = this.previousSelectedValue;
    }

    changeHandler() {
        this.onChange.emit({currentOption: this.currentOption, selectedValue: this.selectedValue});
    }
}
