package com.definex.sqlgenerator.service;

import java.io.File;

public interface ExcelToSql {
    String excelToSql(File excelFile, Integer sheetInd, String table, boolean firstRowIsTableCol, String columns, Integer rowLimit);
}
