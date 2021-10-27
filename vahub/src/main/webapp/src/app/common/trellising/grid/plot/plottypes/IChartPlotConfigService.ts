import {IPlotConfigService} from '../IPlotConfigService';
import {TabId} from '../../../store';
import {UserOptions} from '../../../../../../vahub-charts/types/interfaces';

export interface IChartPlotConfigService extends IPlotConfigService {
    createPlotConfig(title: string, xAxisLabel: string, globalXAxisLabel: string, yAxisLabel: string,
                     globalYAxisLabel: string, height: number, tabId: TabId): UserOptions;
}
