import {Component, Input} from '@angular/core';
import {MapRangeFilterItemModel} from './MapRangeFilterItemModel';

@Component({
    selector: 'maprangefilter',
    templateUrl: 'MapRangeFilterComponent.html',
    styleUrls: ['../../filters.css']
})

export class MapRangeFilterComponent {
    @Input() model: MapRangeFilterItemModel;
    @Input() openContent: Function;
    @Input() isCohortFilter: boolean;
}
