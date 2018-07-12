package io.github.ppolushkin.domain;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;

import java.io.IOException;

/**
 * Created by Pavel Polushkin
 * 28.06.2017.
 */
public interface ExcelReader {

    void loadWorkBook(String resourceLocation, String workSheet) throws IOException, InvalidFormatException;

    Workbook getWorkBook();

    String getWorkSheet();

    default Cell getCell(Workbook wb, String workSheet, String cellLocation) {
        Sheet sheet = wb.getSheet(workSheet);
        CellReference cellReference = new CellReference(cellLocation);
        Row row = sheet.getRow(cellReference.getRow());
        return row.getCell(cellReference.getCol());
    }

    default Double readDouble(String cellLocation) {
        Cell cell = getCell(getWorkBook(), getWorkSheet(), cellLocation);
        try {
            return cell.getNumericCellValue();
        } catch (Exception e) {
            return Double.NaN;
        }
    }

    default String readString(String cellLocation) {
        Cell cell = getCell(getWorkBook(), getWorkSheet(), cellLocation);
        return cell == null ? "" : cell.getStringCellValue();
    }


}
