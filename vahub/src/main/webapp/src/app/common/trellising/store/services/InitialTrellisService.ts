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

import {TabId, ITrellises, TrellisCategory, AnalyteTrellises, TermLevelType, YAxisType} from '../ITrellising';
import {fromJS, List} from 'immutable';
import * as  _ from 'lodash';
import {TabStoreUtils} from '../utils/TabStoreUtils';

export class InitialTrellisService {
    private static findAndInsert(initial: ITrellises[], trellises: ITrellises[], ...trellisedBy: string[]): void {
        const trellis: ITrellises[] = trellisedBy.map((x) => <ITrellises>_.find(trellises, {'trellisedBy': x}));
        const firstTrellis = _.find(trellis, (x) => x !== undefined);
        if (firstTrellis) {
            initial.push(firstTrellis);
        }
    }

    static inOptions(trellises: ITrellises[], ...trellisedBy: string[]): boolean {
        if (trellisedBy.length === 0) {
            return true;
        } else {
            const availableOptions = trellises.map(x => x.trellisedBy);
            return _.intersection(availableOptions, trellisedBy).length > 0;
        }
    }

    static initialNonMandatoryTrellis(
        currentTrellises: ITrellises[], trellises: ITrellises[], tabId: TabId, isOngoing: boolean, allTrellising: boolean): ITrellises[] {
        const initial: ITrellises[] = [];
        if (allTrellising) {
            return initial;
        }
        if (currentTrellises === undefined) {
            if (!isOngoing) {
                switch (tabId) {
                    case TabId.POPULATION_BARCHART:
                    case TabId.POPULATION_TABLE:
                        break;
                    default:
                        this.findAndInsert(initial, trellises, 'ARM');
                        break;
                }
            }
        } else {
            const trellisedBys = currentTrellises.map(x => x.trellisedBy);
            this.findAndInsert(initial, trellises, ...trellisedBys);
        }
        return initial;
    }

    static initialNonMandatorySeries(
        currentTrellises: ITrellises[], series: ITrellises[], tabId: TabId,
        isOngoing: boolean, allSeries: boolean, colorByForYAxis?: YAxisType): ITrellises[] {
        const initial: ITrellises[] = [];
        const trellisedBys = currentTrellises !== undefined ? currentTrellises.map(x => x.trellisedBy) : [];
        if (allSeries) {
            return initial;
        }
        if (currentTrellises === undefined || !this.inOptions(series, ...trellisedBys) ||
            TabStoreUtils.isRequiredToSetDefaultColorByOnOptionChange(tabId)) {
            switch (tabId) {
                case TabId.AES_COUNTS_BARCHART:
                    this.findAndInsert(initial, series, 'MAX_SEVERITY_GRADE');
                    break;
                case TabId.TL_DIAMETERS_PLOT:
                case TabId.TL_DIAMETERS_PER_SUBJECT_PLOT:
                    this.findAndInsert(initial, series, YAxisType.BEST_RESPONSE);
                    break;
                case TabId.TUMOUR_RESPONSE_WATERFALL_PLOT:
                    colorByForYAxis ?
                        this.findAndInsert(initial, series, colorByForYAxis) :
                        this.findAndInsert(initial, series, YAxisType.ASSESSMENT_RESPONSE, YAxisType.BEST_RESPONSE);
                    break;
                case TabId.AES_OVER_TIME:
                    this.findAndInsert(initial, series, 'MAX_SEVERITY_GRADE', 'SEVERITY_GRADE');
                    break;
                case TabId.CVOT_ENDPOINTS_OVER_TIME:
                    this.findAndInsert(initial, series, 'CATEGORY_1');
                    break;
                case TabId.CEREBROVASCULAR_EVENTS_OVER_TIME:
                    this.findAndInsert(initial, series, 'EVENT_TYPE');
                    break;
                case TabId.CI_EVENT_OVERTIME:
                    this.findAndInsert(initial, series, 'FINAL_DIAGNOSIS');
                    break;
                case TabId.SINGLE_SUBJECT_RENAL_LINEPLOT:
                case TabId.RENAL_CKD_BARCHART:
                    this.findAndInsert(initial, series, 'CKD_STAGE_NAME');
                    break;
                case TabId.LAB_LINEPLOT:
                    if (!isOngoing) {
                        this.findAndInsert(initial, series, 'ARM');
                    } else {
                        this.findAndInsert(initial, series, 'SOURCE_TYPE');
                    }
                    break;
                case TabId.SINGLE_SUBJECT_LAB_LINEPLOT:
                    if (isOngoing) {
                        this.findAndInsert(initial, series, 'SOURCE_TYPE');
                    }
                    break;
                case TabId.EXACERBATIONS_GROUPED_COUNTS:
                case TabId.EXACERBATIONS_COUNTS:
                case TabId.EXACERBATIONS_OVER_TIME:
                    this.findAndInsert(initial, series, 'EXACERBATION_SEVERITY');
                    break;
                case TabId.ANALYTE_CONCENTRATION:
                    // TODO: we need only initials here
                    this.findAndInsert(initial, series, ...Object.keys(AnalyteTrellises));
                    break;
                case TabId.AES_CHORD_DIAGRAM:
                    this.findAndInsert(initial, series, ...Object.keys(TermLevelType));
                    break;
                case TabId.CTDNA_PLOT:
                    this.findAndInsert(initial, series, 'SUBJECT');
                    break;
                case TabId.POPULATION_BARCHART:
                case TabId.POPULATION_TABLE:
                    this.findAndInsert(initial, series, 'COUNTRY', 'STUDY_CODE');
                    break;
                case TabId.BIOMARKERS_HEATMAP_PLOT:
                    this.findAndInsert(initial, series, 'ALTERATION_TYPE');
                    break;
                case TabId.QT_PROLONGATION:
                    this.findAndInsert(initial, series, 'ALERT_LEVEL');
                    break;
                default:
                    break;
            }
        } else {
            this.findAndInsert(initial, series, ...trellisedBys);
        }
        return initial;
    }

