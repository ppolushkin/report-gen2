# Report-gen2
Второй генератор отчетов. Он использует **report-base.xml** как входной файл и генерирует pdf отчеты.

## Build
`mvnw clean package`

## Develop
- Make sure `src/resource/application.properties` is correct and `report-base.xls` is on place.
- Run Application main() or `mvnw spring-boot:run`

## Update reports
- Reports are located in `resources` folder, [Jaspersoft® Studio 6.20.1](https://community.jaspersoft.com/project/jasperreports-library/releases) was used

## Install and Use
[prod/README.md](prod/README.md)