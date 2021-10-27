import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {fromJS, List} from 'immutable';
import {IPlot, ISelectionDetail, PlotType} from '../../common/trellising/store';
import {getServerPath} from '../../common/utils/Utils';
import {LabsFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import {LabsHttpService} from './LabsHttpService';
import {Observable} from 'rxjs/Observable';
import {XAxisOptions} from '../../common/trellising/store/actions/TrellisingActionCreator';
import Dataset = Request.Dataset;
import ChartGroupByOptionsFiltered = Request.ChartGroupByOptionsFiltered;
import TrellisOptions = Request.TrellisOptions;
import LabGroupByOptions = InMemory.LabGroupByOptions;
import Lab = Request.Lab;
import TrellisedShiftPlot = InMemory.TrellisedShiftPlot;
import LabSelectionRequest = Request.LabSelectionRequest;
import ChartSelection = Request.ChartSelection;
import ChartSelectionItemRange = Request.ChartSelectionItemRange;
import LabStatsRequest = Request.LabStatsRequest;

@Injectable()
export class LabsShiftPlotHttpService extends LabsHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected labsFiltersModel: LabsFiltersModel) {
        super(http, populationFiltersModel, labsFiltersModel);
    }

    getTrellisOptions(currentDatasets: Dataset[],
                      yAxisOption: any): Observable<TrellisOptions<LabGroupByOptions>[]> {

        const path = getServerPath('labs', 'trellising');

        const postData: any = {
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            labsFilters: this.labsFiltersModel.transformFiltersToServer(),
            datasets: currentDatasets,
            yAxisOption: yAxisOption.get('groupByOption')
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as TrellisOptions<LabGroupByOptions>[]);
    }


    getXAxisOptions(datasets: Dataset[]): Observable<any> {
        return Observable.of(<XAxisOptions>{
            drugs: [],
            hasRandomization: false,
            options: [
                <any>{
                    groupByOption: 'BASELINE'
                }
            ]
        });
    }

    getPlotData(datasets: Dataset[],
                countType,
                settings: any): Observable<List<IPlot>> {
        const postData: LabStatsRequest = {
            datasets,
            settings: {
                settings: {
                    options: {},
                    trellisOptions: []
                },
                filterByTrellisOptions: settings.filterByTrellisOptions
            },
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            labsFilters: this.labsFiltersModel.transformFiltersToServer(),
            statType: null
        };

        return this.http.post(getServerPath('labs', 'shift-plot'), JSON.stringify(postData))
            .map((data: any) => {
                return fromJS(
                    data.map((value: TrellisedShiftPlot<Lab, LabGroupByOptions>) => {
                        return {
                            plotType: PlotType.ERRORPLOT,
                            trellising: value.trellisedBy,
                            data: value.data
                        };
                    })
                );
            });
    }

    getSelection(datasets: Dataset[],
                 selectionItems: ChartSelectionItemRange<Lab, LabGroupByOptions, number>[],
                 settings: ChartGroupByOptionsFiltered<string, string>): Observable<ISelectionDetail> {

        const path = getServerPath('labs', 'shift-selection');

        const postData: LabSelectionRequest = {
            datasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            labsFilters: this.labsFiltersModel.transformFiltersToServer(),
            selection:  {
                selectionItems: selectionItems,
                settings: null
            }
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as ISelectionDetail);
    }
}
