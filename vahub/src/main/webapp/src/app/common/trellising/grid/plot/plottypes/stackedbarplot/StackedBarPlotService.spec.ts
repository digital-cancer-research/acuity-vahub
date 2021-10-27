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

import {StackedBarPlotService} from './StackedBarPlotService';
import ColoredOutputBarChartData = InMemory.ColoredOutputBarChartData;

describe('GIVEN StackedBarPlotService class', () => {

    let stackedBarPlotService: StackedBarPlotService;

    beforeEach(() => {
        stackedBarPlotService = new StackedBarPlotService();
    });

    describe('WHEN there are x values not declared as categories', () => {

        const mockDataFromServer: ColoredOutputBarChartData[] = [{
            name: 'CKD Stage 1',
            color: '#b4da50',
            categories: ['-3'],
            series: [{
                category: '-3',
                rank: 1,
                value: 100,
                totalSubjects: null
            }, {
                category: null,
                rank: null,
                value: 100,
                totalSubjects: null
            }]
        }];

        it('THEN the x values are not added to the series', () => {
            const barChartData = stackedBarPlotService.splitServerData(mockDataFromServer);
            expect(barChartData.series.length).toBe(1);
        });
    });
});
