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

import {inject, TestBed} from '@angular/core/testing';
import {PopulationSummaryTableService} from './PopulationSummaryTableService';
import ColoredOutputBarChartData = InMemory.ColoredOutputBarChartData;

describe('GIVEN TitleService class', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({providers: [PopulationSummaryTableService]});
    });

    describe('WHEN mapping chart data to table', () => {
        let data: ColoredOutputBarChartData[];
        let yAxisOption: string;
        beforeEach(() => {
            data = [{
                name: 'DummyData', color: '#CC6677', categories: ['N', 'Y'],
                series: [
                    {category: 'N', rank: 1, value: 98, totalSubjects: 98},
                    {category: 'Y', rank: 2, value: 99, totalSubjects: 99}
                ]
            }];
            yAxisOption = 'COUNT_OF_SUBJECTS';
        });

        it('THEN maps data into column headings', inject([PopulationSummaryTableService], (service: PopulationSummaryTableService) => {
            service.processData(data, yAxisOption);
            expect(service.dashboard.tableHeaders.map(x => x.columnName)).toEqual(['N', 'Y', 'Total']);
            expect(service.dashboard.tableHeaders.map(x => x.total)).toEqual([98, 99, 197]);
        }));

        it('THEN maps data into row names', inject([PopulationSummaryTableService], (service: PopulationSummaryTableService) => {
            service.processData(data, yAxisOption);
            expect(service.dashboard.table.map(x => x.rowName)).toEqual(['DummyData', 'Total']);
        }));
        it('THEN maps data into row counts', inject([PopulationSummaryTableService], (service: PopulationSummaryTableService) => {
            service.processData(data, yAxisOption);
            expect(service.dashboard.table[0].rowValue.map(x => x.countValue)).toEqual([98, 99, 197]);
        }));
    });
});