    static isAll(currentTrellises: List<ITrellises>, baseTrellises: List<ITrellises>, trellisCategory: TrellisCategory): boolean {
        let currentTrellisesJS: any[];
        let baseTrellisesJS: any[];
        if (currentTrellises !== undefined) {
            currentTrellisesJS = currentTrellises.size > 0 ? currentTrellises.toJS() : [];
            baseTrellisesJS = baseTrellises.size > 0 ? baseTrellises.toJS() : [];
        }
        const currentNonMandatory: any[] =
            currentTrellisesJS ? _.filter(currentTrellisesJS, {'category': trellisCategory}) : currentTrellisesJS;
        const baseNonMandatory: any[] = baseTrellisesJS ? _.filter(baseTrellisesJS, {'category': trellisCategory}) : baseTrellisesJS;
        return currentNonMandatory.length === 0 && baseNonMandatory.length > 0;
    }

    static isAllSeries(currentTrellises: List<ITrellises>, baseTrellises: List<ITrellises>): boolean {
        return InitialTrellisService.isAll(currentTrellises, baseTrellises, TrellisCategory.NON_MANDATORY_SERIES);
    }

    static isAllTrellising(currentTrellises: List<ITrellises>, baseTrellises: List<ITrellises>): boolean {
        return InitialTrellisService.isAll(currentTrellises, baseTrellises, TrellisCategory.NON_MANDATORY_TRELLIS);
    }

    /**
     * Generate the initial trellis handling the non mandatory trellis
     * and non mandatory series options if a trellis has already been defined then
     * these options are conserved
     */
    static generateInitialTrellis(currentTrellises: List<ITrellises>,
                                  trellises: List<ITrellises>,
                                  tabId: TabId,
                                  isOngoing: boolean,
                                  firstPass: boolean,
                                  allSeries: boolean,
                                  allTrellising: boolean,
                                  colorByForAxis?: YAxisType): List<ITrellises> {
        const trellisesJS: any[] = trellises.toJS();
        let currentTrellisesJS: any[];
        if (currentTrellises !== undefined) {
            currentTrellisesJS = currentTrellises.size > 0 ? currentTrellises.toJS() : undefined;
        }
        if (!firstPass && !currentTrellisesJS) {
            currentTrellisesJS = [];
        }
        const initialTrellis: ITrellises[] = [];

        const currentNonMandatoryTrellis: any[] =
            currentTrellisesJS ? _.filter(currentTrellisesJS, {'category': TrellisCategory.NON_MANDATORY_TRELLIS}) : currentTrellisesJS;
        const nonMandatoryTrellis: any[] = _.filter(trellisesJS, {'category': TrellisCategory.NON_MANDATORY_TRELLIS});
        const currentNonMandatorySeries: any[] =
            currentTrellisesJS ? _.filter(currentTrellisesJS, {'category': TrellisCategory.NON_MANDATORY_SERIES}) : currentTrellisesJS;
        const nonMandatorySeries: any[] = _.filter(trellisesJS, {'category': TrellisCategory.NON_MANDATORY_SERIES});
        const mandatoryTrellis: any[] = _.filter(trellisesJS, {'category': TrellisCategory.MANDATORY_TRELLIS});
        const mandatoryHigherTrellis: any[] = _.filter(trellisesJS, {'category': TrellisCategory.MANDATORY_HIGHER_LEVEL});

        if (mandatoryHigherTrellis.length > 0) {
            mandatoryHigherTrellis.forEach((trellis) => {
                initialTrellis.push(trellis);
            });
        }
        if (mandatoryTrellis.length > 0) {
            mandatoryTrellis.forEach((trellis) => {
                initialTrellis.push(trellis);
            });
        }
        if (nonMandatoryTrellis.length > 0) {
            InitialTrellisService.initialNonMandatoryTrellis(
                currentNonMandatoryTrellis, nonMandatoryTrellis, tabId, isOngoing, allTrellising).forEach((trellis) => {
                initialTrellis.push(trellis);
            });
        }
        if (nonMandatorySeries.length > 0) {
            //
            InitialTrellisService.initialNonMandatorySeries(
                currentNonMandatorySeries, nonMandatorySeries, tabId, isOngoing, allSeries, colorByForAxis).forEach((trellis) => {
                initialTrellis.push(trellis);
            });
        }
        return fromJS(initialTrellis);
    }

}
