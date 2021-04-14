package ru.minat0.minetail.core.utils;

import org.jetbrains.annotations.NotNull;

import java.util.StringTokenizer;

// FIXME: 14.04.2021
public class StringSerialize {
    public static String serialize(@NotNull String[] strs) {
        StringBuilder sizes = new StringBuilder("$");
        StringBuilder result = new StringBuilder();

        for (String str : strs) {
            if (sizes.length() != 1) {
                sizes.append(';');
            }
            sizes.append(str.length());
            result.append(str);
        }

        result.append(sizes.toString());
        return result.toString();
    }

    public static String[] unserialize(String result) {
        if (result != null) {
            int sizesSplitPoint = result.lastIndexOf('$');
            String sizes = result.substring(sizesSplitPoint + 1);
            StringTokenizer st = new StringTokenizer(sizes, ";");
            String[] resultArray = new String[st.countTokens()];

            int i = 0;
            int lastPosition = 0;
            while (st.hasMoreTokens()) {
                String stringLengthStr = st.nextToken();
                int stringLength = Integer.parseInt(stringLengthStr);
                resultArray[i++] = result.substring(lastPosition, lastPosition + stringLength);
                lastPosition += stringLength;
            }
            return resultArray;
        }

        return new String[0];
    }
}
