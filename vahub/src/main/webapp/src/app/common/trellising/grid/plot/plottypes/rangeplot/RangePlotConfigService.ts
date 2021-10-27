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
import {TabId} from '../../../../store';
import {AbstractPlotConfigService} from '../AbstractPlotConfigService';
import {isEmpty} from 'lodash';
import {CustomPlotConfig, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Injectable()
export class RangePlotConfigService extends AbstractPlotConfigService implements IChartPlotConfigService {

    createPlotConfig(title: string, xAxisLabel: string, globalXAxisLabel: string,
                     yAxisLabel: string, globalYAxisLabel: string, height: number, tabId: TabId): UserOptions {
        const chartConfig = this;
        const customConfig: CustomPlotConfig = {
            chart: {
                type: 'range',
                animationTime: 500
            },
            yAxis: [{
                title: {
                    text: yAxisLabel
                }
            }],
            plotLines: [],
            tooltip: {
                formatter: function (): string {
                    if (!this || isEmpty(this)) {
                        return null;
                    }
                    let tooltip;
                    if (chartConfig.isSingleSubjectTab(tabId)) {
                        let valueRow;
                        const transformedYAxisLabel = globalYAxisLabel === 'Actual value' ? 'Result value' : globalYAxisLabel;
                        const titleUnit = title &&
                        title.split('(').length > 1 &&
                        title.split('(')[1].split(')')[0] ? title.split('(')[1].split(')')[0] : '';
                        const measurementTitle = title.split('vs.')[0];
                        if (this.ranges && this.ranges[0] !== this.ranges[1]) {
                            valueRow = 'Median ' + transformedYAxisLabel + ': ' + this.y + ' ' + titleUnit
                                + ' (' +
                                this.ranges[0] + ', ' + this.y + ', ' + this.ranges[1] + ')<br/>';
                        } else {
                            valueRow = transformedYAxisLabel + ': ' + this.y + ' ' + titleUnit + '<br/>';
                        }
                        tooltip = '<b>' + measurementTitle + '</b>'
                            + '<br/>' + globalXAxisLabel + ': ' + this.category + '<br/>'
                            + valueRow
                            + 'Number of data points: ' + this.dataPoints;
                        if (this.name) {
                            tooltip += '<br/>' + 'Source: ' + this.name;
                        } else if (this.series.name !== 'All') {
                            tooltip += '<br/>' + this.series.name;
                        }
                    } else {
                        tooltip = 'Source: ' + this.name + '<br/>'
                            + 'Average: ' + this.y + '<br/>'
                            + 'Time point: ' + this.category + '<br/>'
                            + 'Number of data points: ' + this.dataPoints + '<br/>'
                            + 'Standard error of the mean: ' + this.stdErr;
                    }
                    return tooltip;
                }
            }
        };
        if (tabId === TabId.LAB_LINEPLOT) {
            customConfig.plotLines = super.addReferenceRangeLines(globalYAxisLabel);
        }
        return super.createDefaultPlotConfig(customConfig, height, title);
    }

}
