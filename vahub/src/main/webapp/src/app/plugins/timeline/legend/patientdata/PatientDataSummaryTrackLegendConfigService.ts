import { Injectable } from '@angular/core';
import {TrackLegendConfig} from '../track/ITrackLegend';
import {TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {PatientDataTrackUtils} from '../../track/patientdata/PatientDataTrackUtils';

@Injectable()
export class PatientDataSummaryTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Patient data',
            items: [
                {
                    type: TrackLegendType.ACUITY,
                    text: '1 = # Patient reported event',
                    height: '15',
                    width: '15',
                    color: PatientDataTrackUtils.assignColour(1, PatientDataTrackUtils.TIMELINE_LEVEL_TRACK_EXPANSION_LEVEL)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: '2 <= # Patient reported events <= 5',
                    height: '15',
                    width: '15',
                    color: PatientDataTrackUtils.assignColour(3, PatientDataTrackUtils.TIMELINE_LEVEL_TRACK_EXPANSION_LEVEL)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: '5 < # Patient reported events <= 10',
                    height: '15',
                    width: '15',
                    color: PatientDataTrackUtils.assignColour(6, PatientDataTrackUtils.TIMELINE_LEVEL_TRACK_EXPANSION_LEVEL)
                },
                {
                    type: TrackLegendType.ACUITY,
                    text: '10 < # Patient reported events',
                    height: '15',
                    width: '15',
                    color: PatientDataTrackUtils.assignColour(11, PatientDataTrackUtils.TIMELINE_LEVEL_TRACK_EXPANSION_LEVEL)
                }
            ]
        };
    }
}
