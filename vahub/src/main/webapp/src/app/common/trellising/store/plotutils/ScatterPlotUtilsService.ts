import * as  _ from 'lodash';

import {IPlot} from '../ITrellising';
import {BaseChartUtilsService} from './BaseChartUtilsService';
import {List} from 'immutable';
import OutputScatterPlotEntry = InMemory.OutputScatterPlotEntry;

export class ScatterPlotUtilsService extends BaseChartUtilsService {

    protected getXZoomRanges(plots: List<IPlot>): {min: number, max: number} {
        return plots.reduce((range: any, plot: IPlot) => {
            if (!plot.get('data').isEmpty()) {
                const scatterChartData = <OutputScatterPlotEntry[]>plot.get('data').get('data').toJS();
                const values = scatterChartData && scatterChartData.map(entry => entry.x);
                if (values) {
                    range.min = !range.min || _.min(values) < range.min ? _.min(values) : range.min;
                    range.max = !range.max || _.max(values) > range.max ? _.max(values) : range.max;
                }
            }
            return range;
        }, {min: undefined, max: undefined});
    }

    protected getYZoomRanges(plots: List<IPlot>): {min: number, max: number} {
        return plots.reduce((range: any, plot: IPlot) => {
            if (!plot.get('data').isEmpty()) {
                const scatterChartData = <OutputScatterPlotEntry[]>plot.get('data').get('data').toJS();
                const values = scatterChartData.map(entry => entry.y);
                if (values) {
                    range.min = !range.min || _.min(values) < range.min ? _.min(values) : range.min;
                    range.max = !range.max || _.max(values) > range.max ? _.max(values) : range.max;
                }
            }
            return range;
        }, {min: undefined, max: undefined});
    }
}
