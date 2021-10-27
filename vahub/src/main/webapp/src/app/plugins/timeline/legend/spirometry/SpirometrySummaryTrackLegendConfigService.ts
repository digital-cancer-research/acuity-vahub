import {Injectable} from '@angular/core';
import {TrackLegendConfig, TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {SpirometryTrackUtils} from '../../track/spirometry/SpirometryTrackUtils';

@Injectable()
export class SpirometrySummaryTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Spirometry summary',
            items: [
                {
                    type: TrackLegendType.GRADIENT,
                    text: '% change from baseline',
                    colorStart: SpirometryTrackUtils.MINIMAL_COLOUR,
                    color: SpirometryTrackUtils.BASELINE_COLOUR,
                    colorEnd: SpirometryTrackUtils.MAXIMAL_COLOUR
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'Spirometry Measurement',
                    height: '10',
                    width: '10',
                    color: SpirometryTrackUtils.BASELINE_COLOUR
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'Spirometry Measurement at baseline',
                    height: '10',
                    width: '10',
                    color: SpirometryTrackUtils.BASELINE_COLOUR
                }
            ]
        };
    }
}
