import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

import {getServerPath} from '../../../../common/utils/Utils';
import {PopulationFiltersModel, DoseFiltersModel} from '../../../../filters/dataTypes/module';
import {TrackData, TrackRequest} from '../IDataService';
import {BaseTrackDataService} from '../BaseTrackDataService';
import {SessionEventService} from '../../../../session/event/SessionEventService';
import {DoseTrackDataTransformer} from './DoseTrackDataTransformer';

@Injectable()
export class DoseTrackDataService extends BaseTrackDataService {

    constructor(protected http: HttpClient,
                private doseTrackDataTransformer: DoseTrackDataTransformer,
                private sessionEventService: SessionEventService,
                private populationFiltersModel: PopulationFiltersModel,
                private doseFiltersModel: DoseFiltersModel) {
        super(http);
    }

    fetchData(request: TrackRequest): Observable<TrackData[]> {
        let result: Observable<TrackData[]>;
        switch (request.expansionLevel) {
            case 3:
                result = this.fetchDataForLevel2(request);
                break;
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
        const path = getServerPath('timeline/dosing', 'dose-summaries');
        const postData: any = this.getRequestPayload(request);
        return this.fetchTimelineTrackData(request, path, postData)
            .map((response) => {
                return this.doseTrackDataTransformer.transformDoseSummaryTrackData(response);
            });
    }

    fetchDataForLevel2(request: TrackRequest): Observable<TrackData[]> {
        const path = getServerPath('timeline/dosing', 'dose-summaries-by-drug');
        const postData: any = this.getRequestPayload(request);
        return this.fetchTimelineTrackData(request, path, postData)
            .map((response) => {
                return this.doseTrackDataTransformer.transformDoseDetailTrackData(response);
            });
    }

    private getRequestPayload(request: TrackRequest): any {
        const populationFilters = this.populationFiltersModel.transformFiltersToServer();
        populationFilters.subjectId = {values: request.trackSubjects};
        return {
            populationFilters: populationFilters,
            doseFilters: this.doseFiltersModel.transformFiltersToServer(),
            dayZero: request.dayZero,
            datasets: this.sessionEventService.currentSelectedDatasets,
            maxDoseType: 'PER_SUBJECT'
        };
    }
}
