import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs/BehaviorSubject';
import {Router} from '@angular/router';
import {Store} from '@ngrx/store';

import {ApplicationState} from '../../common/store/models/ApplicationState';
import {getSelectedSubjectId} from './store/reducers/SingleSubjectViewReducer';

@Injectable()
export class SingleSubjectModel {
    currentChosenSubject = '';
    chosenSubject: BehaviorSubject<string> = new BehaviorSubject<string>(this.currentChosenSubject);

    constructor(private router: Router, private _store: Store<ApplicationState>) {
        this.router.events.subscribe((event: any) => {
            if (event.url === '/') {
                this.changeChosenSubject(null);
            }
        });
        this._store.select(getSelectedSubjectId).subscribe((subjectId) => {
            this.changeChosenSubject(subjectId);
        });
    }

    private changeChosenSubject(newSubject: string): void {
        if (this.currentChosenSubject !== newSubject) {
            this.currentChosenSubject = newSubject;
            this.chosenSubject.next(newSubject);
        }
    }
}
