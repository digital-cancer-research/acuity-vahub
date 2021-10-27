/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
