package org.example.datalabelingtool.domain.datasets.entity;

public enum DatasetColumn {
    SQL_QUERY("sql_query"),
    NATURAL_QUESTION("natural_question"),
    NO_SQL_TEMPLATE("no_sql_template"),
    SQL_TEMPLATE("sql_template");

    private final String columnName;

    DatasetColumn(String columnName) {
        this.columnName = columnName;
    }

    @Override
    public String toString() {
        return columnName;
    }
}
