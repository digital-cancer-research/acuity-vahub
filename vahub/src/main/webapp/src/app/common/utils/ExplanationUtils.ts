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

import {Map} from 'immutable';
import {TabId} from '../trellising/store/ITrellising';

export interface Explanation {
    header: string;
    content: string;
    modalWidth: number;
}

export const EXPLANATION_MAP = Map<TabId, Explanation>([
    [TabId.AES_CHORD_DIAGRAM, {
        header: 'Chord diagram explanation',
        content: `A chord diagram displays the inter-relationships between data in a matrix. The names of the data
             are arranged radially around a circle with the relationships between the data drawn as chords; the wider
             the chord the stronger the relationship.<br/>
             <br/>
             This plot explores the frequency of the co-occurrence of different adverse events – the wider the
             chord the higher the number of co-occurrences. By default, two adverse events are linked if they occurred on
             the same day.<br/>
             <br/>
             It should be noted that a single subject may have an adverse event more than once and thus
             contribute multiple times to a single chord.<br/>`,
        modalWidth: 450
    }],
    [TabId.CTDNA_PLOT, {
        header: 'Variant Allele Frequency of ctDNA Plot Explanation',
        content: `The information below describes the functionality for plotting a data point on the
        Variant Allele Frequency (VAF) ctDNA plot.<br/>
        <b>A value of 0.2 percent / 0.002 fraction is considered as the lower limit of detection for this plot.</b><br/>
        To establish which sample dates a data point needs to be plotted as 0.2 percent / 0.002 fraction, for a particular subject,
        sample dates in the data file are grouped per subject. VAF values for a mutation/s within a gene/s will be plotted as follows:
        <ul class="li-mt1em">
            <li>
            Data points will be <b>plotted above 0.2 percent / 0.002 fraction</b> if a subject profile contains VAF values
            greater than 0.2 percent / 0.002 fraction.
            </li>
            <li>
            Data points will be <b>plotted at 0.2 percent / 0.002 fraction</b> if
            <ul>
                <li>a subject has a VAF value equal to 0.2 percent / 0.002 fraction</li>
                <li>a subject has a VAF value of less than 0.2 percent / 0.002 fraction</li>
                <li>a subject has no mutations detected</li>
            </ul>
            </li>
        </ul>
        Note: the above will only apply when the subjects sample profile contains one or more VAF values of greater
        than 0.2 percent / 0.002 fraction.
        <ul class="li-mt1em">
            <li>
                <b>Data points will not be shown on the plot</b> if a subject’s mutation profile has 'no mutations detected' or
                VAF values of less than or equal to 0.2 percent / 0.002 fraction over the whole sampling period.
            </li>
            <li>
                There will be <b>no connecting line</b> from one sample date to the next if a sample is considered 'missing / not taken’
                in a subject’s profile;
                this is when a sample date is missing in one subject profile against another subject profile.
                In this case the line on the plot will not join to the no mutation detected point (0.2 percent / 0.002 fraction)
                on the plot, the line will connect straight to the next sample date in that subjects’ profile.
            </li>
        </ul>`,
        modalWidth: 800
    }]
]);
