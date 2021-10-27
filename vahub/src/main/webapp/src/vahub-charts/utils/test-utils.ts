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

import * as d3 from 'd3';
import {UserOptions} from '../types/interfaces';

export const getAllElementsBySelector = (selector, renderTo) => d3.select(renderTo).selectAll(selector)._groups[0];

export const getTestOptions = (type: string, renderTo, click, select): UserOptions => {
    return {
        chart: {
            renderTo: renderTo,
            type: type,
            height: 400,
            width: 800,
            id: 'test_id',
            events: {
                selection: select,
                click: click,
                removeSelection: () => {
                },
            }
        },
        title: {
            text: 'test title',
        },
        exporting: {
            buttons: [{
                text: 'testClick',
                onclick: click
            }]
        }
    };
};
