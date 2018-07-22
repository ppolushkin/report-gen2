package io.github.ppolushkin.domain;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Pavel Polushkin
 * 28.06.2017.
 */
public interface ExcelReader {

    final static SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");

    void loadWorkBook(String resourceLocation, String workSheet) throws IOException, InvalidFormatException;

    Workbook getWorkBook();

    String getWorkSheet();

    default String readNumber(String cellLocation) {
        Cell cell = getCell(cellLocation);
        return readNumber(cell);
    }

    default String readDate(String cellLocation) {
        Cell cell = getCell(cellLocation);
        return readDate(cell);
    }

    default String readString(String cellLocation) {
        return readString(getCell(cellLocation));
    }

    default String readString(int columnIndex, int lineNumber) {
        return readString(getCell(columnIndex, lineNumber));
    }

    default String readString(Cell cell) {
        return cell == null ? "" : cell.getStringCellValue();
    }

    default String readNumber(Cell cell) {
        try {
            double val = cell.getNumericCellValue();
            return "" + (int) val;
        } catch (Exception e) {
            return "";
        }
    }

    default String readDate(Cell cell) {
        try {
            Date val = cell.getDateCellValue();
            return "" + formatter.format(val);
        } catch (Exception e) {
            return "";
        }
    }

    default Cell getCell(String cellLocation) {
        return getCell(getWorkBook(), getWorkSheet(), cellLocation);
    }

    default Cell getCell(int columnIndex, int lineNumber) {
        Sheet sheet = getWorkBook().getSheet(getWorkSheet());
        Row row = sheet.getRow(lineNumber - 1); // in my model I start from 1, but Excel starts from 0.
        return row.getCell(columnIndex -1); //A = 1, B = 2, ...
    }

    default Cell getCell(Workbook wb, String workSheet, String cellLocation) {
        Sheet sheet = wb.getSheet(workSheet);
        CellReference cellReference = new CellReference(cellLocation);
        Row row = sheet.getRow(cellReference.getRow());
        return row.getCell(cellReference.getCol());
    }



}
