import {List, Map} from 'immutable';

import {BaseChartUtilsService} from './BaseChartUtilsService';
import {ILegend, ILegendEntry, IPlot, ITrellises, LegendSymbol, TabId} from '../ITrellising';

export class ChordDiagramUtilsService extends BaseChartUtilsService {

    public extractLegend(plotsDatas: List<IPlot>, tabId: TabId, trellises: List<ITrellises>): ILegend[] {
        const legends: Array<ILegend> = [];
        const trellisedBySetting = trellises.getIn([0, 'trellisedBy']);
        plotsDatas.forEach((plot: IPlot) => {
            const extractedColorMap = plot.getIn(['data', 0, trellisedBySetting, 'colorBook']);
            const colorMap: Map<string, string> = extractedColorMap ? extractedColorMap.toJS() : {};
            const legendEntries = Object.keys(colorMap).map(name => {
                return {
                    label: name,
                    color: colorMap[name],
                    symbol: LegendSymbol.CIRCLE
                };
            }) as ILegendEntry[];
            legends.push({entries: legendEntries, title: trellisedBySetting});
        });
        return legends;
    }
}
