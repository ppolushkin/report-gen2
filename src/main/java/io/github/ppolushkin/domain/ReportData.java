package io.github.ppolushkin.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel Polushkin
 * 29.06.2017.
 */
@Data
public class ReportData {

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

    public void addGenTest(String description, String shortDescription, String result, boolean print) {
        genTests.add(new GenTest(description, shortDescription, result, print));
    }

}

