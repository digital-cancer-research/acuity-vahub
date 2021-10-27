import {IChartPlotConfigService} from '../IChartPlotConfigService';
import {AbstractPlotConfigService} from '../AbstractPlotConfigService';
import {Injectable} from '@angular/core';
import {ScaleTypes, TabId} from '../../../../store';
import {CustomPlotConfig, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Injectable()
export class GroupedBarPlotConfigService extends AbstractPlotConfigService implements IChartPlotConfigService {

    public static groupPadding = 0.1;

    createPlotConfig(
        title: string,
        xAxisLabel: string,
        globalXAxisLabel: string,
        yAxisLabel: string,
        globalYAxisLabel: string,
        height: number,
        tabId: TabId): UserOptions {
        const customConfig: CustomPlotConfig = {
            chart: {
                type: 'grouped-bar-plot',
                animationTime: 500
            },
            xAxis: [{
                title: {
                    text: xAxisLabel
                },
                categories: [],
                type: ScaleTypes.CATEGORY_SCALE
            }],
            tooltip: {
                formatter: function (): string {
                    const x = globalXAxisLabel + ': <b>' + this.x + '</b><br/>';
                    const unit = globalYAxisLabel.indexOf('Percentage') > -1 ? ' %' : '';
                    const y = globalYAxisLabel + ': <b>' + this.y.toFixed(2) + unit + '</b><br/>';
                    const series = 'Series: <b>' + this.name + '</b><br/>';
                    return x + y + series;
                }
            }
        };

        return super.createDefaultPlotConfig(customConfig, height, title);

    }

}
