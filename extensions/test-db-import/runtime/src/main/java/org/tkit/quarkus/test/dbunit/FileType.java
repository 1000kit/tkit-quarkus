package org.tkit.quarkus.test.dbunit;

public enum FileType {
    XLS,
    XML;

    /**
     * Returns {@code DataType} of the path.
     *
     * @param file the url of the file.
     * @return {@code DataType} of the path.
     */
    public static FileType getDataType(String file) {
        if (file == null) {
            return null;
        }
        if (file.toString().endsWith(".xml")) {
            return FileType.XML;
        }
        if (file.toString().endsWith(".xls")) {
            return FileType.XLS;
        }
        return null;
    }
}
