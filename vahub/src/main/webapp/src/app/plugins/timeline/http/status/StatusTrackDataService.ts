import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {PopulationFiltersModel} from '../../../../filters/dataTypes/module';
import {getServerPath} from '../../../../common/utils/Utils';
import {TrackData, TrackRequest} from '../IDataService';
import {SessionEventService} from '../../../../session/event/SessionEventService';
import {StatusTrackDataTransformer} from './StatusTrackDataTransformer';
import {BaseTrackDataService} from '../BaseTrackDataService';

@Injectable()
export class StatusTrackDataService extends BaseTrackDataService {

    constructor(protected http: HttpClient,
                private statusTrackDataTransformer: StatusTrackDataTransformer,
                private sessionEventService: SessionEventService,
                private populationFiltersModel: PopulationFiltersModel) {
        super(http);
    }

    fetchData(request: TrackRequest): Observable<TrackData[]> {
        const path = getServerPath('timeline', 'status', 'summaries');
        const postData: any = this.getRequestPayload(request);
        return this.fetchTimelineTrackData(request, path, postData)
            .map((response) => {
                return this.statusTrackDataTransformer.transformStatusTrackData(response);
            })
            .map(trackData => this.extendTrackData(trackData, request));

    }

    private getRequestPayload(request: TrackRequest): any {
        const populationFilters = this.populationFiltersModel.transformFiltersToServer();
        populationFilters.subjectId = {values: request.trackSubjects};
        return {
            populationFilters,
            dayZero: request.dayZero,
            datasets: this.sessionEventService.currentSelectedDatasets
        };
    }
}
