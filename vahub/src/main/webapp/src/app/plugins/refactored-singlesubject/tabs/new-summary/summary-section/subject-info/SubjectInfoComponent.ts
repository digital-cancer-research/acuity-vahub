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
