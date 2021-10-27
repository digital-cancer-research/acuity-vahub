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
