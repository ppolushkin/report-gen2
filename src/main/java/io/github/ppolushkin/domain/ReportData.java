package io.github.ppolushkin.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pavel Polushkin
 * 29.06.2017.
 */
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

    public static class GenTest {
        public String description;
        public String shortDescription;
        public String result;
        public boolean print;

        @Override
        public String toString() {
            return "GenTest{" +
                    "shortDescription='" + shortDescription + '\'' +
                    ", result='" + result + '\'' +
                    ", print=" + print +
                    '}';
        }
    }

    public void addGenTest(String description, String shortDescription, String result, boolean print) {
        GenTest genTest = new GenTest();
        genTest.description = description;
        genTest.shortDescription = shortDescription;
        genTest.result = result;
        genTest.print = print;

        genTests.add(genTest);
    }

}

