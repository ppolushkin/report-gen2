package io.github.ppolushkin;

import io.github.ppolushkin.domain.ExcelReader;
import io.github.ppolushkin.domain.ReportData;
import io.github.ppolushkin.domain.TemplateService;
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

    @Value("${outputFolder}")
    private String outputFolder;

    @Autowired
    private ExcelReader excelReader;

    @Autowired
    private TemplateService templateService;

    @PostConstruct
    private void init() {
        logger.log(Level.INFO, "EXCEL LOCATION " + excelLocation);
        logger.log(Level.INFO, "SHEET NAME " + sheetName);
        logger.log(Level.INFO, "START LINE " + startLine);
        logger.log(Level.INFO, "OUTPUT FOLDER " + outputFolder);
    }

    @Override
    public void run(String... args) throws Exception {

        excelReader.loadWorkBook(excelLocation, sheetName);

        for (int patientNum = 0; patientNum < 17; patientNum++) {
            ReportData reportData = getReportData(patientNum);
            generateReport(reportData);
        }

    }

    private ReportData getReportData(int patientNum) {

        ReportData reportData = new ReportData();
        reportData.tester = excelReader.readString("B" + startLine);

        int patientLine = (startLine + 2) + patientNum * 6;
        reportData.patient = excelReader.readString("B" + patientLine);
        reportData.sex = excelReader.readString("A" + (patientLine + 1));
        reportData.birthdate = excelReader.readString("B" + (patientLine + 1));
        reportData.testdate = excelReader.readString("B" + (patientLine + 2));
        reportData.department = excelReader.readString("B" + (patientLine + 3));
        reportData.doctor = excelReader.readString("B" + (patientLine + 4));
        reportData.abl = excelReader.readDouble("E" + (patientLine + 4));

        if (reportData.abl < 10_000) {
            reportData.reAnalyze = true;
        } else if (reportData.abl >= 10_000 && reportData.abl < 32_000) {
            reportData.sensity = "4";
        } else if (reportData.abl >= 32_000 && reportData.abl < 100_000) {
            reportData.sensity = "4,5";
        } else {
            reportData.sensity = "5";
        }

        if (!reportData.reAnalyze) {
            reportData.bcr = excelReader.readDouble("C" + (patientLine + 4));
            reportData.percent = excelReader.readDouble("G" + patientLine);
            reportData.reached = reportData.percent <= 0.1;
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
        if (reportData.reAnalyze) {
            templateService.replace("ABLNUM", "");
            templateService.replace("BCRNUM", "");
            templateService.replace("SENSITY", "");
            templateService.replace("REACHED", "");

            templateService.replace("PERCENT", "перебрать материал (концентрация гена-нормализатора ABL менее 10000 копий/реакция).");
        } else {
            templateService.replace("ABLNUM", "" + Math.round(reportData.abl));
            templateService.replace("BCRNUM", "" + Math.round(reportData.bcr));
            templateService.replace("SENSITY", reportData.sensity);
            templateService.replace("REACHED", reportData.reached ? "достигнут" : "не достигнут");
            templateService.replace("PERCENT", String.format("равна %.3f %% (IS).", reportData.percent));
        }
        templateService.replace("TODAY", LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.uuuu")));
        templateService.save(outputFolder, reportData.patient + " RQ от " + reportData.testdate + ".doc");
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}