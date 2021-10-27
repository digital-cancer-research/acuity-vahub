import {Component, Input, OnInit} from '@angular/core';
import * as  _ from 'lodash';

import {CohortEditorService} from '../services/CohortEditorService';
import UserVO = Request.UserVO;

@Component({
    selector: 'cohort-sharing',
    templateUrl: 'CohortSharingComponent.html',
    styleUrls: ['CohortEditorComponent.css']
})
export class CohortSharingComponent implements OnInit {

    @Input()
    sharedUsers: UserVO[] = [];

    @Input()
    ownerPrid: string;

    allDatasetUsers: UserVO[];
    searchText: string;

    constructor(private cohortEditorService: CohortEditorService) {
    }

    ngOnInit(): void {
        this.cohortEditorService.getAllDatasetUsers().subscribe((users) => this.allDatasetUsers = users);
    }

    isSharedToUser(user: UserVO): boolean {
        const isSharedToUsers = _.chain(this.sharedUsers).map('sidAsString').includes(user.sidAsString).value();
        const isOwnedByUser = this.ownerPrid === user.sidAsString;
        return isSharedToUsers || isOwnedByUser;
    }

    toggleSharedUser(user: Request.UserVO): void {
        if (!this.isSharedToUser(user)) {
            this.sharedUsers.push(user);
        } else {
            _.remove(this.sharedUsers, (users) => users.sidAsString === user.sidAsString);
        }
    }

    selectAll(): void {
        this.sharedUsers = _.cloneDeep(this.allDatasetUsers);
    }

    deselectAll(): void {
        this.sharedUsers = [];
    }
}
