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
