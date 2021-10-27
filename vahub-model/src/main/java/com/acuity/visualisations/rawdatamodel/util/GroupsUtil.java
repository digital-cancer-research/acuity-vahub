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

package com.acuity.visualisations.rawdatamodel.util;

public final class GroupsUtil {

    private GroupsUtil() {

    }

    /**
     * Returns the alphabet character that matches specified index. In case when index is less than 26 it returns a
     * single character, otherwise it try to return combination of two characters, for example 'AB' for index equals 28.
     * @param index to define the alphabet character.
     * @return the alphabet character or combination of two alphabet characters; otherwise an empty string.
     */
    public static String getGroupCharacter(int index) {
        index--;
        if (index < 26) {
            return String.valueOf((char) (65 + index));
        } else if (index < 702) {
            return String.valueOf((char) (65 + index / 26 - 1)) + (char) (65 + index % 26);
        }
        return "";
    }
}
