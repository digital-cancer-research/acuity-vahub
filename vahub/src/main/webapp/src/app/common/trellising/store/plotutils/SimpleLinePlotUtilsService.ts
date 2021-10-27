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

import {isNil, min, max} from 'lodash';
import {List, Map} from 'immutable';
import {
    AnalyteTrellises, EMPTY, ILegend, IPlot, ITrellises, LegendSymbol, TabId, TrellisDesign,
    ZoomRanges
} from '../ITrellising';
import {BaseChartUtilsService} from './BaseChartUtilsService';

export class SimpleLinePlotUtilsService extends BaseChartUtilsService {
    private static readonly MIN_CTDNA_FRACTION_VALUE = 0.001;
    private static readonly MIN_CTDNA_PERCENTAGE_VALUE = 0.1;

    public extractLegend(plotsDatas: List<IPlot>, tabId: TabId, trellises: List<ITrellises>): ILegend[] {
        const title = this.getLegendTitle(tabId, trellises);
        return this.getLegend(tabId, plotsDatas, title);
    }

    protected getLegendTitle(tabId: TabId, trellises: any): string {
        switch (tabId) {
            //First trellis in `trellises` list is requested from back-end side (.../trellising) and is what is marked
            // as mandatory_trellises, the second one is color by options, that are non_mandatory_series
            //So if you add trellis to any SIMPLE_LINEPLOT, be careful about the indices
            case TabId.ANALYTE_CONCENTRATION:
                return trellises.getIn([1, 'trellisedBy']);
            case TabId.CTDNA_PLOT:
                return trellises.getIn([0, 'trellisedBy']);
            case TabId.TL_DIAMETERS_PLOT:
            case TabId.TL_DIAMETERS_PER_SUBJECT_PLOT:
                return 'Overall visit response';
            default:
                return 'All';
        }
    }

    protected getLegend(tabId: TabId, plotsDatas: List<IPlot>, title: string): Array<ILegend> {
        let entries;

        if (tabId === TabId.ANALYTE_CONCENTRATION) {
            entries = plotsDatas
                .flatMap((plot): List<Map<string, any>> => plot.get('data'))
                .flatMap((datum): List<Map<string, any>> => datum.get('series'))
                .groupBy(entry => {
                    // TODO: check: may be we do not need else here
                    if (title) {
                        return entry.getIn(['name', 'colorByValue']);
                    } else {
                        return entry.getIn(['name', 'subjectCycle']);
                    }
                })
                .map(datum => datum.first())
                .map(entry => {
                    let label;
                    if (title) {
                        label = entry.getIn(['name', 'colorByValue']);
                        if (!label && title === AnalyteTrellises.CYCLE) {
                            label = EMPTY;
                        }
                    } else {
                        label = entry.getIn(['name', 'subjectCycle']);
                    }
                    return {
                        label: label,
                        color: entry.get('color'),
                        symbol: LegendSymbol.CIRCLE
                    };
                })
                .toArray();
        } else if (tabId === TabId.CTDNA_PLOT) {
            const formattedTitle = title !== 'SUBJECT' ? title.toLowerCase() : 'subjectCode';
            entries = plotsDatas
                .flatMap((plot): List<Map<string, any>> => plot.get('data')) // get data for all plots
                .flatMap((datum): List<Map<string, any>> => datum.get('series')) // get series for all plots
                .groupBy(entry => entry.get('name').get(formattedTitle)) // group by legend name
                .map(datum => datum.first())
                .map(entry => {
                    return {
                        label: entry.get('name').get(formattedTitle),
                        color: entry.get('color'),
                        symbol: LegendSymbol.CIRCLE
                    };
                })
                .toArray();
        } else {
            entries = plotsDatas
                .flatMap((plot): List<Map<string, any>> => plot.get('data')) // get data for all plots
                .flatMap((datum): List<Map<string, any>> => datum.get('series')) // get series for all plots
                .map((datum): Map<string, any> => datum
                    .delete('x')
                    .delete('y')
                ) // remove x and y keys redundant for legend
                .toSet() // remove duplicates
                .map(entry => {
                    return {
                        label: entry.get('name'),
                        color: entry.get('color'),
                        symbol: LegendSymbol.CIRCLE
                    };
                }) // add label instead of name and circle symbol
                .toArray();
        }

        return [{title, entries}];
    }

    calculateZoomRanges(plots: List<any>, trellisDesign: TrellisDesign, tabId: TabId): ZoomRanges {
        switch (tabId) {
            case TabId.TL_DIAMETERS_PLOT:
            case TabId.TL_DIAMETERS_PER_SUBJECT_PLOT:
                return this.calculateTlDiametersZoomRanges(plots, trellisDesign, tabId);
            case TabId.CTDNA_PLOT:
                return this.calculateCtDnaZoomRanges(plots, trellisDesign);
            default:
                return this.defaultZoomRanges(plots, trellisDesign);
        }
    }

