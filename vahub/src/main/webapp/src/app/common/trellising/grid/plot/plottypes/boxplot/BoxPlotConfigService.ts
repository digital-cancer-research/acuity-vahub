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

import {Injectable} from '@angular/core';
import {find, has, includes, isUndefined, merge} from 'lodash';

import {DatasetViews} from '../../../../../../security/DatasetViews';
import {TextUtils} from '../../../../../utils/TextUtils';
import {TabId} from '../../../../store';
import {AbstractPlotConfigService} from '../AbstractPlotConfigService';
import {IChartPlotConfigService} from '../IChartPlotConfigService';
import {ExportChartService} from '../../../../../../data/export-chart.service';
import {ConfigurationService} from '../../../../../../configuration/ConfigurationService';
import {CustomPlotConfig, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';
import {OutlierData} from './BoxPlotService';

@Injectable()
export class BoxPlotConfigService extends AbstractPlotConfigService implements IChartPlotConfigService {

    constructor(private datasetViews: DatasetViews,
                private exportChartsService: ExportChartService,
                private configService: ConfigurationService) {
        super(exportChartsService, configService);
    }

    createPlotConfig(title: string, xAxisLabel: string, globalXAxisLabel: string, yAxisLabel: string,
                     globalYAxisLabel: string, height: number, tabId: TabId): UserOptions {
        const chartConfig = this;
        let customConfig: CustomPlotConfig = {
            chart: {
                type: 'boxplot',
                animationTime: 500
            },
            tooltip: {
                formatter: function() {
                    let tooltip = '';
                        tooltip += TextUtils.changeWeekToAssessmentWeek(xAxisLabel ? xAxisLabel
                            : globalXAxisLabel + ': ' + this.category + '<br/>', tabId);
                        if (has(this, 'median')) {
                            tooltip += 'Upper whisker: ' + parseFloat(this.upperWhisker).toFixed(2) + '<br/>';
                            tooltip += 'Upper quartile: ' + parseFloat(this.upperQuartile).toFixed(2) + '<br/>';
                            tooltip += 'Median: ' + parseFloat(this.median).toFixed(2) + '<br/>';
                            tooltip += 'Lower quartile: ' + parseFloat(this.lowerQuartile).toFixed(2) + '<br/>';
                            tooltip += 'Lower whisker: ' + parseFloat(this.lowerWhisker).toFixed(2) + '<br/>';
                            if (tabId === TabId.DOSE_PROPORTIONALITY_BOX_PLOT || tabId === TabId.PK_RESULT_OVERALL_RESPONSE) {
                                tooltip += `Sample size: ${this.subjectCount}`;
                            }
                        } else {
                            if (includes(yAxisLabel, 'Measurement value')) { // abbreviate the axis title
                                tooltip += 'Measurement value: ' + parseFloat(this.y).toFixed(2) + '<br/>';
                            } else {
                                tooltip += (yAxisLabel ? yAxisLabel : globalYAxisLabel) + ': '
                                    + parseFloat(this.y).toFixed(2) + '<br/>';
                            }
                            let subjectId = this.subjectId;
                            if (isUndefined(this.subjectId)) {
                                subjectId = chartConfig.getSubjectIdWhenXAxisIsCategorical(this.series.chart.rawOutliers, this);
                            }
                            tooltip += 'Subject ID: ' + chartConfig.datasetViews.getSubjectEcodeById(subjectId);
                        }
                        return tooltip;
                }
            }
        };
        customConfig = super.createDefaultPlotConfig(customConfig, height, title);
        if (tabId === TabId.LAB_BOXPLOT) {
            customConfig.plotLines = super.addReferenceRangeLines(globalYAxisLabel);
        }
        return merge(customConfig, this.additionalOptions(title, xAxisLabel, globalXAxisLabel, yAxisLabel, globalYAxisLabel, tabId));
    }

    private getSubjectIdWhenXAxisIsCategorical(rawOutliers: Array<OutlierData>, point): string {
        return find(rawOutliers, (outlier: Array<OutlierData>) => {
                return outlier[0] === point.category && outlier[1] === point.y;
            }
        )[2];
    }

    private additionalOptions(title: string, xAxisLabel: string, globalXAxisLabel: string,
                              yAxisLabel: string, globalYAxisLabel: string, tabId: TabId): UserOptions {
        switch (tabId) {
            case TabId.CARDIAC_BOXPLOT:
                if (this.cardiacRequiringPlotLines(globalYAxisLabel, yAxisLabel)) {
                    return {
                            plotLines: [
                                {
                                    color: '#c0392b',
                                    styles: {
                                        'stroke-dasharray': 5,
                                        'fill': 'none'
                                    },
                                    axis: 'y',
                                    value: 450,
                                    width: 1,
                                    zIndex: 5
                                },
                                {
                                    color: '#c0392b',
                                    styles: {
                                        'stroke-dasharray': 5,
                                        'fill': 'none'
                                    },
                                    axis: 'y',
                                    value: 480,
                                    width: 1,
                                    zIndex: 5
                                },
                                {
                                    color: '#c0392b',
                                    styles: {
                                        'stroke-dasharray': 5,
                                        'fill': 'none'
                                    },
                                    axis: 'y',
                                    value: 500,
                                    width: 1,
                                    zIndex: 5
                                }
                            ]
                    };
                } else {
                    return {};
                }

            default:
                return {};
        }
    }

    private cardiacRequiringPlotLines(globalYAxisLabel: string, yAxisLabel: string): boolean {
        if (globalYAxisLabel !== 'Result value' || !yAxisLabel || yAxisLabel.length === 0) {
            return false;
        }
        return yAxisLabel.toUpperCase().indexOf('QTCF') > -1 ||
            yAxisLabel.toUpperCase().indexOf('QTC') > -1 ||
            yAxisLabel.toUpperCase().indexOf('QTCB') > -1;
    }
}
