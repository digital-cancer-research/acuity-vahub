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

import { Injectable } from '@angular/core';
import * as  _ from 'lodash';
import OutputScatterPlotEntry = InMemory.OutputScatterPlotEntry;

export interface ScatterData {
    color: string;
    data: Array<number>;
}

@Injectable()
export class ScatterPlotService {
    static getCategoricalBin(x: number): { min: number, max: number } {
        const nearest = Math.round(x);
        return { min: nearest - 0.49, max: nearest + 0.49 };
    }

    public reformatServerData(data: OutputScatterPlotEntry[]): ScatterData[] {
        const reducedData = data.map(x => ({ color: x.color, data: [x.x, x.y] }));
        const groupedData = _.groupBy(reducedData, x => x.color);
        return _.map(groupedData, (value, key) => {
            return {
                color: key,
                data: value.reduce((a, b) => { a.push(b.data); return a; }, [])
            };
        });
    }
}
