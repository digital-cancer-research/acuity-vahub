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

import {ITrellises, IPlot, TabId, TrellisDesign, ILegend, ZoomRanges} from '../ITrellising';
import {List} from 'immutable';
import * as _ from 'lodash';

export class BaseChartUtilsService {
    protected static DEFAULT_MIN = 999999999999999;
    protected static DEFAULT_MAX = -999999999999999;
    protected static CHART_COLOR_LIST = [
        '#DE606C',
        '#3CB44C',
        '#F9DA00',
        '#F58231',
        '#9FDB61',
        '#006480',
        '#731975',
        '#CC9933',
        '#FF9966',
        '#819999',
        '#26C0D0',
        '#E85BD5',
        '#A05100',
        '#808000',
        '#911EB4',
        '#808080',
        '#FFD052',
        '#665DD8',
        '#DC76D6',
        '#FF9900',
        '#CC9966',
        '#CC6633',
        '#660099',
        '#5E6337',
        '#AAFFC3',
        '#3A6B99',
        '#EAAEBE',
        '#E1E05F',
        '#663366',
        '#B0CBAA',
        '#E85BD5'
    ];

    public static orderPlots(plots: List<IPlot>, plotDatas: List<IPlot>): List<IPlot> {
        return plots.withMutations((currentPlots) => {
            const plotDataSize = plotDatas ? plotDatas.size : 0;
            currentPlots.forEach((plot, index) => {
                const thisTrellis = plot.get('trellising');
                const plotDataFromServer = plotDatas.find((plotData: IPlot) => {
                    const trellising = plotData.get('trellising').toJS();
                    return _.isEqual(
                        _.sortBy(thisTrellis.map(x => _.omit(x, 'category')), 'trellisedBy'),
                        _.sortBy(trellising.map(x => _.omit(x, 'category')), 'trellisedBy')
                    );
                });
                if (plotDataFromServer) {
                    currentPlots.setIn([index, 'data'], plotDataFromServer.get('data'));
                    currentPlots.setIn([index, 'plotType'], plotDataFromServer.get('plotType'));
                } else {
                    currentPlots.setIn([index, 'data'], List());
                    currentPlots.setIn([index, 'plotType'], plotDataSize ? plotDatas.get(0).get('plotType') : null);
                }
            });
        });
    }

    public static getColor(index: number): string {
        const colorKey = index % 30;
        return this.CHART_COLOR_LIST[colorKey];
    }

    public extractLegend(plotsDatas: List<IPlot>, tabId: TabId, trellises: List<ITrellises>): ILegend[] {
        return [];
    }

    public calculateZoomRanges(plots: List<IPlot>, trellisDesign: TrellisDesign, tabId: TabId): ZoomRanges {
        switch (trellisDesign) {
            case TrellisDesign.CONTINUOUS_OVER_TIME:
                return {
                    x: this.calculateZoomRangesContinuousX(plots),
                    y: {min: 0, max: 100}
                };
            case TrellisDesign.CATEGORICAL_OVER_TIME:
                return {
                    x: {min: 0, max: 100},
                    y: {min: 0, max: 100}
                };
            case TrellisDesign.CATEGORICAL_COUNTS_AND_PERCENTAGES:
                return {
                    x: this.calculateZoomRangesCategoricalX(plots),
                    y: this.calculateZoomRangesCategoricalY(plots)
                };
            case TrellisDesign.VARIABLE_Y_CONST_X:
                return {
                    x: this.calculateZoomRangesScatterX(plots, tabId),
                    y: this.calculateZoomRangesScatterY(plots, tabId)
                };
            case TrellisDesign.NO_AXIS:
            case TrellisDesign.VARIABLE_Y_VARIABLE_X:
                return {
                    x: {min: 0, max: 100},
                    y: {min: 0, max: 100}
                };
            default:
                break;
        }
    }

    protected calculateZoomRangesScatterX(plots: List<IPlot>, tabId: TabId): { max: number, min: number } {
        let allDataNull = true;
        plots.forEach((plot: IPlot) => {
            if (plot.get('data') !== null) {
                allDataNull = false;
                return false;
            }
        });
        if (allDataNull) {
            return {max: 0, min: 0};
        } else {
            const zoomRanges: { max: number, min: number } = this.getXZoomRanges(plots);
            if (tabId === TabId.LIVER_HYSLAW || tabId === TabId.SINGLE_SUBJECT_LIVER_HYSLAW) {
                zoomRanges.min = 0.0;
                zoomRanges.max = zoomRanges.max > 4.0 ? zoomRanges.max : 4.0;
            }
            return zoomRanges;
        }
    }

