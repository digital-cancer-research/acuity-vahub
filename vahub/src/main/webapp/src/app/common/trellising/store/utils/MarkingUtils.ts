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
