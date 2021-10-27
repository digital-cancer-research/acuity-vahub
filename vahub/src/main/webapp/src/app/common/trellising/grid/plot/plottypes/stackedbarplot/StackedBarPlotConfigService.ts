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
import * as  _ from 'lodash';
import {ExportChartService} from '../../../../../../data/export-chart.service';
import {ConfigurationService} from '../../../../../../configuration/ConfigurationService';
import {CustomPlotConfig, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

@Injectable()
export class StackedBarPlotConfigService extends AbstractPlotConfigService implements IChartPlotConfigService {

    constructor(private exportChartsService: ExportChartService,
                private configService: ConfigurationService) {
        super(exportChartsService, configService);
    }
    createPlotConfig(title: string, xAxisLabel: string, globalXAxisLabel: string,
                     yAxisLabel: string, globalYAxisLabel: string, height: number, tabId: TabId): UserOptions {
        let customConfig: CustomPlotConfig = {
            chart: {
                type: 'stacked-bar-plot',
                animationTime: 500
            },
            xAxis: [{
                categories: [],
                title: {
                    text: xAxisLabel
                },
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
        customConfig = super.createDefaultPlotConfig(customConfig, height, title);
        return _.merge(customConfig, this.additionalOptions(title, xAxisLabel, globalXAxisLabel, yAxisLabel, globalYAxisLabel, tabId));

    }

    private additionalOptions(title: string, xAxisLabel: string, globalXAxisLabel: string,
                              yAxisLabel: string, globalYAxisLabel: string, tabId: TabId): UserOptions {
        switch (tabId) {
            case TabId.CONMEDS_BARCHART:
                return {
                    tooltip: {
                        formatter: function (): string {
                            const x = globalXAxisLabel + ': <b>' + this.x + '</b><br/>';
                            const unit = globalYAxisLabel.indexOf('Percentage') > -1 ? ' %' : '';
                            const y = globalYAxisLabel + ': <b>' + this.y.toFixed(2) + unit + '</b><br/>';
                            const series = 'Series: <b>' + this.series.name + '</b><br/>';
                            let percentage = '';
                            let subjectCount = '';
                            let atcCodeTranslation = '';
                            if (globalXAxisLabel.toUpperCase().indexOf('ATC') !== -1) {
                                atcCodeTranslation = 'ATC code translation: <b>'
                                    + ((this.tooltip && this.tooltip.atcText) ? this.tooltip.atcText : 'N/A')
                                    + '</b><br/>';
                            }
                            if (globalYAxisLabel.toUpperCase().indexOf('PERCENT') === -1
                                && this.tooltip
                                && this.tooltip.percentOfSubjects) {
                                percentage = 'Percentage of subjects: <b>' + this.tooltip.percentOfSubjects.toFixed(2) + '%</b><br/>';
                            }
                            if (globalYAxisLabel.toUpperCase().indexOf('SUBJECTS (COUNT)') === -1) {
                                subjectCount = 'Count of subjects: <b>' + this.totalSubjects + '</b><br/>';
                            }

                            return x + y + series + percentage + subjectCount + atcCodeTranslation;
                        }
                    },
                };
            case TabId.QT_PROLONGATION:
                return {
                    tooltip: {
                        formatter: function (): string {
                            return `
                                <span>${globalXAxisLabel}: <b>${this.x}</b></span><br/>
                                <span>${globalYAxisLabel}: <b>${this.y}</b></span><br/>
                            `;
                        }
                    }
                };
            default:
                return {};
        }
    }

}
