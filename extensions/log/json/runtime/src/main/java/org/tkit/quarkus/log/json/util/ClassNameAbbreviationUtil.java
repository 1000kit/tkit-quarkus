package org.tkit.quarkus.log.json.util;

public class ClassNameAbbreviationUtil {

    /**
     * Creates abbreviation from class name in the for of <code>p.p.p.p.ClassName</code> where <code>p</code> stands for first
     * letter of the corresponding package.
     *
     * @param className class name to abbreviate
     * @return abbreviated classname
     */
    public static String abbreviateClassName(String className) {
        StringBuilder sb = new StringBuilder();
        int fromIndex = 0;
        int dollarIndex = className.indexOf("$", fromIndex);
        String suffix = null;
        if (dollarIndex != -1) {
            className = className.substring(0, dollarIndex);
            suffix = className.substring(dollarIndex);
        }

        //TODO make this more efficient
        while (true) {
            int index = className.indexOf(".", fromIndex);
            if (index == -1) {
                sb.append(className.substring(fromIndex));
                break;
            }
            String part = className.substring(fromIndex, index);
            sb.append(part, 0, 1);
            sb.append(".");
            fromIndex = index + 1;
        }
        return sb.toString();
    }

}
