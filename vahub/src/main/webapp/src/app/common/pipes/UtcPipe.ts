import { Pipe, PipeTransform } from '@angular/core';
import * as moment from 'moment';

import {DateUtilsService} from '../utils/DateUtilsService';

/**
 * Angular's DatePipe & Javascript's Date constructor converts the date to the user's timezone. This pipe does not
 *
 * Example:
 * {{ "2014-04-22T00:00:00" | utc }}
 * formats to: "22-Apr-2014 00:00:00"
 *
 * {{ "2014-04-22T00:00:00" | utc:true }}
 * formats to: "22-Apr-2014"
 *
 * {{ undefined | utc }}
 * formats to: "--"
 */
@Pipe({ name: 'utc' })
export class UtcPipe implements PipeTransform {

    constructor(private dateUtilsService: DateUtilsService) {
    }

    transform(date: string, hideTime?: boolean): string {
        if (!date) {
            return '--';
        }

        if (!hideTime) {
            return this.dateUtilsService.toDateTime(date);
        }

        return this.dateUtilsService.toShortDate(date);
    }
}
