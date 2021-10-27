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
