package com.definex.sqlgenerator.api.controller;

import com.definex.sqlgenerator.service.ExcelToSql;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping(value = "api/excel-to-sql/oracle", produces = "multipart/form-data",
        consumes = "multipart/form-data")
@AllArgsConstructor
@Tag(name = "Sql Converter Api Documentation")
public class Controller {

    private ExcelToSql excelToSqlService;

    @PostMapping("create-insert")
    @Operation(summary = "Users upload excel files and obtain insert queries for Oracle database")
    public ResponseEntity<?> excelToSql(@RequestParam(name = "file") MultipartFile excelFile,
                                        @RequestParam(name = "sheet") Integer sheetInd,
                                        @RequestParam(name = "table") String table,
                                        @RequestParam(name = "first-row-is-table-col") @NonNull boolean firstRowIsTableCol,
                                        @RequestParam(name = "columns") @Nullable String columns,
                                        @RequestParam(name = "row-limit") @NonNull Integer rowLimit) throws IOException {
        File file = new File(System.getProperty("java.io.tmpdir") + "/" + excelFile.getOriginalFilename());
        excelFile.transferTo(file);
        file.deleteOnExit();
        return new ResponseEntity<>(excelToSqlService.excelToSql(file, sheetInd, table, firstRowIsTableCol, columns, rowLimit), HttpStatus.OK);
    }
}
