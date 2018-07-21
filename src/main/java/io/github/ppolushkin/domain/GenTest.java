package io.github.ppolushkin.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GenTest {
    private String description;
    private String shortDescription;
    private String result;
    private boolean print;
}
