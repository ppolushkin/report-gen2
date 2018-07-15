package io.github.ppolushkin;

import io.github.ppolushkin.domain.ExcelReader;
import io.github.ppolushkin.domain.ReportData;
import io.github.ppolushkin.domain.TemplateService;
import org.apache.poi.ss.util.CellReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class Application implements CommandLineRunner {

    private static final Logger logger = Logger.getLogger(Application.class.getName());

    @Value("${excelLocation}")
    private String excelLocation;

    @Value("Лист${sheetNumber}")
    private String sheetName;

    @Value("${startLine}")
    private int startLine;

    @Value("${endLine}")
    private int endLine;

    @Value("${outputFolder}")
    private String outputFolder;

    private static final int FIRST_LINE = 1;

    private static final short FIRST_GEN_COL = (short) (new CellReference("J1").getCol() + 1);

    @Autowired
    private ExcelReader excelReader;

    @Autowired
    private TemplateService templateService;

    @PostConstruct
    private void init() {
        logger.log(Level.INFO, "********************************");
        logger.log(Level.INFO, "EXCEL LOCATION " + excelLocation);
        logger.log(Level.INFO, "SHEET NAME " + sheetName);
        logger.log(Level.INFO, "START LINE " + startLine);
        logger.log(Level.INFO, "END LINE   " + endLine);
        logger.log(Level.INFO, "OUTPUT FOLDER " + outputFolder);
        logger.log(Level.INFO, "********************************");
    }

    @Override
    public void run(String... args) throws Exception {

        excelReader.loadWorkBook(excelLocation, sheetName);

        for (int line = startLine; line <= endLine; line+=3) {
            ReportData reportData = getReportData(line);
//            generateReport(reportData);
        }

    }

    private ReportData getReportData(int lineNumber) {

        ReportData reportData = new ReportData();

        reportData.lineNumber = lineNumber;
        reportData.commonLogNumber = excelReader.readNumber("A" + lineNumber);
        reportData.dnaNumber = excelReader.readNumber("F" + lineNumber);
        reportData.rnaNumber = excelReader.readNumber("G" + lineNumber);

        reportData.patient = excelReader.readString("B" + lineNumber);
        reportData.sex = excelReader.readString("C" + lineNumber);
        reportData.birthdate = excelReader.readString("C" + (lineNumber + 1));
        reportData.testdate = excelReader.readString("C" + (lineNumber + 2));
        reportData.department = excelReader.readString("E" + (lineNumber + 1));
        reportData.doctor = excelReader.readString("E" + (lineNumber + 2));

        int col = FIRST_GEN_COL;

        String genTestDescription;
        while (!(genTestDescription = excelReader.readString(col, FIRST_LINE)).isEmpty()) {
            String genTestShortDescription = excelReader.readString(col, FIRST_LINE + 1);
            if (genTestShortDescription.equalsIgnoreCase("Исследователь")) {
                break;
            }
            String result = excelReader.readString(col, lineNumber);
            boolean print = !excelReader.readString(col + 1, lineNumber).isEmpty();
            reportData.addGenTest(genTestDescription, genTestShortDescription, result, print);
            col += 2;
        }

        for (int i = 0; i < 200; i++) {
            if (excelReader.readString(FIRST_GEN_COL + i, FIRST_LINE).equalsIgnoreCase("Исследователь")) {
                reportData.tester = excelReader.readString(FIRST_GEN_COL + i, lineNumber);
                break;
            }
        }

        if (reportData.tester == null) {
            logger.log(Level.SEVERE, "Ячейка Исследователь не найдена! Она должна быть в первой строчке, например по адресу BF1.");
        }

        return reportData;
    }


    private void generateReport(ReportData reportData) throws Exception {
        templateService.open("template.doc");
        templateService.replace("TESTER", reportData.tester);
        templateService.replace("PATIENT", reportData.patient);
        templateService.replace("SEX", reportData.sex);
        templateService.replace("BIRTHDATE", reportData.birthdate);
        templateService.replace("TESTDATE", reportData.testdate);
        templateService.replace("DEPARTMENT", reportData.department);
        templateService.replace("DOCTOR", reportData.doctor);
        templateService.replace("TODAY", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
        templateService.save(outputFolder, reportData.patient + " RQ от " + reportData.testdate + ".doc");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}