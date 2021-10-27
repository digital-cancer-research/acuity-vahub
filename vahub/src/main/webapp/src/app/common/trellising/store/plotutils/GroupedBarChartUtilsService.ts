import * as  _ from 'lodash';

import {IPlot} from '../ITrellising';
import {StackedBarChartUtilsService} from './StackedBarChartUtilsService';
import OutputBarChartData = Request.OutputBarChartData;

export class GroupedBarChartUtilsService extends StackedBarChartUtilsService {

    protected getCategoricalYZoomRanges(range: any, plot: IPlot): {min: number, max: number} {
        const groupedMaxValue: number = plot.getIn(['data']).toJS().reduce((maxValue: number, data: OutputBarChartData) => {
            const maxSeriesValue = _.max(data.series.map(x => x.value)) * 1.1; // Add 10% to allow selection
            maxValue = maxValue < maxSeriesValue ? maxSeriesValue : maxValue;
            return maxValue;
        }, 0);
        range.max = groupedMaxValue > range.max ? groupedMaxValue : range.max;
        return range;
    }
}
