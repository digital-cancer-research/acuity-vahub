import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

import {DynamicAxis} from '../../common/trellising/store';
import {downloadData, getServerPath} from '../../common/utils/Utils';
import {BaseChartsHttpService} from '../BaseChartsHttpService';
import {LabsFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import Dataset = Request.Dataset;
import DetailsOnDemandRequest = Request.DetailsOnDemandRequest;
import SortAttrs = Request.SortAttrs;

@Injectable()
export class LabsHttpService extends BaseChartsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected labsFiltersModel: LabsFiltersModel) {
        super();
    }

    getSubjectsInFilters(currentDatasets: Dataset[]): Observable<string[]> {
        const path = getServerPath('labs', 'filters-subjects');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            labsFilters: this.labsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as string[]);
    }

    getXAxisOptions(currentDatasets: Dataset[]): Observable<DynamicAxis[]> {
        const path = getServerPath('labs', 'boxplot-xaxis');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            labsFilters: this.labsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as DynamicAxis[]);
    }

    getDetailsOnDemand(currentDatasets: Dataset[],
                       eventIds: string[],
                       startRow: number,
                       endRow: number,
                       sortAttrs: SortAttrs[]): Observable<Map<string, string>[]> {

        const requestBody: DetailsOnDemandRequest = {
            eventIds: eventIds,
            sortAttrs: sortAttrs,
            datasets: currentDatasets,
            start: startRow,
            end: endRow
        };

        const path = getServerPath('labs', 'details-on-demand');

        return this.http.post(path, JSON.stringify(requestBody)).map(res => res as Map<string, string>[]);
    }

    downloadAllDetailsOnDemandData(currentDatasets: Dataset[]): void {
        const requestBody = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            labsFilters: this.labsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        const path = getServerPath('labs', 'download-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }

    downloadDetailsOnDemandData(currentDatasets: Dataset[], eventIds: string[]): void {
        const requestBody = {
            eventIds: eventIds,
            datasets: currentDatasets
        };

        const path = getServerPath('labs', 'download-selected-details-on-demand');

        this.http.post(path, JSON.stringify(requestBody), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('details_on_demand.csv', response);
            });
    }
}
