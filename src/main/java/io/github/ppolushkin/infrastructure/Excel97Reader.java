package io.github.ppolushkin.infrastructure;

import io.github.ppolushkin.domain.ExcelReader;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Pavel Polushkin
 * 28.06.2017.
 */
@Service
public class Excel97Reader implements ExcelReader {

    private Workbook workbook;

    private String workSheet;

    @Override
    public void loadWorkBook(String fileLocation, String workSheet) throws IOException {
        if (workbook != null) {
            throw new IllegalArgumentException("Workbook already opened");
        }
        this.workbook = new HSSFWorkbook(new POIFSFileSystem(new File(fileLocation)));
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
