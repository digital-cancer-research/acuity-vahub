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
import {BIOMARKERS_Y_MAX, MODIFIED_MUTATION_COLOR, NO_DATA_PLACEHOLDER, ScaleTypes, TabId} from '../../../../store';
import {maxBy} from 'lodash';

import {AbstractPlotConfigService} from '../AbstractPlotConfigService';
import {HeatmapPlotUtilsService} from '../../../../store/plotutils';
import {IPlotConfigService} from '../../IPlotConfigService';
import OutputHeatMapEntry = InMemory.OutputHeatMapEntry;
import {CustomPlotConfig, SeriesOptions, UserOptions} from '../../../../../../../vahub-charts/types/interfaces';

export interface HeatMapBoostSeriesObject {
    svgSeries: OutputHeatMapEntry[];
    boostedSeries: OutputHeatMapEntry[];
    emptySeries: OutputHeatMapEntry[];
}

@Injectable()
export class HeatMapPlotConfigService extends AbstractPlotConfigService implements IPlotConfigService {

    static DEFAULT_FILLING_SERIES = 'DEFAULT_FILLING_SERIES';
    static HEATMAP_MIN_PLOT_PADDING = 170;
    static HEATMAP_MAX_HEIGHT = 600;
    static DEFAULT_GENOMIC_PROFILE_POINT_WIDTH = 5;
    static DEFAULT_GENOMIC_PROFILE_POINT_HEIGHT = 11;
    static DEFAULT_GENOMIC_PROFILE_POINT_BORDER_WIDTH = 1;
    static XAXIS_LABELS_HEIGHT_RATIO = 11;
    static MERGED_XAXIS_LABELS_HEIGHT_RATIO = 9;


    createPlotConfig(title: string, height: number, xcategories: Array<string>, ycategories: Array<string>): UserOptions {
        const customConfig: CustomPlotConfig = {
            chart: {
                type: 'heatmap',
                height: height,
                animationTime: 500
            },
            xAxis: [{
                type: ScaleTypes.CATEGORY_SCALE,
                categories: xcategories,
            }],
            yAxis: [{
                type: ScaleTypes.CATEGORY_SCALE,
                categories: ycategories,
            }]
        };
        return super.createDefaultPlotConfig(customConfig, height, title);
    }

    additionalOptions(tabId: TabId): UserOptions {
        switch (tabId) {
            case TabId.BIOMARKERS_HEATMAP_PLOT:
                return {
                    tooltip: {
                        formatter: function () {
                            const {
                                value: {
                                    gene,
                                    subjectCode,
                                    totalNumberOfAlterations,
                                    biomarkerParameters
                                }
                            } = this.point;


                            if (this.seriesName === HeatMapPlotConfigService.DEFAULT_FILLING_SERIES) {
                                return null;
                            }

                            const tableRows = biomarkerParameters.map(({
                                                                           alleleFrequency,
                                                                           aminoAcidChange,
                                                                           copyNumberAlterationCopyNumber,
                                                                           mutation,
                                                                           somaticStatus
                                                                       }) => {
                                return `<tr>
                                        <td>${mutation || NO_DATA_PLACEHOLDER}</td>
                                        <td>${somaticStatus || NO_DATA_PLACEHOLDER}</td>
                                        <td>${aminoAcidChange || NO_DATA_PLACEHOLDER}</td>
                                        <td>${copyNumberAlterationCopyNumber || NO_DATA_PLACEHOLDER}</td>
                                        <td>${alleleFrequency || NO_DATA_PLACEHOLDER}</td>
                                    </tr>`;
                            });

                            return `<div id="table-tooltip"><b>Subject:</b> <span>${subjectCode}</span><br/>
                                    <b>Gene:</b> <span>${gene}</span><br/>
                                    <b>Total number of alterations:</b> <span>${totalNumberOfAlterations}</span><br/>
                                <table class="table table-bordered xx-small-font">
                                    <thead>
                                        <th>Alteration<br/>type</th>
                                        <th>Somatic<br/>status</th>
                                        <th>Change in<br/>amino acid</th>
                                        <th>Copy<br/>number</th>
                                        <th>Variant allele<br/>frequency</th>
                                    </thead>
                                    <tbody>
                                        ${tableRows.join(' ')} <!-- it just calls join() by default which leads to commas in between -->
                                    </tbody>
                                </table></div>`;
                        }
                    }
                };
            default:
                return {};
        }
    }

