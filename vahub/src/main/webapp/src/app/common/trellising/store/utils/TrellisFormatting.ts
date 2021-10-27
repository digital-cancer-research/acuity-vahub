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

import {isArray, merge, reduce} from 'lodash';
import {fromJS, List} from 'immutable';

import {PaginatedTrellisService} from '../services/PaginatedTrellisService';
import {IChartSelection, ITrellis, ITrellises, PlotSettings} from '../ITrellising';
import {AxisOption, XAxisOptions} from '../actions/TrellisingActionCreator';

/**
 * should format options to correct format
 * if options has options prop - return as is
 * if option is array (old approach?) - return an object with options prop
 * if neither - return an object with empty options
 * @param options
 * @returns {Request.AxisOptions<string>}
 */
export function formatAxisOptions(options: any): XAxisOptions {
    if (options.options) {
        return options;
    } else if (isArray(options)) {
        return <XAxisOptions>{
            hasRandomization: false,
            drugs: [],
            options: options.map((option: string): AxisOption => {
                return <AxisOption>{
                    groupByOption: option,
                    binableOption: false,
                    supportsDuration: false,
                    timestampOption: false
                };
            })
        };
    }

    return <XAxisOptions>{
        hasRandomization: false,
        drugs: [],
        options: []
    };
}

/**
 * should create a payload for new chart data request
 * @param xAxisOption - selected x axis option
 * @param yAxisOption - selected y axis option
 * @param trellisOptions - current trellising (only MANDATORY_TRELLIS options)
 * @param limit - limit of charts per page
 * @param offset - offset from the first page()
 * @param colorByOptions - list of options to color by
 * @param plotSettings - settings set for the plot in the right panel, like line aggregation type etc
 * @param filterTrellisByOptions - list of trellises from current plot placeholders
 * @returns {any}
 */
export function constructDataRequestPayload(xAxisOption: Request.AxisOptions<string>,
                                            yAxisOption: Request.AxisOptions<string>,
                                            trellisOptions: any[],
                                            limit: number,
                                            offset: number,
                                            colorByOptions: List<ITrellises>,
                                            plotSettings: PlotSettings,
                                            filterTrellisByOptions?: List<Array<ITrellis>>,
                                            mainPlotSelection?: any): any {
    const trellisings: List<ITrellises> = PaginatedTrellisService.paginatedTrellis(limit, offset, fromJS(trellisOptions));
    let seriesBy;
    if (plotSettings && plotSettings.size > 0) {
        seriesBy = {
            groupByOption: plotSettings.get('trellisedBy'),
            additionalSettings: plotSettings.get('trellisOptions'),
            params: {}
        };
    }
    let colorBy;
    if (colorByOptions && colorByOptions.size > 0) {
        colorBy = {
            groupByOption: colorByOptions.map((x: ITrellises) => x.get('trellisedBy')).join(', '),
            params: null
        };
    }
    return {
        settings: {
            options: {
                'X_AXIS': xAxisOption,
                'Y_AXIS': yAxisOption,
                'COLOR_BY': colorBy,
                'SERIES_BY': seriesBy
            },
            trellisOptions: trellisings.map(trellising => {
                return {
                    groupByOption: trellising.get('trellisedBy'),
                    params: null
                };
            }).toJS()
        },
        filterByTrellisOptions: filterTrellisByOptions.map(plotTrellising => {
            return reduce(plotTrellising.map((option: ITrellis) => {
                return {[option.trellisedBy]: option.trellisOption};
            }), merge, {});
        }).toArray(),
        mainPlotSelection
    };
}

