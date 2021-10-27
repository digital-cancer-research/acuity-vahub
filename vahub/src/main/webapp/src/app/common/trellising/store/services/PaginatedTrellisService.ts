import {fromJS, List} from 'immutable';
import * as  _ from 'lodash';

import {ITrellises, IPlot, PlotRecord, TrellisCategory} from '../ITrellising';
import {AbstractPaginationService} from './AbstractPaginationService';

export class PaginatedTrellisService extends AbstractPaginationService {
    public static paginatedTrellis(limit: number, offset: number, trellises: List<ITrellises>): List<ITrellises> {

        const trellisesJS = trellises.toJS();
        _.remove(trellisesJS, (trellis: ITrellises) => {
            return trellis.trellisedBy === 'All';
        });
        const trellisOptions = this.getNonSeriesTrellisOptionValues(trellisesJS);

        if (trellisOptions.length > 0) {

            const allTrellisCombinations = this.calculateTrellisCombinationForEachChart(trellisOptions);
            const trellisCombinationsOnThisPage = allTrellisCombinations.slice(offset - 1, offset - 1 + limit);

            const trellisesNonSeries = this.getNonSeriesTrellisOptions(trellisesJS);
            const newTrellisJS = _.unzip(trellisCombinationsOnThisPage).map((newOptions: string[], index: number) => {
                return {
                    category: trellisesNonSeries[index].category,
                    trellisedBy: trellisesNonSeries[index].trellisedBy,
                    trellisOptions: _.uniq(newOptions)
                };
            });
            _.filter(trellisesJS, {'category': TrellisCategory.NON_MANDATORY_SERIES}).forEach((trellisSeries: any) => {
                newTrellisJS.push({
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: trellisSeries.trellisedBy,
                    trellisOptions: trellisSeries.trellisOptions
                });
            });

            return fromJS(newTrellisJS);
        } else {
            const newTrellisJS = [];
            _.filter(trellisesJS, {'category': TrellisCategory.NON_MANDATORY_SERIES}).forEach((trellisSeries: any) => {
                newTrellisJS.push({
                    category: TrellisCategory.NON_MANDATORY_SERIES,
                    trellisedBy: trellisSeries.trellisedBy,
                    trellisOptions: trellisSeries.trellisOptions
                });
            });
            return fromJS(newTrellisJS);
        }
    }
}
