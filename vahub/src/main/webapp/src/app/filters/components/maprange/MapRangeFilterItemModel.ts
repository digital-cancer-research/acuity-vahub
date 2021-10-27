import {RangeFilterItemModel} from '../range/RangeFilterItemModel';
import {BaseMapFilterItemModel} from '../BaseMapFilterItemModel';
import * as  _ from 'lodash';
import {FILTER_TYPE} from '../dtos';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

export class MapRangeFilterItemModel extends BaseMapFilterItemModel {

    static MAP_RANGE_DATE_DISPLAY_NAMES = {
        drugsTotalDurationInclBreaks: `<%= drugName %> Days on Treatment (incl. breaks)`,
        drugsTotalDurationExclBreaks: `<%= drugName %> Days on Treatment (excl. breaks)`
    };

    constructor(key: string, displayName: string) {
        super(FILTER_TYPE.MAP_RANGE, key, displayName);
    }

    createFilterItemModelInstance(drugName: string): BaseFilterItemModel {
        let listFilterDisplayName: string;
        const template: string = MapRangeFilterItemModel.MAP_RANGE_DATE_DISPLAY_NAMES[this.key];
        listFilterDisplayName = _.template(template)({drugName: drugName});
        return new RangeFilterItemModel(drugName, listFilterDisplayName, 1);
    }
}
