import {IChartPlotConfigService} from '../IChartPlotConfigService';
import {Injectable} from '@angular/core';
import {ScaleTypes, TabId} from '../../../../store';
import {AbstractPlotConfigService} from '../AbstractPlotConfigService';
import {CustomPlotConfig, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Injectable()
export class BarLinePlotConfigService extends AbstractPlotConfigService implements IChartPlotConfigService {
    createPlotConfig(
        title: string,
        xAxisLabel: string,
        globalXAxisLabel: string,
        yAxisLabel: string,
        globalYAxisLabel: string,
        height: number, tabId: TabId
    ): UserOptions {
        const customConfig: CustomPlotConfig = {
            chart: {
                type: 'barline',
                animationTime: 500
            },
            xAxis: [{
                type: ScaleTypes.CATEGORY_SCALE
            }],
            yAxis: [
                {
                    title: {
                        text: yAxisLabel
                    },
                },
                {
                    title: {
                        text: undefined
                    },
                },
            ],
            tooltip: {
                formatter: function(): string {
                    const tooltipSubjects = this.points.filter((el) => el.category === this.x).reverse();
                    let tooltip = '';
                    tooltip +=  xAxisLabel ? xAxisLabel : globalXAxisLabel + ': <b>' + this.x + '</b><br/>';
                    tooltip += customConfig.yAxis[1].title.text + ': <b>' + this.subjects.find((el) => el.category === this.x).y + '</b><br/>';
                    tooltip +=  yAxisLabel ? yAxisLabel : globalYAxisLabel + '<br/>';
                    tooltipSubjects.forEach(el => {
                        tooltip += el.name + ': <b>' + el.y + '</b><br/>';
                    });
                    return tooltip;
                }
            }
        };
        if (tabId === TabId.AES_OVER_TIME ||
            tabId === TabId.EXACERBATIONS_OVER_TIME ||
            tabId === TabId.CVOT_ENDPOINTS_OVER_TIME ||
            tabId === TabId.CI_EVENT_OVERTIME ||
            tabId === TabId.CEREBROVASCULAR_EVENTS_OVER_TIME) {
            customConfig.yAxis[1].title.text = 'Number of subjects';
        }
        return super.createDefaultPlotConfig(customConfig, height, title);
    }

}
