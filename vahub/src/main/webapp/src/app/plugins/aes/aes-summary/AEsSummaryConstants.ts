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

interface SummaryCategoryLink {
    routeData: RouteData;
    name: string;
}


interface RouteData {
    link: string;
    api: string;
}

export const SAVE_MODAL = {
    title: 'Download table?',
    message: 'This data has not been validated and must not be used within regulatory submissions or as input to regulatory reports. ' +
             'All findings must be confirmed through the validated study programmer outputs.',
    textButton: 'Continue to download',
    canBeDismissed: true
};

export const ANY_CATEGORY = {link: 'any', api: 'summaries-any-category'};
export const COMMON_CATEGORY = {link: 'common', api: 'summaries-mst-cmn'};
export const DEATH_OUTCOME_CATEGORY = {link: 'death-outcome', api: 'summaries-death-outcome'};
export const LDOST_CATEGORY = {link: 'ldost', api: 'summaries-sae-lead-to-disc'};

export const ANY_AE_NAME = 'Adverse Events in any category - subject level';
export const MOST_COMMON_AE_NAME = 'Adverse Events; most common (frequency of > 5% in any subject group)';
export const DEATH_OUTCOME_AE_NAME = 'Adverse Events with outcome of death by System Organ Class and Preferred Term';
export const LDOST_SAE_NAME = 'Serious Adverse Events leading to discontinuation of Study Treatment, ' +
                              'by System Organ Class and Preferred Term';

export const DEFAULT_SUMMARY_CATEGORY = ANY_CATEGORY;

export const SUMMARY_CATEGORIES: SummaryCategoryLink[] = [
    {name: 'Any Category', routeData: ANY_CATEGORY},
    {name: 'Most common AEs', routeData: COMMON_CATEGORY},
    {name: 'AEs with outcome of death', routeData: DEATH_OUTCOME_CATEGORY},
    {name: 'SAEs leading to discontinuation of study treatment', routeData: LDOST_CATEGORY}
];
export const DEFAULT_HEADER_VALUE = '(Empty)';

export const NOTE_LDOST_TERMS =
    'The following terms have been used to determine whether an {AE_TYPE} led to discontinuation of study treatment: ' +
    '<ul><li>drug permanently discontinued, drug withdrawn, permanently stopped</li></ul>';

export const NOTE_AE_INCLUSION =
    'Only those adverse events with an onset date on or after the date of first dose and up to and including ' +
    '28 days following the date of last dose of study medication are considered.';

export const ANY_AE_NOTES =
    [
        NOTE_AE_INCLUSION,
        `'Causally related' is as assessed by the investigator.`,
        'The following terms have been used to determine whether an AE is causally related to study treatment: ' +
            '<ul><li>1, Probably Related, Possible, Related, Yes, Possibly Related, Definitely Related</li></ul>',
        'The following terms have been used to determine whether an AE is a serious adverse event: ' +
            '<ul><li>1, AESER, Yes, Y</li></ul>',
        NOTE_LDOST_TERMS.replace(/\{AE_TYPE\}/g, 'AE')
    ];

export const LDOST_AE_NOTES = [
    NOTE_AE_INCLUSION,
    NOTE_LDOST_TERMS.replace(/\{AE_TYPE\}/g, 'SAE')
];

export const COMMON_AE_NOTES =
    [
        NOTE_AE_INCLUSION
    ];