    public modifyGreenColoredEntries(entries: OutputHeatMapEntry[], tabId: TabId): HeatMapBoostSeriesObject {
        const series: HeatMapBoostSeriesObject = {svgSeries: [], boostedSeries: [], emptySeries: []};
        if (tabId === TabId.BIOMARKERS_HEATMAP_PLOT) {
            const gradientEntries: SeriesOptions<OutputHeatMapEntry> = entries.map((entry) => {
                if (entry.color === MODIFIED_MUTATION_COLOR) {
                    const gradientColor = {
                        pattern: {
                            id: 'green-stripes-pattern',
                            path: {
                                d: 'M 0 5 L 5 0 M -1 1 L 1 -1 M 4 6 L 6 4',
                                stroke: '#008000',
                                strokeWidth: 1.3
                            },
                            width: '5',
                            height: '5',
                            patternUnits: 'userSpaceOnUse'
                        }
                    };
                    return {
                        ...entry,
                        color: gradientColor
                    };
                }
                return entry;
            });
            series.svgSeries = gradientEntries.filter((entry) => {
                return entry.color.pattern;
            });
            series.boostedSeries = gradientEntries.filter((entry) => {
                return !entry.color.pattern;
            });
        }
        return series;
    }

    /**
     * specific for Biomarkers plot. This method returns data with no mutation series
     */
    public fillWithNoMutationSeries(xcategories: string[], ycategories: string[]): OutputHeatMapEntry[] {
        const noMutationSeries = [];
        xcategories.forEach((xvalue, i) => {
            ycategories.forEach((yvalue, j) => {
                noMutationSeries.push({
                    color: HeatmapPlotUtilsService.BIOMARKERS_NO_MUTATION_COLOR,
                    name: '',
                    value: HeatmapPlotUtilsService.BIOMARKERS_NO_MUTATION_LABEL,
                    x: i,
                    y: j
                });
            });
        });
        return noMutationSeries;
    }

    /**
     * Calculates width of genomic_profile plot. It depends on how many subjects we have on x axis.
     * @param arrayLength - number of subjects
     * @returns {number} - width
     */
    public calculateGenomicProfilePlotWidth(arrayLength: number, maxPlotWidth: number): number {
        const calculatedWidth = arrayLength * (HeatMapPlotConfigService.DEFAULT_GENOMIC_PROFILE_POINT_WIDTH
            + HeatMapPlotConfigService.DEFAULT_GENOMIC_PROFILE_POINT_BORDER_WIDTH);
        return calculatedWidth > maxPlotWidth
            ? null : calculatedWidth + HeatMapPlotConfigService.HEATMAP_MIN_PLOT_PADDING;
    }

    /**
     * Calculates distance between top of the plot and bottom of title to set the bottom margin of the title.
     * The more genes there are on y axis, the less this distance is.
     * It is done because when there are less than 50 genes we want the plot to stay the same size and to be pinned to the bottom.
     * @param arrayLength - number of genes
     * @param xCategories - array of x axis labels, we need it to know how much space on the plot is occupied by vertical x axis labels
     * @param isExport - true when exporting/printing, we need to recalculate title margin while exporting cause title text is changing
     * @returns {number} - title margin bottom size
     */
    private calculateTitleBottomMargin(xCategories: string[], arrayLength: number, isExport = false): number {
        const longestLabel = maxBy(xCategories, 'length');
        // this means that we have merged datasets and dataset name appears in x axis label
        const ratio = longestLabel.includes('-') ? HeatMapPlotConfigService.MERGED_XAXIS_LABELS_HEIGHT_RATIO
            : HeatMapPlotConfigService.XAXIS_LABELS_HEIGHT_RATIO;
        const plotHeight = document.querySelector('.trellising-plot-window').clientHeight
            - longestLabel.length * ratio;
        const calculatedHeight = (arrayLength + 1) * (plotHeight / BIOMARKERS_Y_MAX
            + HeatMapPlotConfigService.DEFAULT_GENOMIC_PROFILE_POINT_BORDER_WIDTH);
        let margin = plotHeight - calculatedHeight;
        if (isExport) {
            margin -= document.getElementsByClassName('chart-title')[0].getBoundingClientRect().height;
        }
        return margin > 0 && calculatedHeight <= HeatMapPlotConfigService.HEATMAP_MAX_HEIGHT ? margin : null;
    }
}
