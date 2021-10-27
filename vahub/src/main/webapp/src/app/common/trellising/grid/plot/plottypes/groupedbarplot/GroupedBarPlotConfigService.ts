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
