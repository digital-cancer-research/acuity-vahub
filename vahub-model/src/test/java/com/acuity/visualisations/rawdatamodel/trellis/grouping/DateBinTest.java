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

package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class DateBinTest {
    @Test
    public void testGetBin() throws Exception {
        final Bin bin1 = Bin.newInstance(DaysUtil.toDate("1981-03-24"), 50);
        final Bin bin2 = Bin.newInstance(DaysUtil.toDate("1981-03-23"), 50);
        System.out.println(bin1);
        System.out.println(bin2);
        Assertions.assertThat(bin1).hasToString("1981-03-24 - 1981-05-12");
        Assertions.assertThat(bin2).hasToString("1981-02-02 - 1981-03-23");
    }
    @Test
    public void testGetNextBin() throws Exception {
        final Bin bin = Bin.newInstance(DaysUtil.toDate("1981-03-24"), 50);
        final Bin nextBin = bin.getNextBin();
        System.out.println(bin);
        System.out.println(nextBin);
        Assertions.assertThat(bin).hasToString("1981-03-24 - 1981-05-12");
        Assertions.assertThat(nextBin).hasToString("1981-05-13 - 1981-07-01");
    }

}
