package io.github.ppolushkin.domain;

/**
 * Created by Pavel Polushkin
 * 29.06.2017.
 */
public interface TemplateService {

    void buildReport(ReportData reportData, String outputFolder);

}
