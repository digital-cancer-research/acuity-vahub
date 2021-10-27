import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {fromJS, List} from 'immutable';

import {getServerPath} from '../../common/utils/Utils';
import {CvotFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {DynamicAxis, IPlot, PlotType} from '../../common/trellising/store';
import {CvotHttpService} from './CvotHttpService';
import Dataset = Request.Dataset;
import TrellisedOvertime = InMemory.TrellisedOvertime;
import CvotEndpointGroupByOptions = InMemory.CvotEndpointGroupByOptions;
import CvotEndpoint = Request.CvotEndpoint;
import CvotEndpointRequest = Request.CvotEndpointRequest;
import TrellisOptions = Request.TrellisOptions;

@Injectable()
export class CvotBarLineChartHttpService extends CvotHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected cvotFiltersModel: CvotFiltersModel) {
        super(http, populationFiltersModel, cvotFiltersModel);
    }

    getColorByOptions(currentDatasets: Dataset[], yAxisOption: string)
        : Observable<TrellisOptions<CvotEndpointGroupByOptions>[]> {
        const path = getServerPath('cvotendpoint', 'colorby-options');

        const postData: CvotEndpointRequest = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cvotEndpointFilters: this.cvotFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData))
            .map(res => res as Request.TrellisOptions<CvotEndpointGroupByOptions>[]);
    }

    getXAxisOptions(currentDatasets: Request.AcuityObjectIdentityWithPermission[]): Observable<DynamicAxis[]> {
        const path = getServerPath('cvotendpoint', 'overtime-xaxis');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cvotEndpointFilters: this.cvotFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as DynamicAxis[]);
    }

    getPlotData(currentDatasets: Dataset[], countType: any, settings: any) {
        const path = getServerPath('cvotendpoint', 'overtime');

        const barChartSettings = {
            settings: {
                options: {
                    'X_AXIS': settings.settings.options['X_AXIS'],
                    'COLOR_BY': settings.settings.options['COLOR_BY']
                },
                trellisOptions: []
            },
            filterByTrellisOptions: []
        };

        const postData: any = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cvotEndpointFilters: this.cvotFiltersModel.transformFiltersToServer(),
            countType: countType.groupByOption,
            settings: barChartSettings
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedOvertime<CvotEndpoint, CvotEndpointGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value: TrellisedOvertime<CvotEndpoint, CvotEndpointGroupByOptions>) => {
                    return {
                        plotType: PlotType.BARLINECHART,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }
}
