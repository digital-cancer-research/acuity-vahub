import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

import {getServerPath} from '../../../../common/utils/Utils';
import {PopulationFiltersModel, PatientDataFiltersModel} from '../../../../filters/dataTypes/module';
import {TrackData, TrackRequest} from '../IDataService';
import {SessionEventService} from '../../../../session/event/SessionEventService';
import {PatientDataTrackDataTransformer} from './PatientDataTrackDataTransformer';
import {BaseTrackDataService} from '../BaseTrackDataService';
import {DynamicAxis} from '../../../../common/trellising/store/ITrellising';
import {DayZero} from '../../store/ITimeline';

@Injectable()
export class PatientDataTrackDataService extends BaseTrackDataService {

    constructor(protected http: HttpClient,
                private patientDataTrackDataTransformer: PatientDataTrackDataTransformer,
                private sessionEventService: SessionEventService,
                private populationFiltersModel: PopulationFiltersModel,
                private patientDataFiltersModel: PatientDataFiltersModel) {
        super(http);
    }

    fetchData(request: TrackRequest): Observable<TrackData[]> {
        let result: Observable<TrackData[]>;
        switch (request.expansionLevel) {
            case 2:
                result = this.fetchDataForLevel2(request);
                break;
            case 1:
                result = this.fetchDataForLevel1(request);
                break;
            default:
                break;
        }
        return result.map(trackData => this.extendTrackData(trackData, request));

    }

    fetchDataForLevel1(request: TrackRequest): Observable<TrackData[]> {
        const path = getServerPath('timeline/patientdata', 'patientdatasummaries');
        const postData: any = this.getRequestPayload(request);
        return this.fetchTimelineTrackData(request, path, postData)
            .map((response) => {
                return this.patientDataTrackDataTransformer.transformPatientDataSummaryTrackData(response);
            });
    }

    fetchDataForLevel2(request: TrackRequest): Observable<TrackData[]> {
        const path = getServerPath('timeline/patientdata', 'patientdatadetails');
        const postData: any = this.getRequestPayload(request);
        return this.fetchTimelineTrackData(request, path, postData)
            .map((response) => {
                return this.patientDataTrackDataTransformer.transformPatientDataDetailsTrackData(response);
            });
    }

    private getRequestPayload(request: TrackRequest): any {
        const dayZeroAsMap: DynamicAxis = <DynamicAxis> request.dayZero;
        const value = dayZeroAsMap.get('value');
        let TIMESTAMP_TYPE;
        switch (value) {
            case (DayZero.DAYS_SINCE_RANDOMISATION):
                TIMESTAMP_TYPE = 'DAYS_HOURS_SINCE_RANDOMISATION';
                break;
            case (DayZero.DAYS_SINCE_FIRST_TREATMENT):
                TIMESTAMP_TYPE = 'DAYS_HOURS_SINCE_FIRST_DOSE_OF_DRUG';
                break;
            default:
                TIMESTAMP_TYPE = 'DAYS_HOURS_SINCE_FIRST_DOSE';
        }
        return {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            patientDataFilters: this.patientDataFiltersModel.transformFiltersToServer(),
            options: {
                X_AXIS: {
                    groupByOption: 'MEASUREMENT_DATE',
                    params: {
                        TIMESTAMP_TYPE,
                        DRUG_NAME: dayZeroAsMap.get('stringarg')
                    },
                }
            },
            subjectIds: request.subjectIds,
            datasets: this.sessionEventService.currentSelectedDatasets
        };
    }
}
