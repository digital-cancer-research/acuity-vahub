import {BoxPlotConfigService} from './BoxPlotConfigService';
import {TabId} from '../../../../store';
import {inject, TestBed} from '@angular/core/testing';
import {MockDatasetViews} from '../../../../../MockClasses';
import {DatasetViews} from '../../../../../../security/DatasetViews';
import {ExportChartService} from '../../../../../../data/export-chart.service';
import {ConfigurationService} from '../../../../../../configuration/ConfigurationService';

class MockConfigurationService {
}

describe('GIVEN BoxPlotConfigService class', () => {
    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [
                { provide: ExportChartService, useValue: {}},
                { provide: DatasetViews, useClass: MockDatasetViews },
                { provide: ConfigurationService, useClass: MockConfigurationService },
                BoxPlotConfigService
            ]
        });
    });
    describe('WHEN Lab boxplot and y axis is normalised to the reference range', () => {
        it('THEN two horizontal lines added', inject([BoxPlotConfigService], (configService: BoxPlotConfigService) => {
            const config = configService.createPlotConfig('', '', '', '', 'Ref range norm. value', 0, TabId.LAB_BOXPLOT);
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
