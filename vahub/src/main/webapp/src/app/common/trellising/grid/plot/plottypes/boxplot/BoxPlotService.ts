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
import {chain, isEmpty} from 'lodash';

import {TextUtils} from '../../../../../utils/TextUtils';
import OutputBoxplotEntry = Request.OutputBoxplotEntry;
import OutputBoxPlotOutlier = Request.OutputBoxPlotOutlier;

export interface OutlierData {
    x: number;
    y: number;
    subjectId: string;
}
export interface BoxPlotSeries {
    name: string;
    color: string;
    type: string;
    eventCounts: number[];
    tooltip: {};
    data: OutputBoxplotEntry[];
}
export interface BoxPlotData {
    boxes: OutputBoxplotEntry[];
    outliers: OutlierData[];
    categories: string [];
    eventCounts: number[];
}

@Injectable()
export class BoxPlotService {
    splitServerData(stats: OutputBoxplotEntry[], isCategorical: boolean): BoxPlotData {
        let categories = this.getCategories(stats);
        const eventCounts = stats.map((stat) => stat.eventCount);
        let outliers: OutlierData[];
        if (isCategorical) {
            outliers = this.getCategoricalOutliers(categories, stats);
        } else {
            outliers = this.getNonCategoricalOutliers(stats);
        }
        if (isCategorical) {
            categories = categories.map(TextUtils.stringOrEmpty);
        }
        return {
            categories: isCategorical ? categories.map(String) : undefined,
            boxes: stats.filter((el, i) => eventCounts[i]),
            outliers: outliers,
            eventCounts
        };
    }

    private getCategories(stats: OutputBoxplotEntry[]): string[] {
        return chain(stats).sortBy(x => x.xrank).map(x => x.x).value();
    }

    private getCategoricalOutliers(categories: string[], stats: OutputBoxplotEntry[]): OutlierData[] {
        return chain(categories)
            .map((category: string, index: number) => {
                return chain(stats)
                    .filter({'x': category})
                    .map('outliers')
                    .flatten()
                    .map((outlier: OutputBoxPlotOutlier) => {
                        return {x: index, y: outlier.outlierValue, subjectId: outlier.subjectId};
                    })
                    .value();
            })
            .map((outliers: OutlierData[]) => {
                if (isEmpty(outliers)) {
                    return null;
                } else {
                    return outliers;
                }
            })
            .flatten()
            .value()
            .filter(d => d);
    }

    private getNonCategoricalOutliers(stats: OutputBoxplotEntry[]): OutlierData[] {
        return chain(stats)
            .map('outliers')
            .flatten()
            .map((outlier: OutputBoxPlotOutlier) => {
                return {x: Number(outlier.x), y: outlier.outlierValue, subjectId: outlier.subjectId};
            })
            .value();
    }
}
