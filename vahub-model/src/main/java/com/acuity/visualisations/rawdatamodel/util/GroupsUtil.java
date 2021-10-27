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
