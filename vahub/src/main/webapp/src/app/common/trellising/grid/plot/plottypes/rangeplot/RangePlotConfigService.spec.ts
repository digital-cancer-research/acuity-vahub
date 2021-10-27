import {RangePlotConfigService} from './RangePlotConfigService';
import {TabId} from '../../../../store';
import {inject, TestBed} from '@angular/core/testing';
import {ExportChartService} from '../../../../../../data/export-chart.service';
import {ConfigurationService} from '../../../../../../configuration/ConfigurationService';

class MockConfigurationService {
}

describe('GIVEN RangePlotConfigService class', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [],
            providers: [
                { provide: ExportChartService, useValue: {}},
                { provide: ConfigurationService, useClass: MockConfigurationService },
                RangePlotConfigService,
            ]
        });
    });
    describe('WHEN Lab line plot and y axis is normalised to the reference range', () => {
        it('THEN two horizontal lines added',   inject([RangePlotConfigService], (configService: RangePlotConfigService) => {
            const config = configService.createPlotConfig('', '', '', '', 'Ref range norm. value', 0, TabId.LAB_LINEPLOT);
            expect(config.plotLines).toEqual([{
                value: 0,
                axis: 'y',
                color: 'red',
                width: 1
            }, {
                value: 1,
                axis: 'y',
                color: 'red',
                width: 1
            }]);
        }));
    });
});
