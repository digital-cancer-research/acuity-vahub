import {Injectable} from '@angular/core';
import * as  _ from 'lodash';
import {TabId} from '../../trellising/store/ITrellising';
import {TabStoreUtils} from '../../trellising/store/utils/TabStoreUtils';

/**
 * Service for messages generation
 * @property {string[]} part1
 * @property {string[]} part2
 * @property {string[]} part3
 * @property {string[]} part4
 * @property {string[]} part5
 * @property {string[]} part6
 */
@Injectable()
export class ProgressService {

    private static part1: string[] = [
        'Checking', 'Handling', 'Querying', 'Loading',
        'Tabulating', 'Ordering', 'Formatting', 'Preparing',
        'Aligning', 'Integrating', 'Assigning', 'Reticulating splines of'
    ];
    private static part2: string[] = [
        'subject', 'population', 'population subgroup',
        'dynamic .js for', 'code snippets of', 'data-matrix for',
        'key-value hash of', 'ontology for', 'default'
    ];
    private static part3: string[] = [
        'event', 'baseline', 'chronology',
        'linked', 'cross-field', 'immutable object',
        'mutable object', 'dynamic'
    ];
    private static part4: string[] = [
        'sets', 'subsets', 'custom subject sets',
        'custom event sets'
    ];
    private static part5: string[] = [
        'data', 'data linked', 'plot',
        'axis', 'filter', 'hidden', 'cached'
    ];
    private static part6: string[] = [
        'fields', 'functions', 'client objects',
        'browser objects', 'config items'
    ];

    /**
     * Generates random message based around some kind of action related to a given tab
     */
    public generateMessage(tabId: TabId): string {
        let message = _.sample(ProgressService.part1) + ' ';
        message += _.sample(ProgressService.part2) + ' ';
        message += _.sample(ProgressService.part3) + ' ';
        message += _.sample(ProgressService.part4) + ' of ';
        message += TabStoreUtils.getPageNameFromTabId(tabId) + ' for ';
        message += _.sample(ProgressService.part5) + ' ';
        message += _.sample(ProgressService.part6) + '.';
        return message;
    }

}
