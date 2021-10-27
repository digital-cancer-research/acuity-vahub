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
