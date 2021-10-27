import {Observable} from 'rxjs/Observable';
import Dataset = Request.Dataset;

export interface IFiltersServices {
    getSubjectsInFilters(currentDatasets: Dataset[]): Observable<string[]>;
}
