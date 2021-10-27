import * as  _ from 'lodash';

export class TimelinePaginationService {

    static getPages(numberOfSubjects: number, limit: number): number[] {
        if (!numberOfSubjects || numberOfSubjects === 0 || !limit) {
            return [];
        }
        return _.range(1, Math.ceil(numberOfSubjects / limit) + 1, 1);
    }
}
