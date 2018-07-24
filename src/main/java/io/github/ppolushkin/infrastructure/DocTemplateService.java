package io.github.ppolushkin.infrastructure;

import io.github.ppolushkin.domain.GenTest;
import io.github.ppolushkin.domain.ReportData;
import io.github.ppolushkin.domain.TemplateService;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.export.Exporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

@Service
@Qualifier(value = "DocTemplateService")
public class DocTemplateService implements TemplateService {

    @Override
    public void buildReport(ReportData reportData, String outputFolder) {
        try (InputStream inputStream = new ClassPathResource("no_name_report.jrxml").getInputStream()) {
            JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);

            JasperPrint jasperPrint = JasperFillManager.fillReport(
                    jasperReport,
                    getReportParameters(reportData),
                    new JRBeanCollectionDataSource(getReportList(reportData)));

//            Exporter exporter = new JRDocxExporter();
            Exporter exporter = new JROdtExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFolder + getReportName(reportData)));
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
        return reportData.getPatient() + " " + String.join(" + ", uniqueTests) + " от " + reportData.getTestdate() + ".odt";
    }

    private List<?> getReportList(ReportData reportData) {
        return reportData.getGenTests();
    }

    private Map<String, Object> getReportParameters(ReportData reportData) throws IllegalAccessException {
        Map<String, Object> parameters = new HashMap<>();
        for (Field field : reportData.getClass().getDeclaredFields()) {
            Object value = field.get(reportData);
            parameters.put(field.getName(), value);
        }
        return parameters;
    }

}
