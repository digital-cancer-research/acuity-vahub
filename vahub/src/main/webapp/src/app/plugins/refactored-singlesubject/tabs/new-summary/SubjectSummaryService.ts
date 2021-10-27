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