    protected calculateZoomRangesScatterY(plots: List<IPlot>, tabId: TabId): { max: number, min: number } {
        // TODO: it seems strange to check for null in every method
        let allDataNull = true;
        plots.forEach((plot: IPlot) => {
            if (plot.get('data') !== null) {
                allDataNull = false;
                return false;
            }
        });
        if (allDataNull) {
            return {max: 0, min: 0};
        } else {
            const zoomRanges: { max: number, min: number } = this.getYZoomRanges(plots);
            if (tabId === TabId.LIVER_HYSLAW || tabId === TabId.SINGLE_SUBJECT_LIVER_HYSLAW) {
                zoomRanges.min = 0.0;
                zoomRanges.max = zoomRanges.max > 4.0 ? zoomRanges.max : 4.0;
            }
            return zoomRanges;
        }
    }

    protected calculateZoomRangesCategoricalX(plots: List<IPlot>): { max: number, min: number } {
        const allDataNull: boolean = plots.reduce((nonNull: any, plot: IPlot) => {
            return !nonNull ? (plot.get('data') === null || plot.get('data').isEmpty()) : nonNull;
        }, false);
        if (allDataNull) {
            return {max: 0, min: 0};
        } else {
            return plots.reduce((range: any, plot: IPlot) => {
                return this.getCategoricalXZoomRanges(range, plot);
            }, {min: 0, max: 0});
        }
    }

    protected calculateZoomRangesCategoricalY(plots: List<IPlot>): { max: number, min: number } {
        const allDataNull: boolean = plots.reduce((nonNull: any, plot: IPlot) => {
            return !nonNull ? plot.get('data') === null : nonNull;
        }, false);
        if (allDataNull) {
            return {max: 0, min: 0};
        } else {
            return plots.reduce((range: any, plot: IPlot) => {
                return this.getCategoricalYZoomRanges(range, plot);
            }, {min: 0, max: 0});
        }
    }

    protected calculateZoomRangesContinuousX(plots: List<IPlot>): { max: number, min: number } {
        //Use extreme defaults to capture actual min and max
        const allDataNull: boolean = plots.reduce((nonNull: any, plot: IPlot) => {
            return nonNull && (plot.get('data') === null || plot.get('data').size === 0);
        }, true);
        if (allDataNull) {
            return {max: 0, min: 0};
        } else {
            const zoomRanges: { max: number, min: number } = plots.filter(plot => plot.get('data')).reduce((range: any, plot: IPlot) => {
                const thisRange: { max: number, min: number } = this.getContinuousXZoomRanges(range, plot);
                if (thisRange.max > range.max) {
                    range.max = thisRange.max;
                }
                if (thisRange.min < range.min) {
                    range.min = thisRange.min;
                }
                return range;
            }, {max: BaseChartUtilsService.DEFAULT_MAX, min: BaseChartUtilsService.DEFAULT_MIN});
            zoomRanges.min = zoomRanges.min === null ? 0 : zoomRanges.min;
            if (zoomRanges.min === zoomRanges.max && zoomRanges.max !== undefined) {
                zoomRanges.min -= 1.0;
                zoomRanges.max += 1.0;
            }
            return zoomRanges;
        }
    }

    protected getXZoomRanges(plots: List<IPlot>): { min: number, max: number } {
        return {min: undefined, max: undefined};
    }

    protected getYZoomRanges(plots: List<IPlot>): { min: number, max: number } {
        return {min: undefined, max: undefined};
    }

    protected getCategoricalXZoomRanges(range: any, plot: IPlot): { min: number, max: number } {
        return range;
    }

    protected getCategoricalYZoomRanges(range: any, plot: IPlot): { min: number, max: number } {
        return range;
    }

    protected getContinuousXZoomRanges(range: any, plot: IPlot): { min: number, max: number } {
        return range;
    }

    protected getLegendTitle(tabId: TabId, label: string): string {
        switch (tabId) {
            case TabId.AES_COUNTS_BARCHART:
                return label.replace('MAX_SEVERITY_GRADE', 'Maximum experienced severity grade');
            case TabId.LAB_LINEPLOT:
                return label.replace('ARM', 'Actual study arm');
            default:
                return label;
        }
    }
}
