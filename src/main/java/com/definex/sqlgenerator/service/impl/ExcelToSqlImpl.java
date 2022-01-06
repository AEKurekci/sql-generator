package com.definex.sqlgenerator.service.impl;

import com.definex.sqlgenerator.service.ExcelToSql;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;

@Service
public class ExcelToSqlImpl implements ExcelToSql {

    @Override
    public String excelToSql(File excelFile, Integer sheetInd, String table, boolean firstRowIsTableCol, String columns, Integer rowLimit){
        String query = "";
        String insert = "INSERT INTO " + table;
        String columnsString = "";
        try{
            FileInputStream file = new FileInputStream(excelFile);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(sheetInd);
            if(!firstRowIsTableCol) {
                columnsString = " (" + columns + ") VALUES";
            }
            boolean firstRowPassed = false;
            int rowCounter = 0;
            for(Row row: sheet){
                rowCounter++;
                int colIndex = 0;
                if(!firstRowPassed){
                    if(!firstRowIsTableCol){
                        firstRowPassed = true;
                        continue;
                    }
                    columnsString += " (";
                    for(; colIndex < row.getLastCellNum(); colIndex++){
                        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        columnsString += cell.getStringCellValue() + ",";
                    }
                    columnsString = columnsString.substring(0, columnsString.length() - 1) + ") VALUES";
                    firstRowPassed = true;
                }
                else {
                    query += insert + columnsString + " (";
                    for(; colIndex < row.getLastCellNum(); colIndex++){
                        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
                        if (cell == null){
                            query += null + ",";
                            continue;
                        }
                        switch (cell.getCellType()){
                            case STRING:
                                String cellContent = cell.getStringCellValue();
                                cellContent = cellContent.replace("'", "''");
                                query += "'" + cellContent + "',";
                                break;
                            case NUMERIC:
                                query += cell.getNumericCellValue() + ",";
                                break;
                            case FORMULA:
                                query = constructFormula(query, cell) + ",";
                        }
                    }
                    query = query.substring(0, query.length() - 1) + "); ";
                }
                if (rowCounter > rowLimit) break;
            }
            query = query.substring(0, query.length() - 2) + ";";
            workbook.close();
            file.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        return query;
    }

    private String constructFormula(String query, Cell cell){
        switch (cell.getCachedFormulaResultType()){
            case STRING:
                query += cell.getRichStringCellValue();
                break;
            case NUMERIC:
                query += cell.getNumericCellValue();
                break;
            case BOOLEAN:
                query += cell.getBooleanCellValue();
                break;
        }
        return query;
    }
}
