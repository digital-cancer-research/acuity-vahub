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

import {Injectable} from '@angular/core';
import {Map} from 'immutable';

import {EXPORT_DELIMETER} from '../../../../common/utils/ExportUtils';
import {HeaderModel} from './SubjectSummaryConstants';

@Injectable()
export class SubjectSummaryService {

    getExportParams(allHeaders: HeaderModel[]): any {
        return {
            customHeader: this.getCustomHeader(allHeaders),
            columnSeparator: EXPORT_DELIMETER,
        };
    }

    private getCustomHeader(allHeaders: HeaderModel[]): string {
        const rows = [
            ['patientId', 'studyDrug', 'startDate'],
            ['deathDate', 'studyId', 'withdrawalDate'],
            ['', 'studyName', 'withdrawalReason'],
            ['', 'studyPart', ''],
            ['', 'datasetName', ''],
        ];

        const headersMap = Map<string, any>(
            allHeaders.map(header => [header.name, {displayName: header.displayName, value: header.value}])
        );

        let customHeader = rows.map(row => {
            return row.map(key => {
                const headerData = headersMap.get(key);

                return headerData ? `${headerData.displayName}: ${headerData.value}` : '';
            }).join(EXPORT_DELIMETER);
        }).join('\n');

        customHeader += '\n';

        return customHeader;
    }
}
