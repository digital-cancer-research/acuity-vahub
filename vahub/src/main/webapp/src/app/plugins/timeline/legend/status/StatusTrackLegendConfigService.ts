import {Injectable} from '@angular/core';
import {TrackLegendConfig, TrackLegendType} from '../track/ITrackLegend';
import {TimelineUtils} from '../../chart/TimelineUtils';
import {StatusTrackUtils} from '../../track/status/StatusTrackUtils';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';

@Injectable()
export class StatusTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Status summary',
            items: [
                {
                    type: TrackLegendType.IMAGE,
                    text: 'Informed Consent',
                    height: '15',
                    width: '15',
                    rotate: 180,
                    src: TimelineUtils.INFORMED_CONSENT_SYMBOL
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'First and Last Dose',
                    height: '15',
                    width: '15',
                    rotate: 90,
                    src: TimelineUtils.FIRST_DOSE_SYMBOL
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'Randomisation',
                    height: '15',
                    width: '15',
                    rotate: 90,
                    src: TimelineUtils.RANDOMISATION_SYMBOL
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'Screening and Last Visits (Labs)',
                    height: '15',
                    width: '15',
                    rotate: 90,
                    src: TimelineUtils.SCREENING_VISIT_SYMBOL
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'Ongoing Until Today',
                    height: '15',
                    width: '15',
                    rotate: 90,
                    src: TimelineUtils.ONGOING_SYMBOL
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'Withdrawal/Completion',
                    height: '15',
                    width: '15',
                    rotate: 270,
                    src: TimelineUtils.WITHDRAWAL_COMPLETION_SYMBOL
                },
                {
                    type: TrackLegendType.IMAGE,
                    text: 'Death',
                    height: '15',
                    width: '15',
                    rotate: 270,
                    src: TimelineUtils.DEATH_SYMBOL
                },
                {
                    type: TrackLegendType.GAP,
                    text: ''
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: 'Phase: On study',
                    height: '15',
                    width: '15',
                    color: StatusTrackUtils.ON_STUDY_DRUG
                }
            ]
        };
    }
}
