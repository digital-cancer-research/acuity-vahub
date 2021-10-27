import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

import {getServerPath} from '../../../../common/utils/Utils';
import {PopulationFiltersModel, AesFiltersModel} from '../../../../filters/dataTypes/module';
import {TrackData, TrackRequest} from '../IDataService';
import {BaseTrackDataService} from '../BaseTrackDataService';
import {SessionEventService} from '../../../../session/event/SessionEventService';
import {AesTrackDataTransformer} from './AesTrackDataTransformer';

@Injectable()
export class AesTrackDataService extends BaseTrackDataService {

    constructor(protected http: HttpClient,
                private aeTrackDataTransformer: AesTrackDataTransformer,
                private sessionEventService: SessionEventService,
                private populationFiltersModel: PopulationFiltersModel,
                private aesFiltersModel: AesFiltersModel) {
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
        const path = getServerPath('timeline/aes', 'aessummaries');
        const postData: any = this.getRequestPayload(request);
        return this.fetchTimelineTrackData(request, path, postData)
            .map((response) => {
                return this.aeTrackDataTransformer.transformAesSummaryTrackData(response);
            });
    }

    fetchDataForLevel2(request: TrackRequest): Observable<TrackData[]> {
        const path = getServerPath('timeline/aes', 'aesdetails');
        const postData: any = this.getRequestPayload(request);
        return this.fetchTimelineTrackData(request, path, postData)
            .map((response) => {
                return this.aeTrackDataTransformer.transformAesDetailTrackData(response);
            });
    }

    private getRequestPayload(request: TrackRequest): any {
        const populationFilters = this.populationFiltersModel.transformFiltersToServer();
        populationFilters.subjectId = {values: request.trackSubjects};
        return {
            populationFilters: populationFilters,
            aesFilters: this.aesFiltersModel.transformFiltersToServer(),
            dayZero: request.dayZero,
            datasets: this.sessionEventService.currentSelectedDatasets
        };
    }
}
