import {Observable} from 'rxjs/Observable';
import Dataset = Request.Dataset;
import TrellisOptions = Request.TrellisOptions;

export interface ITrellisServices<T> {
    getTrellisOptions(currentDatasets: Dataset[], yAxisOption: string): Observable<TrellisOptions<T>[]>;
}
