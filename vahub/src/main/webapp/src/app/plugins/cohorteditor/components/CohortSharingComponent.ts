/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
