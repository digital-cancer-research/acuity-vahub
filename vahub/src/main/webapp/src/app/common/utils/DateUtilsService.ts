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
