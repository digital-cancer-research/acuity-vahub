import {Injectable} from '@angular/core';
import {TrackLegendConfig, TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {DoseTrackUtils} from '../../track/dose/DoseTrackUtils';
import {TimelineUtils} from '../../chart/TimelineUtils';

@Injectable()
export class DoseSummaryTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Dose summary',
            items: [
                {
                    type: TrackLegendType.ACUITY,
                    text: 'No dose reduction',
                    height: '15',
                    width: '15',
                    color: DoseTrackUtils.assignColour(0)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'Inactive dosing (100% reduction)',
                    height: '15',
                    width: '15',
                    color: DoseTrackUtils.assignColour(-100)
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'Ongoing Until Today',
                    height: '15',
                    width: '15',
                    rotate: 90,
                    src: TimelineUtils.ONGOING_SYMBOL
                }
            ]
        };
    }
}
