import {IChartPlotConfigService} from '../IChartPlotConfigService';
import {Injectable} from '@angular/core';
import {ScaleTypes, TabId} from '../../../../store';
import {AbstractPlotConfigService} from '../AbstractPlotConfigService';
import {CustomPlotConfig, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Injectable()
export class LinePlotConfigService extends AbstractPlotConfigService implements IChartPlotConfigService {

    createPlotConfig(title: string, xAxisLabel: string, globalXAxisLabel: string,
                     yAxisLabel: string, globalYAxisLabel: string, height: number, tabId: TabId): UserOptions {
        const customConfig: CustomPlotConfig = {
            chart: {
                type: 'line',
                animationTime: 500
            },
            xAxis: [{
                title: {
                    text: xAxisLabel
                },
                type: ScaleTypes.CATEGORY_SCALE
            }],
            yAxis: [{
                title: {
                    text: yAxisLabel
                }
            }],
            tooltip: {
                formatter: function (): string {
                    if (!this) {
                        return null;
                    }
                    return 'Series: ' + this.series.name + '<br/>'
                        + globalYAxisLabel + ': ' + this.y + '<br/>'
                        + 'Time point: ' + this.category;
                }
            }
        };
        return super.createDefaultPlotConfig(customConfig, height, title);
    }
}
