import {List, Map} from 'immutable';
import {filter} from 'lodash';
import {
    ILegend,
    ILegendEntry,
    IPlot,
    ITrellises,
    LegendSymbol,
    MODIFIED_MUTATION_COLOR,
    TabId,
    TrellisCategory,
    TrellisDesign,
    ZoomRanges
} from '../ITrellising';
import {BaseChartUtilsService} from './BaseChartUtilsService';

export class HeatmapPlotUtilsService extends BaseChartUtilsService {

    static BIOMARKERS_NO_MUTATION_COLOR = '#d6d6d6';
    static BIOMARKERS_NO_MUTATION_LABEL = 'No mutation detected';

    public extractLegend(plotsDatas: List<IPlot>, tabId: TabId, trellises: List<ITrellises>): ILegend[] {
        const legends: Array<ILegend> = [];
        const title = filter(<ITrellises[]>trellises.toJS(), {'category': TrellisCategory.NON_MANDATORY_SERIES})
            .map(x => x.trellisedBy).join(', ');

        const entries = plotsDatas
            .flatMap(plot => <Map<string, any>>plot.getIn(['data', 'entries'])) // get all entries
            .groupBy(k => k.get('color')) // remove duplicates
            .map(k => k.first())
            .toList() // convert back to list
            .map(k => {
                const label: string = k.get('name');
                const legendEntry: ILegendEntry = {
                    color: k.get('color'),
                    label,
                    symbol: LegendSymbol.CIRCLE
                };

                if (legendEntry.color === MODIFIED_MUTATION_COLOR) {
                    legendEntry.className = 'stripped-green-color';
                }

                return legendEntry;
            })
            .toSet()
            .toArray();

        return [{title, entries}];
    }

    calculateZoomRanges(plots: List<IPlot>, trellisDesign: TrellisDesign, tabId): ZoomRanges {
        const x = plots.reduce((size, plot): number => {
            return size + plot.data.get('xcategories').size;
        }, 0);

        const y = plots.reduce((size, plot): number => {
            return size + plot.data.get('ycategories').size;
        }, 0);

        return {
            x: {
                min: 1,
                max: x
            },
            y: {
                min: 1,
                max: y
            }
        };
    }

    protected getCategoricalXZoomRanges(range: any, plot: IPlot): { min: number, max: number } {
        console.log(range, plot);

        return {
            min: 1,
            max: 100
        };
    }

    protected getCategoricalYZoomRanges(range: any, plot: IPlot): { min: number, max: number } {
        console.log(range, plot);

        return {
            min: 1,
            max: 100
        };
    }
}
