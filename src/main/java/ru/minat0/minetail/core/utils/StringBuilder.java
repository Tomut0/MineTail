package ru.minat0.minetail.core.utils;

import org.jetbrains.annotations.NotNull;

import java.util.StringTokenizer;

public class StringBuilder {
    public static String serialize(@NotNull String[] strs) {
        java.lang.StringBuilder sizes = new java.lang.StringBuilder("$");
        java.lang.StringBuilder result = new java.lang.StringBuilder();

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
            int sizesSplitPoint = result.toString().lastIndexOf('$');
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
