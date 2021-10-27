import {Component, Input} from '@angular/core';
import {MapListFilterItemModel} from './MapListFilterItemModel';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

@Component({
    selector: 'maplistfilter',
    templateUrl: 'MapListFilterComponent.html',
    styleUrls: ['../../filters.css']
})

export class MapListFilterComponent {
    @Input() model: MapListFilterItemModel;
    @Input() openedFilterModel: BaseFilterItemModel;
    @Input() openContent: Function;
    @Input() isCohortFilter: boolean;
}
