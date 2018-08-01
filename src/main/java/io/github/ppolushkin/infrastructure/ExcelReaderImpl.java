package io.github.ppolushkin.infrastructure;

import io.github.ppolushkin.domain.ExcelReader;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * https://poi.apache.org/spreadsheet/quick-guide.html#NewWorkbook
 */
@Service
public class ExcelReaderImpl implements ExcelReader {

    private Workbook workbook;

    private String workSheet;

    @Override
    public void loadWorkBook(String fileLocation, String workSheet) throws IOException, InvalidFormatException {
        if (workbook != null) {
            throw new IllegalArgumentException("Workbook already opened");
        }
        this.workbook = WorkbookFactory.create(new File(fileLocation), null, true);
        this.workSheet = workSheet;
    }

    @Override
    public Workbook getWorkBook() {
        return workbook;
    }

    @Override
    public String getWorkSheet() {
        return workSheet;
    }
}