// Categorical - ChartSelectionItemRange
// BarChart - ChartSelectionItem
export function formatSelection(selections: List<IChartSelection>, yAxisOption: any): any[] {
    // [
    //     {
    //         'trellising': [
    //             {
    //                 'category': 'MANDATORY_TRELLIS',
    //                 'trellisedBy': 'MEASUREMENT',
    //                 'trellisOption': 'Gamma-Glutamyltransferase (U/L)'
    //             }
    //         ],
    //         'range': [
    //             {
    //                 'categories': [
    //                     '27.0',
    //                     '28.0',
    //                     '28.01'
    //                 ],
    //                 'xMin': 26.920847222222225,
    //                 'xMax': 41.79633333333334,
    //                 'yMin': -128.54142857142858,
    //                 'yMax': 90.23238095238094
    //             }
    //         ],
    //         'bars': []
    //     }
    // ]
    return selections.map((selection: IChartSelection) => {
        const trellising: List<ITrellis> = selection.get('trellising');
        // Here we basically assume that if
        // we have bars - then chart should not have ranges but
        // dblClick selection always returns bars
        const bars = selection.get('bars');
        const columns = selection.get('columns');
        const ranges = selection.get('range');
        const markers = selection.get('selectionItems');
        if (bars && bars.size > 0) {
            return bars.map(bar => {
                return {
                    selectedTrellises: reduce(trellising.map((option) => {
                        return {[option.get('trellisedBy')]: option.get('trellisOption')};
                    }).toArray(), merge, {}),
                    selectedItems: {
                        /**
                         * We add "NONE" option for Cerebrovascular on the frontend,
                         * @see {@link generateAvailableOptions}
                         * although BE can't deserialize it, so we should not send it
                         * here it's called 'All' because it gets transformed in
                         * {@link LabelPipe.transform}
                         */
                        'X_AXIS': bar.get('category') !== 'All' ? bar.get('category') : undefined,
                        'Y_AXIS': bar.get('yAxis') || !isNaN(bar.get('yAxis')) ? bar.get('yAxis') : undefined,
                        'COLOR_BY': bar.get('series') ? bar.get('series') !== 'All' ? bar.get('series') : undefined : undefined,
                    }
                };
            });
        } else if (columns && columns.size > 0) {
            // example of columns is tumour prior therapies plot
            // we send start, end and category of horizontal column
            return columns.map(column => {
                return {
                    selectedTrellises: reduce(trellising.map((option) => {
                        return {[option.get('trellisedBy')]: option.get('trellisOption')};
                    }).toArray(), merge, {}),
                    selectedItems: {
                        'Y_AXIS': column.get('category'),
                        'START': column.get('start'),
                        'END': column.get('end'),
                        'SERIES_BY': column.get('series')
                    }
                };
            });
        } else if (ranges && ranges.size > 0) {

            return ranges.map(range => {
                return range.get('categories').map(category => {
                    return {
                        selectedTrellises: reduce(trellising.map((option) => {
                            const yAxisParams = yAxisOption.params.trellisingParams;
                            return {[option.get('trellisedBy')]: option.get('trellisOption'), ...yAxisParams};
                        }).toArray(), merge, {}),
                        selectedItems: {
                            'X_AXIS': category
                        },
                        range: {
                            minimum: parseFloat(range.get('yMin')).toFixed(2),
                            maximum: parseFloat(range.get('yMax')).toFixed(2)
                        }
                    };
                });
            });
        } else {
            return markers.map(marker => {
                return {
                        'X_AXIS': marker.get('X_AXIS'),
                        'Y_AXIS': marker.get('Y_AXIS'),

                };
            });
        }
    }).flatten().toJS();
    // return [<ChartSelectionItem>{
    //     selectedTrellises: {
    //         'MEASUREMENT': 'ALT'
    //     },
    //     selectedItems: {
    //         'X_AXIS': 1.0
    //     },
    //     range: {
    //         minimum: 1,
    //         maximum: 2
    //     }
    // }];

    // return selectionItems: [
    //     {
    //         'selectedTrellises': {
    //             'ARM': 'Placebo'
    //         },
    //         'selectedItems': {
    //             'COLOR_BY': 'GRADE 5',
    //             'X_AXIS': 'General signs and symptoms NEC'
    //         }
    //     }
    //     ]
}

/**
 * Filters can depend on plot settings (for now this can be only in prior therapies plot)
 * This method creates settings for request for settings-dependant filters
 * @param settings
 * @returns {any}
 */
export function transformFiltersSettings(settings: PlotSettings): any {
    return {
        settings: {
            options: {
                'SERIES_BY': settings  ? {
                    groupByOption: settings.get('trellisedBy'),
                    params: {}
                } : undefined
            },
            trellisOptions: []
        },
        filterByTrellisOptions: [{}]
    };
}

export function handleFilterByTrellisOptions(yAxisOptions: any): any {
    return Object.keys(yAxisOptions).map(key => {
        return {[key]: yAxisOptions[key]};
    });
}