    calculateTlDiametersZoomRanges(plots: List<any>, trellisDesign: TrellisDesign, tabId: TabId): ZoomRanges {
        const categories = [];
        return plots
            .map(plot => plot.get('data')) // get all data entries in plots list
            .flatten(1) // transform List<List<Map>> to List<Map>
            .map(plot => plot.get('series')) // get all series entries
            .flatten(1) // transform List<List<Map>> to List<Map>
            .map(plot => {
                let x;
                if (trellisDesign === TrellisDesign.CATEGORICAL_OVER_TIME) {
                    if (categories.indexOf(plot.get('x')) === -1) {
                        categories.push(plot.get('x'));
                    }
                    x = plot.get('x');
                } else {
                    x = plot.getIn(['x', 'start']) || plot.getIn(['x', 'end']);
                }
                return {
                    x,
                    y: plot.get('y')
                };
            }) // get x and y entries and transform it to object
            .filter(coords => {
                return !isNil(coords.x) && !isNil(coords.y);
            }) // remove all entries with undefined values
            .reduce((acc, coords) => {
                const x = trellisDesign === TrellisDesign.CATEGORICAL_OVER_TIME ? {min: 0, max: categories.length - 1}
                    : {
                        min: Math.min(coords.x, acc.x.min),
                        max: Math.max(coords.x, acc.x.max)
                    };
                return {
                    x,
                    y: {
                        min: Math.min(coords.y, acc.y.min),
                        max: Math.max(coords.y, acc.y.max)
                    }
                };
            }, {
                x: {min: BaseChartUtilsService.DEFAULT_MIN, max: BaseChartUtilsService.DEFAULT_MAX},
                y: {min: BaseChartUtilsService.DEFAULT_MIN, max: BaseChartUtilsService.DEFAULT_MAX}
            }); // find min and max
    }

    calculateCtDnaZoomRanges(plots: List<any>, trellisDesign: TrellisDesign): ZoomRanges {
        const categories = [];
        return plots
            .map(plot => plot.get('data')) // get all data entries in plots list
            .flatten(1) // transform List<List<Map>> to List<Map>
            .map(plot => plot.get('series')) // get all series entries
            .flatten(1) // transform List<List<Map>> to List<Map>
            .map(plot => {
                if (trellisDesign === TrellisDesign.CATEGORICAL_OVER_TIME && categories.indexOf(plot.get('x')) === -1) {
                    categories.push(plot.get('x'));
                }
                return {
                    x: plot.get('x'),
                    y: plot.get('y'),
                    fraction: plot.getIn(['name', 'vaf'])

                };
            }) // get x and y entries and transform it to object
            .filter(coords => {
                return !isNil(coords.x) && !isNil(coords.y);
            }) // remove all entries with undefined values
            .reduce((acc, coords) => {
                const x = trellisDesign === TrellisDesign.CATEGORICAL_OVER_TIME ? {min: 0, max: categories.length - 1}
                    : {
                        min: Math.min(coords.x, acc.x.min),
                        max: Math.max(coords.x, acc.x.max)
                    };
                // We want to know if y axis is fractional or percentage
                // So we can set min value that will look appropriate with log scale
                const yMin = coords.y === parseFloat(coords.fraction) ? SimpleLinePlotUtilsService.MIN_CTDNA_FRACTION_VALUE
                    : SimpleLinePlotUtilsService.MIN_CTDNA_PERCENTAGE_VALUE;
                const y = {
                    min: Math.min(yMin, acc.y.min),
                    max: Math.max(coords.y, acc.y.max)
                };
                return {x, y};
            }, {
                x: {min: BaseChartUtilsService.DEFAULT_MIN, max: BaseChartUtilsService.DEFAULT_MAX},
                y: {min: BaseChartUtilsService.DEFAULT_MIN, max: BaseChartUtilsService.DEFAULT_MAX}
            }); // find min and max
    }

    defaultZoomRanges(plots: List<IPlot>, trellisDesign: TrellisDesign): ZoomRanges {
        const arrX = [];
        const arrY = [];
        plots.forEach((val) => {
            val.get('data').forEach((series) => {
                series.get('series').forEach(el => {
                    const errorOnYAxis = el.get('standardDeviation') || 0;
                    arrX.push(el.get('x'));
                    if (errorOnYAxis !== 0) {
                        arrY.push(el.get('y') - errorOnYAxis, el.get('y') + errorOnYAxis);
                    } else {
                        arrY.push(el.get('y'));
                    }
                });
            });
        });
        // max by y is rounded to the closest number with one decimal because of the highest point being not visible
        // TODO: investigate the reason
        return {
            x: {
                min: min(arrX),
                max: max(arrX)
            },
            y: {
                min: min(arrY),
                max: Math.ceil(max(arrY) * 10) / 10
            }
        };
    }

    private getCycleLabel(cycle: Map<string, any>) {
        return cycle.get('treatmentCycle');
    }
}
