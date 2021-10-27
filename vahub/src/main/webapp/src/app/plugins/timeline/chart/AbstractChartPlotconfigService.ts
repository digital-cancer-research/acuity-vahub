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

import {merge} from 'lodash';
import {IChartPlotconfigService} from './IChartPlotconfigService';

export abstract class AbstractChartPlotconfigService implements IChartPlotconfigService {

    /**
     * Default implementation of plotconfig
     * NOTE: only add properties if the configuration is gonig to be shared by all plot
     *
     * @param customPlotConfigs an array of customized plotconfigs
     * @returns {{chart: any}}
     */
    createPlotConfig(...customPlotConfigs: any[]): any {
        const defaultPlotConfig = {
            // Options regarding the chart area and plot area as well as general chart options.
            chart: {
                // marginLeft: 200,
                // marginRight: 50,
                borderWidth: 0,

                // disable animation
                animation: false
            },

            /**
             * Disable exporting button for narrative plot related plots
             */
            exporting: {
                enabled: false
            },

            /**
             * The plotOptions is a wrapper object for config objects for each series type. The config objects for each series
             * can also be overridden for each series item as given in the series array.
             */
            plotOptions: {
                animation: false,
                shadow: false,
                dataLabels: {
                    style: {
                        textShadow: false
                    }
                }
            },

            tooltip: {
                outside: true,
                delayForDisplay: 500,
                hideDelay: 50,
                borderWidth: 0,
                shadow: false,
                animation: false,
                style: {
                    padding: 0
                }
            },
            /**
             * The chart's main title.
             */
            title: {
                /**
                 * set the text to an empty string and stop the chart adapting to fit the title in.
                 * This is the closest we can get to removing the title!
                 */
                text: '',
                floating: true
            },

            /**
             * The X axis or category axis. Normally this is the horizontal axis, though if the chart is inverted this is
             * the vertical axis. In case of multiple axes, the xAxis node is an array of configuration objects.
             */
            xAxis: [{}],

            /**
             * The y axis or value axis. Normally this is the vertical axis, though if the chart is inverted this is
             * the horizontal axis. In case of multiple axes, the yAxis node is an array of configuration objects.
             */
            yAxis: [{}],

            /**
             * The legend is a box containing a symbol and name for each series item or point item in the chart.
             */
            legend: {
                /**
                 * by default, we don't want to show the legend.
                 */
                enabled: false
            },

            /**
             * The data series to append to the chart. Plot options for the specific type of plot can be added to a series
             * individually - this can be used to change how some datapoints appear while leaving other series as is.
             */
            series: [],
        };

        customPlotConfigs.unshift(defaultPlotConfig);
        return this.mergePlotConfigs(customPlotConfigs);
    }

    private mergePlotConfigs(plotConfigs: any[]): any {
        let mergedPlotConfig = {};
        plotConfigs.forEach(plotConfig => {
            mergedPlotConfig = merge(mergedPlotConfig, plotConfig);
        });
        return mergedPlotConfig;
    }
}
