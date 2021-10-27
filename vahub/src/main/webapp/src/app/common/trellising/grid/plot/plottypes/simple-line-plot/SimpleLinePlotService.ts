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
import {flatMap, uniq} from 'lodash';
import {AggregationType, ErrorBarsType, IZoom, PlotSettings, TabId} from '../../../../store';
import OutputLineChartData = InMemory.OutputLineChartData;

@Injectable()
export class SimpleLinePlotService {

    static getStringCategories(rawData: any, isCategorical: boolean): string[] {
        return isCategorical ? uniq(flatMap(rawData, 'series').map(serie => serie.x))
            .sort((categoryA, categoryB) => {
                const a = categoryA.toString().split(' ');
                const b = categoryB.toString().split(' ');
                return a[0] !== b[0] ? a[0].localeCompare(b[0]) : a[1] - b[1];
            }) : null;
    }

    static getShiftedYAxis(zoomY: IZoom, canHaveLogScale: boolean, tabId: TabId, yAxisLabel: string): { min: number, max: number } {
        const delta = !(tabId === TabId.CTDNA_PLOT && zoomY.zoomMin === zoomY.zoomMax
        && SimpleLinePlotService.hasFraction(yAxisLabel))
            ? (zoomY.zoomMax - zoomY.zoomMin) / 100 : (zoomY.absMax - zoomY.absMin) / 200;
        const min = zoomY.zoomMin - delta;
        return {
            min: min <= 0 && (canHaveLogScale || tabId === TabId.CTDNA_PLOT) ? zoomY.zoomMin : min,
            max: zoomY.zoomMax + delta
        };
    }

    static shouldErrorBarsBeShown(plotSettings: PlotSettings): boolean {
        return plotSettings
            && plotSettings.get('trellisedBy')
            && plotSettings.get('trellisedBy') !== AggregationType.SUBJECT_CYCLE
            && plotSettings.getIn(['errorBars', ErrorBarsType.STANDARD_DEVIATION]);
    }

    static hasFraction(axisLabel: string): boolean {
        return axisLabel.includes('fraction');
    }

    splitServerDataToZones(rawData: OutputLineChartData[], allCategories: string[]): any {
        return rawData.map(datum => {
            const zones = this.getZones(datum.series, allCategories);
            return {
                name: datum.seriesBy,
                series: datum.series.map(serie => {
                    let x;
                    let category;
                    if (!allCategories) {
                        x = serie.x.start;
                        category = x;
                    } else {
                        x = allCategories.indexOf(serie.x);
                        category = serie.x;
                    }
                    return {
                        name: serie.name,
                        color: serie.color,
                        y: serie.y,
                        x,
                        category
                    };
                }),
                zones
            };
        });
    }

    private getZones(series: any[], allCategories: string[]) {
        const zones = [];
        series.forEach((serie, i, allSeries) => {
            if (i > 0) {
                // when lesion date is shown on x axis we have x like {start, end}
                // when Assesment week, x is a string value
                const x = !allCategories ? serie.x.start : allCategories.indexOf(serie.x);
                if (serie.color !== allSeries[i - 1].color) {
                    zones.push({
                        value: x,
                        color: allSeries[i - 1].color
                    });
                }

                if (i === allSeries.length - 1) {
                    zones.push({
                        value: x + 1,
                        color: serie.color
                    });
                }
            }
        });
        return zones;
    }
}
