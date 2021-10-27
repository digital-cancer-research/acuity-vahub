import {FILTER_TYPE} from '../dtos';
import {ListFilterItemModel} from '../list/ListFilterItemModel';
import {BaseMapFilterItemModel} from '../BaseMapFilterItemModel';
import * as  _ from 'lodash';
import {BaseFilterItemModel} from '../BaseFilterItemModel';

export class MapListFilterItemModel extends BaseMapFilterItemModel {

    static MAP_LIST_FILTER_DISPLAY_NAMES = {
        drugsActionTaken: `Action Taken on <%= drugName %>`,
        drugsCausality: `Causality, <%= drugName %>`,
        drugsDosed: `<%= drugName %> Dosed`,
        drugsMaxDoses: `Max. Dose per Admin. of <%= drugName %>`,
        drugsMaxFrequencies: `Max. Admin. Frequency of <%= drugName %>`,
        drugsDiscontinued: `<%= drugName %> Discontinuation`,
        drugsDiscontinuationReason: `<%= drugName %> Main Reason for Discontinuation`,
        drugsDiscontinuationDate: `Date of Discontinuation of <%= drugName %>`,
        biomarkerGroups: `<%= drugName %> Biomarkers`
    };

    constructor(key: string, displayName: string) {
        super(FILTER_TYPE.MAP_LIST, key, displayName);
    }

    createFilterItemModelInstance(drugName: string): BaseFilterItemModel {
        let listFilterDisplayName: string;
        const template: string = MapListFilterItemModel.MAP_LIST_FILTER_DISPLAY_NAMES[this.key];
        listFilterDisplayName = _.template(template)({drugName: drugName});
        return new ListFilterItemModel(drugName, listFilterDisplayName);
    }
}
