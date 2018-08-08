package io.github.ppolushkin;

import io.github.ppolushkin.domain.ExcelReader;
import io.github.ppolushkin.domain.GenTest;
import io.github.ppolushkin.domain.ReportData;
import io.github.ppolushkin.domain.TemplateService;
import org.apache.poi.ss.util.CellReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.github.ppolushkin.domain.ReportData.NO_9_22_COMMENT_TEXT;

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
    @Qualifier(value = "PdfTemplateService")
//    @Qualifier(value = "RtfTemplateService")
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

        for (int line = startLine; line <= endLine; line += 3) {
            boolean printPatient = !("" + excelReader.readString("I" + line)).trim().isEmpty();
            if (printPatient) {
                ReportData reportData = getReportData(line);
                generateReport(reportData);
            } else {
                logger.log(Level.INFO, "Пропускаю пациента на линнии " + line);
            }

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
        reportData.diagnosis = excelReader.readString("D" + (lineNumber + 1));
        reportData.department = excelReader.readString("E" + (lineNumber + 1));
        reportData.testdate = excelReader.readDate("C" + (lineNumber + 2));
        reportData.material = excelReader.readString("D" + (lineNumber + 2));
        reportData.doctor = excelReader.readString("E" + (lineNumber + 2));

        int col = FIRST_GEN_COL;

        String genTestDescription;
        while (!(genTestDescription = excelReader.readString(col, FIRST_LINE)).isEmpty()) {
            String genTestShortDescription = excelReader.readString(col, FIRST_LINE + 1);
            if (genTestShortDescription.equalsIgnoreCase("Исследователь")) {
                break;
            }
            String result = excelReader.readString(col, lineNumber);
            boolean print = !("" + excelReader.readString(col + 1, lineNumber)).trim().isEmpty();
            reportData.addGenTest(genTestDescription, genTestShortDescription, result, print);
            col += 2;
        }

        for (int i = 0; i < 200; i++) {
            if (excelReader.readString(FIRST_GEN_COL + i, FIRST_LINE).equalsIgnoreCase("Исследователь")) {
                reportData.tester = excelReader.readString(FIRST_GEN_COL + i, lineNumber);
                reportData.summary = excelReader.readString(FIRST_GEN_COL + i + 1, lineNumber);
                break;
            }
        }

        if (reportData.tester == null) {
            logger.log(Level.SEVERE, "Ячейка Исследователь не найдена! Она должна быть в первой строчке, например по адресу BF1.");
        }

        return reportData;
    }

    private void generateReport(ReportData reportData) throws Exception {
        //
        // Игнорируем результаты которые не идут в бланк
        //
        reportData.genTests.removeIf(t -> !t.isPrint());

        reportData.show_summary = !reportData.summary.isEmpty();
        reportData.has_jak2 = has(reportData, "JAK-2");
        reportData.has_calr = has(reportData, "CALR");

        boolean has_t9_22 = has(reportData, "t(9 22)");
        boolean has_t9_22_p230 = has(reportData, "t(9 22)", "p230");
        boolean not_found_jak2 = !found(reportData, "JAK-2") && !redo(reportData, "JAK-2");     // не обнаружено и не надо перебирать материал
        boolean not_found_9_22 = !found(reportData, "t(9 22)") && !redo(reportData, "t(9 22)"); // не обнаружено и не надо перебирать материал

        //
        // Комментарий про отсутствие 9_22 можно показывать только при диагнозах ХМЛ и МПН и если исследование на ген p230 не заказадно
        // Это следует из самого комментария
        //
        boolean can_show_no_9_22_comment = !has_t9_22_p230 && (reportData.diagnosis.contains("ХМЛ") || reportData.diagnosis.contains("МПН"));

        if (reportData.has_jak2 && has_t9_22) { // Заказаны t(9 22) и JAK-2
            if (not_found_jak2 && not_found_9_22) {
                reportData.show_comment = true;
                if (can_show_no_9_22_comment) {
                    reportData.comment = ReportData.NO_JAK2_NO_9_22_COMMENT_TEXT;
                } else {
                    reportData.comment = ReportData.NO_JAK2_COMMENT_TEXT;
                }
            }
            if (!not_found_jak2 && not_found_9_22 && can_show_no_9_22_comment) {
                reportData.show_comment = true;
                reportData.comment = NO_9_22_COMMENT_TEXT;
            }
            if (not_found_jak2 && !not_found_9_22) {
                //do nothing
            }
            if (!not_found_jak2 && !not_found_9_22) {
                //do nothing
            }

        } else if (has_t9_22) { // Заказан только t(9 22)
            if (not_found_9_22 && can_show_no_9_22_comment) {
                reportData.show_comment = true;
                reportData.comment = NO_9_22_COMMENT_TEXT;
            }
        } else if (reportData.has_jak2) { // Заказан только JAK-2
            if (not_found_jak2) {
                reportData.show_comment = true;
                reportData.comment = ReportData.NO_JAK2_COMMENT_TEXT;
            }
        }

        reportData.patientInfo = reportData.patient + ", " + reportData.sex + ", " + reportData.birthdate;
        templateService.buildReport(getTemplateName(reportData), reportData, outputFolder);
    }

    private String getTemplateName(ReportData reportData) {
        String department = reportData.department;
        if ("ХЕЛИКС".equalsIgnoreCase(department) || "ИНВИТРО".equalsIgnoreCase(department)) {
            return "no_name_report.jrxml";
        } else {
            return "report.jrxml";
        }
    }

    /**
     * Есть ли анализ в отчете?
     */
    private boolean has(ReportData reportData, String shortDescritption) {
        return reportData.genTests.stream().anyMatch(gt -> shortDescritption.equals(gt.getShortDescription()));
    }

    /**
     * Есть ли анализ в отчете?
     */
    private boolean has(ReportData reportData, String shortDescritption, String description) {
        return reportData.genTests.stream().anyMatch(gt -> shortDescritption.equals(gt.getShortDescription()) && gt.getDescription().contains(description));
    }

    /**
     * Найдена ли мутация?
     */
    private boolean found(final ReportData reportData, final String shortDescription) {
        return reportData.genTests.stream().anyMatch(
                gt -> shortDescription.equalsIgnoreCase(gt.getShortDescription())
                        && !gt.getResult().toUpperCase().contains("НЕ ОБНАРУЖЕН")
                        && gt.getResult().toUpperCase().contains("ОБНАРУЖЕН"));
    }

    /**
     * Надо ли перебрать материал?
     */
    private boolean redo(final ReportData reportData, final String shortDescription) {
        return reportData.genTests.stream().anyMatch(
                gt -> shortDescription.equalsIgnoreCase(gt.getShortDescription())
                        && gt.getResult().toUpperCase().contains("ПЕРЕБРАТЬ"));
    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);
    }

}