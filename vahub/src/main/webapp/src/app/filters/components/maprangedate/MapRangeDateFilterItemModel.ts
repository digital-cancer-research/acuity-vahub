import {FILTER_TYPE} from '../dtos';
import {RangeDateFilterItemModel} from '../rangedate/RangeDateFilterItemModel';
import {BaseMapFilterItemModel} from '../BaseMapFilterItemModel';
import * as  _ from 'lodash';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

export class MapRangeDateFilterItemModel extends BaseMapFilterItemModel {

    static MAP_RANGE_DISPLAY_NAMES = {
        drugsDiscontinuationDate: `Date of <%= drugName %> Discontinuation`
    };

    constructor(key: string, displayName: string) {
        super(FILTER_TYPE.MAP_RANGE_DATE, key, displayName);
    }

    createFilterItemModelInstance(drugName: string): BaseFilterItemModel {
        let listFilterDisplayName: string;
        const template: string = MapRangeDateFilterItemModel.MAP_RANGE_DISPLAY_NAMES[this.key];
        listFilterDisplayName = _.template(template)({drugName: drugName});
        return new RangeDateFilterItemModel(drugName, listFilterDisplayName);
    }
}
