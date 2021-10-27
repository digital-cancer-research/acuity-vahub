import {Injectable} from '@angular/core';
import {TrackLegendConfig} from '../track/ITrackLegend';
import {TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {EcgTrackUtils} from '../../track/ecg/EcgTrackUtils';

@Injectable()
export class EcgSummaryTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'ECG summary',
            items: [
                {
                    type: TrackLegendType.GRADIENT,
                    text: '% change from baseline',
                    colorStart: EcgTrackUtils.MINIMAL_COLOUR,
                    color: EcgTrackUtils.BASELINE_COLOUR,
                    colorEnd: EcgTrackUtils.MAXIMAL_COLOUR
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'High QTcF, significant or abnormal evaluation',
                    height: '15',
                    width: '15',
                    color: EcgTrackUtils.WARNING_COLOUR,
                    warning: true
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'ECG Measurement',
                    height: '15',
                    width: '15',
                    color: EcgTrackUtils.BASELINE_COLOUR
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'High QTcF, significant or abnormal evaluation at baseline',
                    height: '15',
                    width: '15',
                    color: EcgTrackUtils.WARNING_COLOUR,
                    warning: true
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'ECG Measurement at baseline',
                    height: '15',
                    width: '15',
                    color: EcgTrackUtils.BASELINE_COLOUR
                }
            ]
        };
    }
}
