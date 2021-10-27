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

export function translateTrellisingFromServer(trellising: any[]): any[] {
    // Need to add category for MEASUREMENT option for compatibility with existing chart
    trellising.forEach((t) => {
        if (t.trellisedBy === 'MEASUREMENT') {
            t.category = 'MANDATORY_TRELLIS';
        } else {
            t.category = 'NON_MANDATORY_TRELLIS';
        }
    });
    return trellising;
}

export function translateOptionsToServer(trellising: any): any {
    // Need to find all combinations of MEASUREMENT and ARM for compatibility with existing chart
    // Convert from:
    // [
    //   {
    //     "category": "MANDATORY_TRELLIS",
    //     "trellisedBy": "MEASUREMENT",
    //     "trellisOptions": [
    //       "AST",
    //       "ALT"
    //     ]
    //   },
    //   {
    //     "category": "NON_MANDATORY_TRELLIS",
    //     "trellisedBy": "ARM",
    //     "trellisOptions": [
    //       "Placebo",
    //       "SuperDex 10 mg",
    //       "SuperDex 20 mg"
    //     ]
    //   }
    // ]
    // to:
    // {
    //   "filterByTrellisOptions": [
    //     {
    //       "MEASUREMENT": "AST",
    //       "ARM": "Placebo"
    //     },
    //     {
    //       "MEASUREMENT": "AST",
    //       "ARM": "SuperDex 10 mg"
    //     },
    //     {
    //       "MEASUREMENT": "AST",
    //       "ARM": "SuperDex 20 mg"
    //     },
    //     {
    //       "MEASUREMENT": "ALT",
    //       "ARM": "Placebo"
    //     },
    //     ...
    //   ]
    //      * }
    const filterByTrellisOptions = [];
    if (trellising[0]) {
        for (const measurement of trellising[0].trellisOptions) {
            if (trellising[1]) {
                for (const arm of trellising[1].trellisOptions) {
                    filterByTrellisOptions.push({MEASUREMENT: measurement, ARM: arm});
                }
            } else {
                filterByTrellisOptions.push({MEASUREMENT: measurement});
            }
        }
    }
    return {
        filterByTrellisOptions: filterByTrellisOptions
    };
}

export function translateOptionToServer(trellising: any): any {
    const filterByTrellisOptions = [];
    if (trellising[1]) {
        filterByTrellisOptions.push({MEASUREMENT: trellising[0].trellisOption, ARM: trellising[1].trellisOption});
    } else {
        filterByTrellisOptions.push({MEASUREMENT: trellising[0].trellisOption});
    }
    return {
        filterByTrellisOptions: filterByTrellisOptions
    };
}

export function translateValueFromServer(value: any): any {
    return {
        data: value.data,
        xaxisLabel: value.xaxisLabel,
        yaxisLabel: value.yaxisLabel,
    };
}
