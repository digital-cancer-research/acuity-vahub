import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {fromJS, List} from 'immutable';
import {Observable} from 'rxjs/Observable';
import {IPlot, ISelectionDetail, PlotType} from '../../common/trellising/store';
import {LungFunctionFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {RespiratoryHttpService} from './RespiratoryHttpService';
import Dataset = Request.Dataset;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import TrellisOptions = Request.TrellisOptions;
import LungFunctionGroupByOptions = InMemory.LungFunctionGroupByOptions;
import LungFunctionTrellisRequest = Request.LungFunctionTrellisRequest;
import LungFunctionValuesRequest = Request.LungFunctionValuesRequest;
import TrellisedBoxPlot = Request.TrellisedBoxPlot;
import LungFunction = Request.LungFunction;

@Injectable()
export class RespiratoryBoxPlotHttpService extends RespiratoryHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected lungFunctionFiltersModel: LungFunctionFiltersModel) {
        super(http, populationFiltersModel, lungFunctionFiltersModel);
    }


    getTrellisOptions(currentDatasets: Dataset[], yAxisOption: any)
        : Observable<TrellisOptions<LungFunctionGroupByOptions>[]> {
        const path = `${this.API}/trellising`;

        const postData: LungFunctionTrellisRequest = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            lungFunctionFilters: this.lungFunctionFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            resultType: yAxisOption.get('groupByOption')
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as TrellisOptions<LungFunctionGroupByOptions>[]);
    }

    getPlotData(datasets: Dataset[],
                countType,
                settings: any): Observable<List<IPlot>> {
        const path = `${this.API}/values`;
        const postData: LungFunctionValuesRequest = {
            datasets,
            settings,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            lungFunctionFilters: this.lungFunctionFiltersModel.transformFiltersToServer()
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedBoxPlot<LungFunction, LungFunctionGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value: TrellisedBoxPlot<LungFunction, LungFunctionGroupByOptions>) => {
                    return {
                        plotType: PlotType.BOXPLOT,
                        trellising: value.trellisedBy,
                        data: value.stats
                    };
                }));
            });
    }

    getSelection(datasets: Dataset[],
                 selectionItems: any,
                 settings: ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {
        const path = `${this.API}/selection`;
        const postData: any = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            lungFunctionFilters: this.lungFunctionFiltersModel.transformFiltersToServer(),
            selection: {
                selectionItems,
                settings: settings.settings
            }
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as ISelectionDetail);
    }
}
