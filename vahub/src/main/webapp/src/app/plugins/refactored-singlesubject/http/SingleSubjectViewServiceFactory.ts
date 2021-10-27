import {Injectable} from '@angular/core';
import {TabId} from '../../../common/trellising/store';
import {BaseSingleSubjectViewHttpService} from './BaseSingleSubjectViewHttpService';

@Injectable()
export class SingleSubjectViewServiceFactory {

    constructor(private baseSingleSubjectViewHttpService: BaseSingleSubjectViewHttpService) {}

    getSingleSubjectViewHttpService(tabId: TabId): BaseSingleSubjectViewHttpService {
        switch (tabId) {
            default:
                return this.baseSingleSubjectViewHttpService;
        }
    }
}
