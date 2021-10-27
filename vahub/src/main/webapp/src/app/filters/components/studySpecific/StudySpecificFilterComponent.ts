import {Component, Input, Output, EventEmitter} from '@angular/core';

import {StudySpecificFilterModel} from './StudySpecificFilterModel';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

@Component({
    selector: 'studyspecificfilter',
    templateUrl: 'StudySpecificFilterComponent.html',
    styleUrls: ['../../filters.css']
})
export class StudySpecificFilterComponent {

    @Input() ssfmodel: StudySpecificFilterModel;
    @Input() openedFilterModel: BaseFilterItemModel;
    @Output() modelChanged = new EventEmitter();

    onChange(filterName: string, item: string, target: any): void {
        console.log(target);
        this.ssfmodel.change(filterName, item, target.checked);
        this.modelChanged.emit({});
    }

    /**
     * Clears the selected values
     */
    clear(): void {
        this.ssfmodel.clear();
        this.modelChanged.emit({});
    }
}
