import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';
import {downloadData, getServerPath} from '../../common/utils/Utils';
import {AesFiltersModel, PopulationFiltersModel} from '../../filters/dataTypes/module';
import AesTable = InMemory.AesTable;
import Dataset = Request.Dataset;


@Injectable()
export class AesTableHttpService {

    constructor(protected http: HttpClient,
                protected populationFiltersModel: PopulationFiltersModel,
                protected aesFiltersModel: AesFiltersModel) {
    }

    getData(currentDatasets: Dataset[], aeLevel: string): Observable<AesTable[]> {
        const path = getServerPath('aes', 'aes-table');
        const postData: any = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            aesFilters: this.aesFiltersModel.transformFiltersToServer(),
            aeLevel: aeLevel
        };

        return this.http.post(path, JSON.stringify(postData)).map(res => res as AesTable[]);
    }

    exportData(currentDatasets: Dataset[], aeLevel: string): void {
        const path = getServerPath('aes', 'aes-table-export');
        const postData: any = {
            datasets: currentDatasets,
            populationFilters: this.populationFiltersModel.transformFiltersToServer(),
            aesFilters: this.aesFiltersModel.transformFiltersToServer(),
            aeLevel: aeLevel
        };

        this.http.post(path, JSON.stringify(postData), {responseType: 'blob'})
            .subscribe(response => {
                downloadData('aes_table.csv', response);
            });
    }
}
