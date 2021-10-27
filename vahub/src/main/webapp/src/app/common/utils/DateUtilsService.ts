import {Injectable} from '@angular/core';
import * as moment from 'moment';
import {isNull} from 'lodash';

@Injectable()
export class DateUtilsService {

    private readonly DATE_FORMAT = 'DD-MMM-YYYY';
    private readonly TIME_FORMAT = 'HH:mm:ss';
    private readonly TIMESTAMP_FORMAT = 'YYYY-MM-DDT' + this.TIME_FORMAT;

    toShortDate(date: string): string {
        if (isNull(date)) {
            return null;
        }
        return moment.utc(date).format(this.DATE_FORMAT);
    }

    toDateTime(date: string): string {
        if (isNull(date)) {
            return null;
        }
        return moment.utc(date).format(this.DATE_FORMAT + ' ' + this.TIME_FORMAT);
    }

    toTimestamp(date: string): string {
        if (isNull(date)) {
            return null;
        }
        return moment.utc(date, this.DATE_FORMAT).format(this.TIMESTAMP_FORMAT);
    }

    getNow(): moment.Moment {
        return moment.utc();
    }
}
