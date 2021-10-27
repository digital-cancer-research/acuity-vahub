import {fromJS, List} from 'immutable';
import * as  _ from 'lodash';

import {ITrellises, IPlot, PlotRecord, TrellisCategory} from '../ITrellising';
import {AbstractPaginationService} from './AbstractPaginationService';

export class EmptyPlotsService extends AbstractPaginationService {

    public static generateEmptyPlots(limit: number, offset: number, trellises: List<ITrellises>): List<IPlot> {
        const trellisesJS: any[] = trellises.toJS();
        const series: any = _.filter(trellisesJS, {'category': TrellisCategory.NON_MANDATORY_SERIES});
        const trellisOptions = this.getNonSeriesTrellisOptionValues(trellisesJS);

        if (trellisOptions.length > 0) {

            const allTrellisCombinations = this.calculateTrellisCombinationForEachChart(trellisOptions);

            const trellisCombinationsOnThisPage = allTrellisCombinations.slice(offset - 1, offset - 1 + limit);
            const trellisesNonSeries: ITrellises[] = this.getNonSeriesTrellisOptions(trellisesJS);

            return fromJS(trellisCombinationsOnThisPage.map((page: string[]) => {
                return new PlotRecord({
                    data: null,
                    plotType: null,
                    series: series,
                    trellising: page.map((option: string, index: number) => {
                        return {
                            category: trellisesNonSeries[index].category,
                            trellisedBy: trellisesNonSeries[index].trellisedBy,
                            trellisOption: option
                        };
                    })
                });
            }));
        } else {
            return fromJS([
                new PlotRecord({
                    data: null,
                    plotType: null,
                    trellising: [],
                    series: series
                })
            ]);
        }
    }
}
