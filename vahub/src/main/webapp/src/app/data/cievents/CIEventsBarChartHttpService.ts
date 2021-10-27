import {Injectable} from '@angular/core';
import {Observable} from 'rxjs/Observable';
import {getServerPath} from '../../common/utils/Utils';
import {CIEventsFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {fromJS, List} from 'immutable';
import {DynamicAxis, IPlot, PlotType} from '../../common/trellising/store';
import {CIEventsHttpService} from './CIEventsHttpService';
import {HttpClient} from '@angular/common/http';
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import Dataset = Request.Dataset;
import CIEventGroupByOptions = InMemory.CIEventGroupByOptions;
import CIEvent = Request.CIEvent;
import TrellisedBarChart = Request.TrellisedBarChart;

@Injectable()
export class CIEventsBarChartHttpService extends CIEventsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected cieventsFiltersModel: CIEventsFiltersModel) {
        super(http, populationFiltersModel, cieventsFiltersModel);
    }

    getXAxisOptions(currentDatasets: Dataset[]): Observable<DynamicAxis[]> {
        const path = getServerPath('cievents', 'countsbarchart-xaxis');
        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cieventsFilters: this.cieventsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets
        };
        return this.http.post(path, JSON.stringify(postData)).map(res => res as DynamicAxis[]);
    }

    getPlotData(currentDatasets: Dataset[],
                countType: any,
                settings: ChartGroupByOptionsFiltered<string, string>) {
        const path = getServerPath('cievents', 'countsbarchart');

        let xAxis = settings.settings.options['X_AXIS'];


        /**
         * We add "NONE" option for Cerebrovascular on the frontend,
         * @see {@link generateAvailableOptions}
         * although BE can't deserialize it, so we should not send it
         * TODO some more regular solution is required here
         */
        if (xAxis) {
            if ('NONE' === xAxis.groupByOption) {
                // making it undefined will exclude it from request
                xAxis = undefined;
            }
        }

        const barChartSettings = {
            settings: {
                options: {
                    'X_AXIS': xAxis,
                    'COLOR_BY': settings.settings.options['COLOR_BY']
                },
                trellisOptions: []
            },
            filterByTrellisOptions: []
        };

        const postData = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            cieventsFilters: this.cieventsFiltersModel.transformFiltersToServer(),
            countType: countType.groupByOption,
            settings: barChartSettings
        };

        return this.http.post(path, JSON.stringify(postData))
            .map((data: TrellisedBarChart<CIEvent, CIEventGroupByOptions>[]) => {
                return <List<IPlot>>fromJS(data.map((value: TrellisedBarChart<CIEvent, CIEventGroupByOptions>) => {
                    return {
                        plotType: PlotType.GROUPED_BARCHART,
                        trellising: value.trellisedBy,
                        data: value.data
                    };
                }));
            });
    }
}
