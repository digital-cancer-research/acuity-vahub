import {ITrackDataPoint} from '../../store/ITimeline';
import {IChartEventService} from '../../chart/IChartEventService';
import {PatientDataTrackUtils} from './PatientDataTrackUtils';
import {AbstractTrackModel} from '../AbstractTrackModel';

export abstract class AbstractPatientDataTrackModel extends AbstractTrackModel {

    constructor(protected chartPlotEventService: IChartEventService) {
        super(chartPlotEventService);
    }

    getTrackName(): string {
        return PatientDataTrackUtils.PATIENTDATA_TRACK_NAME;
    }

}
