import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Map} from 'immutable';

@Component({
    selector: 'list-selection',
    templateUrl: './ListSelectionComponent.html',
    styleUrls: ['../../filters.css', '../dataTypes/PlotSettings.css']
})
export class ListSelectionComponent {
    @Input() inactive: boolean;
    @Input() elements: Map<string, boolean>;
    @Output() onSelect: EventEmitter<any> = new EventEmitter<any>();

    selectElement(item) {
        this.onSelect.emit({name: item, selected: !this.elements.get(item)});
    }
}
