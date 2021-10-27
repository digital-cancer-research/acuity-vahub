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

import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {omit} from 'lodash';
import {Map} from 'immutable';

@Component({
    templateUrl: 'SubjectInfoComponent.html',
    changeDetection: ChangeDetectionStrategy.OnPush,
    styleUrls: ['SubjectInfoComponent.css'],
    selector: 'subject-info'
})
export class SubjectInfoComponent implements OnInit {
    @Input() header: any;
    columns: Array<any> = [];

    ngOnInit(): void {
        const headers = this.header.map(header => [header.name, omit(header, 'name')]);
        const headersMap = Map<string, any>(headers);
        const names = [['patientId', 'deathDate'],
            ['studyDrug', 'studyId', 'studyName', 'studyPart', 'datasetName'],
            ['startDate', 'withdrawalDate', 'withdrawalReason']];

        names.forEach((namesArray => {
            this.columns.push(namesArray.filter(name => {
                return headersMap.get(name) && headersMap.get(name).value !== '';
            }).map((name, index) => headersMap.get(name)));
        }));
    }
}
