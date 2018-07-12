package io.github.ppolushkin.domain;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Pavel Polushkin
 * 29.06.2017.
 */
public interface TemplateService {

    void open(String resourceName) throws IOException;

    void replace(String variable, String value);

    void save(String outputFolder, String file) throws IOException;

}
