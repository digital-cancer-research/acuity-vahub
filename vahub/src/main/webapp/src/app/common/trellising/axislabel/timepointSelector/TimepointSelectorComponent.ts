import {Component, EventEmitter, Input, Output} from '@angular/core';
import {List} from 'immutable';
import {DisplayedGroupBySetting} from '../../store/actions/TrellisingActionCreator';

@Component({
    selector: 'timepoint-selector',
    templateUrl: 'TimepointSelectorComponent.html',
})
export class TimepointSelectorComponent {
    @Input() label: string;
    @Input() selectedOption: DisplayedGroupBySetting;
    @Input() availableTrellisAxisOptions: List<string>;

    @Output() apply: EventEmitter<any> = new EventEmitter<any>();

    applyValue() {
        this.apply.emit(this.selectedOption);
    }
}
