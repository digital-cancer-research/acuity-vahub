import {Injectable} from '@angular/core';
import {TrackLegendConfig, TrackLegendType} from '../track/ITrackLegend';
import {ITrackLegendConfigService} from '../ITrackLegendConfigService';
import {LabsTrackUtils} from '../../track/labs/LabsTrackUtils';
import {LabsYAxisValue} from '../../store/ITimeline';

@Injectable()
export class LabsDetailTrackLegendConfigService implements ITrackLegendConfigService {

    getTrackLegendConfig(): TrackLegendConfig {
        return <TrackLegendConfig> {
            title: 'Labs detail',
            items: [
                {
                    type: TrackLegendType.CIRCLE,
                    text: LabsYAxisValue.REF_RANGE_NORM,
                    height: '15',
                    width: '15',
                    color: LabsTrackUtils.CHANGE_FROM_BASE_LAB_COLOUR,
                    yAxisOption: LabsYAxisValue.REF_RANGE_NORM
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: LabsYAxisValue.TIMES_UPPER_REF,
                    height: '15',
                    width: '15',
                    color: LabsTrackUtils.CHANGE_FROM_BASE_LAB_COLOUR,
                    yAxisOption: LabsYAxisValue.TIMES_UPPER_REF
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: LabsYAxisValue.TIMES_LOWER_REF,
                    height: '15',
                    width: '15',
                    color: LabsTrackUtils.CHANGE_FROM_BASE_LAB_COLOUR,
                    yAxisOption: LabsYAxisValue.TIMES_LOWER_REF
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'Lab above or below limit of normal',
                    height: '15',
                    width: '15',
                    color: LabsTrackUtils.OUT_OF_REFERENCE_RANGE_COLOUR,
                    yAxisOption: LabsYAxisValue.RAW
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'Lab above or below limit of normal and outside severity threshold',
                    height: '15',
                    width: '15',
                    color: LabsTrackUtils.OUT_OF_SEVERITY_THRESHOLD_COLOUR,
                    yAxisOption: LabsYAxisValue.RAW
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'Lab within limit of normal',
                    height: '15',
                    width: '15',
                    color: LabsTrackUtils.NORMAL_LAB_COLOUR,
                    yAxisOption: LabsYAxisValue.RAW
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'Lab change from baseline',
                    height: '15',
                    width: '15',
                    color: LabsTrackUtils.CHANGE_FROM_BASE_LAB_COLOUR,
                    yAxisOption: LabsYAxisValue.CHANGE_FROM_BASELINE
                },
                {
                    type: TrackLegendType.CIRCLE,
                    text: 'Lab % change from baseline',
                    height: '15',
                    width: '15',
                    color: LabsTrackUtils.CHANGE_FROM_BASE_LAB_COLOUR,
                    yAxisOption: LabsYAxisValue.PERCENT_CHANGE_FROM_BASELINE
                },
                {
                    type: TrackLegendType.STAR,
                    text: 'Lab baseline measurement',
                    height: '15',
                    width: '15',
                    style: {
                        stroke: LabsTrackUtils.NORMAL_LAB_COLOUR,
                        strokeWidth: 1,
                        fill: '#fff'
                    }
                }
            ]
        };
    }
}
