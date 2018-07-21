package io.github.ppolushkin.infrastructure;

import io.github.ppolushkin.domain.GenTest;
import io.github.ppolushkin.domain.ReportData;
import io.github.ppolushkin.domain.TemplateService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class PdfTemplateService implements TemplateService {

    @Override
    public void buildReport(ReportData reportData, String outputFolder) {

        try (InputStream inputStream = new ClassPathResource("report.jrxml").getInputStream()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    getReportParameters(reportData),
                    new JRBeanCollectionDataSource(getReportList(reportData)));

            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFolder + getReportName(reportData)));
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
            exporter.setConfiguration(configuration);
            exporter.exportReport();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getReportName(ReportData reportData) {
        TreeSet<String> uniqueTests = new TreeSet<>();
        for (GenTest genTest : reportData.getGenTests()) {
            if (genTest.isPrint()) {
                uniqueTests.add(genTest.getShortDescription());
            }
        }
        return reportData.getPatient() + " " + String.join(" + ", uniqueTests) + " от " + reportData.getTestdate() + ".pdf";

    }

    private List<?> getReportList(ReportData reportData) {
        return reportData.getGenTests();
    }

    private Map<String, Object> getReportParameters(ReportData reportData) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("parameter1", "Hi there!");
        return parameters;
    }

}
