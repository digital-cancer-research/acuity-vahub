import {Injectable} from '@angular/core';
import {TrackLegendConfig, TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {SpirometryTrackUtils} from '../../track/spirometry/SpirometryTrackUtils';
import {SpirometryYAxisValue} from '../../store/ITimeline';

@Injectable()
export class SpirometryDetailTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Spirometry detail',
            items: [
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'Spirometry Measurement',
                    height: '10',
                    width: '10',
                    color: SpirometryTrackUtils.SPIROMETRY_MEASUREMENT_COLOUR
                },
                {
                    type: TrackLegendType.DASH_LINE,
                    text: 'Spirometry Baseline',
                    height: '10',
                    width: '10',
                    color: SpirometryTrackUtils.BASELINE_COLOUR
                }
            ]
        };
    }
}
