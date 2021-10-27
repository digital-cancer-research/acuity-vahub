import {chain, uniq, flatten} from 'lodash';
import {Map} from 'immutable';

import {IMultiSelectionDetail, ISelectionDetail} from '../ITrellising';

export class MarkingUtils {
    static merge(details: ISelectionDetail[]): ISelectionDetail {
        if (!details) {
            return;
        }
        const subjectIds: string[] = <string[]>chain(details)
            .map(detail => detail.subjectIds)
            .flatten()
            .uniq()
            .value()
            .sort();

        const eventIds = chain(details)
            .map(detail => detail.eventIds)
            .flatten()
            .uniq()
            .value()
            .sort();

        return <ISelectionDetail>{
            subjectIds: subjectIds,
            eventIds: eventIds,
            totalSubjects: details[0].totalSubjects,
            totalEvents: details[0].totalEvents
        };

    }

    static mergeMultiple(details: IMultiSelectionDetail[]): IMultiSelectionDetail {
        if (!details) {
            return;
        }
        const subjectIds: string[] = <string[]>chain(details)
            .map(detail => detail.subjectIds)
            .flatten()
            .uniq()
            .value()
            .sort();

        const eventsByTables = {};
        const totalByTables = {};

        details[0].eventIds.mapKeys(key => {
            const events = details.map(value => value.eventIds.get(key));
            const event = events.reduce((prev, curr) => {
                    return [...prev, ...curr];
                });
            eventsByTables[key] = uniq(event).sort();
            totalByTables[key] = details[0].totalEvents.get(key);
        });

        return <IMultiSelectionDetail>{
            subjectIds: subjectIds,
            eventIds: Map(eventsByTables),
            totalSubjects: details[0].totalSubjects,
            totalEvents: totalByTables
        };

    }
}
