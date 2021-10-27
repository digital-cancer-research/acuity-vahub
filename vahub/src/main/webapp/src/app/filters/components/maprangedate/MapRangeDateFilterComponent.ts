import {Component, Input} from '@angular/core';
import {MapRangeDateFilterItemModel} from './MapRangeDateFilterItemModel';

@Component({
    selector: 'maprangedatefilter',
    templateUrl: 'MapRangeDateFilterComponent.html',
    styleUrls: ['../../filters.css']
})

export class MapRangeDateFilterComponent {
    @Input() model: MapRangeDateFilterItemModel;
    @Input() openContent: Function;
    @Input() isCohortFilter: boolean;
}
