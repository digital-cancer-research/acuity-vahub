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

import {BaseChartUtilsService} from './BaseChartUtilsService';
import {List, Map} from 'immutable';
import {ILegend, IPlot, ITrellises, LegendSymbol, TabId, TrellisCategory, ZoomRanges} from '../ITrellising';
import {isNil, uniqBy, minBy} from 'lodash';

export class ColumnRangeUtilsService extends BaseChartUtilsService {
    private static SUMMARY = 'Summary';

    /**
     * this methods returns data in inverted format - so X becomes Y and Y becomes X
     * @link ColumnRangePlotConfigService.additionalOptions // chart: { inverted: true }
     */
    calculateZoomRanges(plots: List<any>): ZoomRanges {
        const yMax = plots.reduce((size, plot): number => {
            return size + plot.getIn(['data', 'categories']).size;
        }, 0);
        const allDiagnosisDates = plots
            .map(plot => plot.getIn(['data', 'diagnosisDates']))
            .flatten(1)
            .map(entry => {
                return {
                    y: entry.get('y')
                };
            }).toArray();
        const minDiagnosisDate = minBy(allDiagnosisDates, 'y');

        const x = plots
            .map(plot => plot.getIn(['data', 'data']))
            .flatten(1)
            .map(entry => {
                return {
                    low: entry.get('low'),
                    high: entry.get('high')
                };
            })
            .filter(entry => {
                return !isNil(entry.low) && !isNil(entry.high);
            })
            .reduce((acc, currentValue) => {
                return {
                    min: Math.min(currentValue.low, acc.min),
                    max: Math.max(currentValue.high, acc.max)
                };
            }, {
                min: BaseChartUtilsService.DEFAULT_MIN,
                max: BaseChartUtilsService.DEFAULT_MAX
            });

        return {
            x: {
                min: minDiagnosisDate ? Math.min(x.min, minDiagnosisDate.y) : x.min,
                max: x.max
            },
            y: {
                min: 0,
                max: yMax - 1
            }
        };
    }

    protected getLegendTitle(tabId: TabId, label): string {
        return label || 'All';
    }

    public extractLegend(plotsDatas: List<IPlot>, tabId: TabId, trellises: List<ITrellises>): ILegend[] {
        const label = trellises
            .filter(trellis => trellis.get('category') === TrellisCategory.NON_MANDATORY_SERIES)
            .map(x => x.get('trellisedBy'))
            .join(', ');

        const title = this.getLegendTitle(tabId, label);
        const dataEntries = plotsDatas
        // get data for all plots
            .flatMap((plot): List<Map<string, any>> => plot.getIn(['data', 'data']));
        const entries = dataEntries
            .filter(data => data.get('high') > 0)
            // filter all the data from maps except color and therapies
            .map(data => data.filter((m, k) => k === 'color' || k === 'name'))
            // remove duplicates
            .toSet()
            .map(entry => {
                return {
                    label: entry.get('name'),
                    color: entry.get('color'),
                    symbol: LegendSymbol.CIRCLE
                };
            }) // add label instead of name and circle symbol
            .sort((a, b) => {
                // if all items in legend looks like "30 mg" then we sort it like numbers
                const aStart = a.label.split(' ')[0];
                const bStart = b.label.split(' ')[0];
                return (isNaN(aStart) || isNaN(bStart))
                    ? a.label.localeCompare(b.label) : aStart - bStart;
            })
            .toArray();

        const leftEntries = dataEntries
            .filter(data => data.get('low') < 0)
            // filter all the data from maps except color and therapies
            .map(data => data.filter((m, k) => k === 'color' || k === 'name'))
            // remove duplicates
            .toSet()
            .map(entry => {
                const entryLabel = entry.get('name') === 'All' ? 'Summary' : entry.get('name');
                return {
                    label: entryLabel || 'All',
                    color: entry.get('color'),
                    symbol: LegendSymbol.CIRCLE
                };
            }) // add label instead of name and circle symbol
            .sort((a, b) => {
                // if all items in legend looks like "30 mg" then we sort it like numbers
                if (a.label !== ColumnRangeUtilsService.SUMMARY && b.label !== ColumnRangeUtilsService.SUMMARY) {
                    return a.label.localeCompare(b.label);
                }
                // Summary should always be on top
                return a.label === ColumnRangeUtilsService.SUMMARY ? -1 : 1;
            })
            .toArray();

        const dateLegendEntries = [{
            label: 'Diagnosis date',
            color: 'black',
            symbol: LegendSymbol.DIAMOND
        },
            {
                label: 'Most recent progression date',
                color: 'red',
                symbol: LegendSymbol.TRIANGLE_RIGHT
            }];

        if (dataEntries.find(data => data.get('noStartDate'))) {
            dateLegendEntries.push({
                label: 'Therapy start date missing',
                color: null,
                symbol: LegendSymbol.LEFT_ARROW
            });
        }

        const uniqLeft = uniqBy(leftEntries, 'color').length === 1
            ? [{label: 'All', color: leftEntries[0].color, symbol: leftEntries[0].symbol}] : leftEntries;

        //return [{title, entries}, {title: 'Therapy description' , entries: uniqLeft}];
        return [{title, entries}, {title: 'Therapy description', entries: uniqLeft}, {
            title: null,
            entries: dateLegendEntries
        }];
    }
}

