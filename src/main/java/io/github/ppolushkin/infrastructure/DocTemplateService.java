package io.github.ppolushkin.infrastructure;

import io.github.ppolushkin.domain.TemplateService;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Pavel Polushkin
 * 26.03.2017.
 */
@Service
public class DocTemplateService implements TemplateService {

    private HWPFDocument document;

    @Override
    public void open(String file) throws IOException {
        try (InputStream inputStream = new ClassPathResource(file).getInputStream()) {
            this.document = new HWPFDocument(new POIFSFileSystem(inputStream));
        }
    }

    @Override
    public void replace(String variable, String value) {
        replaceText(this.document, variable, value == null ? "" : value);
    }

    @Override
    public void fillBulletList(List<String> values) {

        for (int listNum = 0; listNum < 10; listNum++) {

            Range r = document.getRange();
            l1:
            for (int i = 0; i < r.numSections(); ++i) {
                Section s = r.getSection(i);
                for (int j = 0; j < s.numParagraphs(); j++) {
                    Paragraph p = s.getParagraph(j);
                    for (int k = 0; k < p.numCharacterRuns(); k++) {
                        CharacterRun run = p.getCharacterRun(k);
                        String text = run.text();
                        if (text.contains("BULLET" + listNum)) {
                            if (listNum < values.size()) {
                                run.replaceText("BULLET" + listNum, values.get(listNum));
                            } else {
                                s.getParagraph(j).delete();
                            }
                            break l1;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void save(String outputFolder, String file) throws IOException {
        try (FileOutputStream out = new FileOutputStream(new File(outputFolder, file))) {
            document.write(out);
        }
    }

    private void replaceText(HWPFDocument doc, String findText, String replaceText) {
        Range r = doc.getRange();
        for (int i = 0; i < r.numSections(); ++i) {
            Section s = r.getSection(i);
            for (int j = 0; j < s.numParagraphs(); j++) {
                Paragraph p = s.getParagraph(j);
                for (int k = 0; k < p.numCharacterRuns(); k++) {
                    CharacterRun run = p.getCharacterRun(k);
                    String text = run.text();
                    if (text.contains(findText)) {
                        run.replaceText(findText, replaceText);
                    }
                }
            }
        }
    }

}
