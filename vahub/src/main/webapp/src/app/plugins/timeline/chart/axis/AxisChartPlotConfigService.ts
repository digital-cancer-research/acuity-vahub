import {Injectable} from '@angular/core';
import {IZoom} from '../../store/ITimeline';
import {IChartPlotconfigService} from '../IChartPlotconfigService';
import {TimelineUtils} from '../TimelineUtils';
import {CustomPlotConfig} from '../../../../../vahub-charts/types/interfaces';

@Injectable()
export class AxisChartPlotConfigService implements IChartPlotconfigService {

    createPlotConfig(zoom: IZoom): CustomPlotConfig {
        return {
            chart: {
                height: 50,
                animationTime: 500,
                type: 'timeline-xaxis',
                margins: {
                    left: TimelineUtils.CHART_LEFT_MARGIN,
                    right: TimelineUtils.CHART_RIGHT_MARGIN + 8
                }
            },
            xAxis: [{
                borders: {
                    min: zoom.zoomMin,
                    max: zoom.zoomMax,
                }
            }],
        };
    }
}
