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
import {AbstractPlotConfigService} from '../AbstractPlotConfigService';
import * as  _ from 'lodash';
import {DatasetViews} from '../../../../../../security/DatasetViews';
import {ExportChartService} from '../../../../../../data/export-chart.service';
import {ConfigurationService} from '../../../../../../configuration/ConfigurationService';
import {CustomPlotConfig, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Injectable()
export class ScatterPlotConfigService extends AbstractPlotConfigService implements IChartPlotConfigService {

    constructor(private datasetViews: DatasetViews,
                private exportChartsService: ExportChartService,
                private configService: ConfigurationService) {
            super(exportChartsService, configService);
    }

    createPlotConfig(title: string, xAxisLabel: string, globalXAxisLabel: string,
                     yAxisLabel: string, globalYAxisLabel: string, height: number): UserOptions {
        let customConfig: CustomPlotConfig = {
            chart: {
                type: 'scatter',
                animationTime: 500
            },
            tooltip: {
                formatter: function (): string {
                    const y = Math.round(this.y * 100) / 100;
                    const x = Math.round(this.x * 100) / 100;
                    return `${yAxisLabel}: <b>${y}</b><br>
                            ${xAxisLabel}: <b>${x}</b>`;
                }
            }
        };
        customConfig = super.createDefaultPlotConfig(customConfig, height, title);
        return _.merge(customConfig, this.additionalOptions());
    }

    private additionalOptions(): CustomPlotConfig {
        return {
            plotLines: [
                {
                    color: 'black',
                    axis: 'y',
                    value: 3.0,
                    width: 0.5,
                    zIndex: 5,
                    styles: {
                        'stroke-dasharray': 5,
                        'fill': 'none'
                    }
                },
                {
                    color: 'black',
                    axis: 'x',
                    value: 2.0,
                    width: 0.5,
                    zIndex: 5,
                    styles: {
                        'stroke-dasharray': 5,
                        'fill': 'none'
                    }
                }
            ]
        };
    }
}
