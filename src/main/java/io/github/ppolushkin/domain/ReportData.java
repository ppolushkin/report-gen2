package io.github.ppolushkin.domain;

import lombok.Data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel Polushkin
 * 29.06.2017.
 */
@Data
public class ReportData {

    public final static String NO_JAK2_NO_9_22_COMMENT_TEXT = "    1. Отрицательный результат определения мутации V617F в 14 экзоне гена JAK2 не исключает наличие других драйверных мутаций, характерных для МПН: в 12 экзоне гена JAK2, 9 экзоне гена CALR и 515 кодоне гена MPL.\n" +
            "    2. Учитывая отрицательный результат определения наиболее часто встречающихся транскриптов гена BCR-ABL b2a2, b3a2 (белок p210) и e1a2, e1a3 (белок p190), диагноз ХМЛ представляется маловероятным. Однако, при наличии типичной клинической картины ХМЛ, для окончательного исключения диагноза рекомендовано проведение цитогенетического анализа клеток костного мозга на наличие транслокации t(9;22)(q34;q11), а также молекулярно-генетических исследований (FISH-анализа и ПЦР) для выявления редких транскриптов гена BCR-ABL (белка р230 и других).";

    public final static String HAS_JAK2_NO_9_22_COMMENT_TEXT = "Учитывая отрицательный результат определения наиболее часто встречающихся транскриптов гена BCR-ABL b2a2, b3a2 (белок p210) и e1a2, e1a3 (белок p190), диагноз ХМЛ представляется маловероятным. Однако, при наличии типичной клинической картины ХМЛ, для окончательного исключения диагноза рекомендовано проведение цитогенетического анализа клеток костного мозга на наличие транслокации t(9;22)(q34;q11), а также молекулярно-генетических исследований (FISH-анализа и ПЦР) для выявления редких транскриптов гена BCR-ABL (белка р230 и других).";

    public Integer lineNumber;
    public String commonLogNumber;
    public String dnaNumber;
    public String rnaNumber;

    public String patient;
    public String sex;
    public String diagnosis;
    public String birthdate;
    public String testdate;
    public String department;
    public String doctor;
    public List<GenTest> genTests = new ArrayList<>();
    public String tester;
    public String material;
    public String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.uuuu"));

    public boolean has_jak2;
    public boolean has_calr;

    public boolean show_comment;
    public String comment;

    public boolean show_summary;
    public String summary;

    public void addGenTest(String description, String shortDescription, String result, boolean print) {
        genTests.add(new GenTest(description, shortDescription, result, print));
    }

}

