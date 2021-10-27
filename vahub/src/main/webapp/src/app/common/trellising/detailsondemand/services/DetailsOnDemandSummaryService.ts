import {Injectable} from '@angular/core';
import {isEmpty} from 'lodash';
import {Map} from 'immutable';

import {IMultiSelectionDetail, ISelectionDetail} from '../../store/ITrellising';

@Injectable()
export class DetailsOnDemandSummaryService {

    getSubjectSummary(selection: ISelectionDetail | IMultiSelectionDetail): string {
        let summary = '';
        if (!isEmpty(selection)) {
            summary = selection.subjectIds.length + ' of ' + selection.totalSubjects;
            if (this.areAllSubjectsSelected(selection)) {
                return '(All) ' + summary;
            }
        }
        return summary;
    }

    getEventSummary(selection: ISelectionDetail): string {
        let summary = '';
        if (!isEmpty(selection)) {
            const eventCount = selection.eventCount || selection.eventIds.length;
            summary = eventCount + ' of ' + selection.totalEvents;
            if (this.areAllEventsSelected(selection)) {
                return '(All) ' + summary;
            }
        }
        return summary;
    }

    getMultipleEventSummary(selection: IMultiSelectionDetail): Map<string, string> {
        let summary = Map<string, string>();
        if (!isEmpty(selection)) {
            selection.eventIds.mapKeys((key, value) => {
                let summaryString = value.length.toString();
                if (value.length === selection.totalEvents.get(key)) {
                    summaryString = `(All) ${summaryString} of ${selection.totalEvents.get(key)}`;
                } else {
                    summaryString = `${value.length} of ${selection.totalEvents.get(key)}`;
                }
                summary = summary.set(key, summaryString);
            });
        }
        return summary;
    }

    private areAllSubjectsSelected(selection: ISelectionDetail | IMultiSelectionDetail): boolean {
        return selection.totalSubjects === selection.subjectIds.length;
    }

    private areAllEventsSelected(selection: ISelectionDetail): boolean {
        const eventCount = selection.eventCount || selection.eventIds.length;
        return selection.totalEvents === eventCount;
    }
}
