#Второй генератор отчетов


Перед тем как запустить нужно настроить параметры в application.properties

excelLocation - путь где лежит база. К сожалению, кирилица пока не работает.
sheetNumber - номер Листа в базе (excel)
startLine - линия, с которой програма начнет читать
endLine - линия на которой программа остановится
outputFolder - директория в которую будут записаны результаты. Ее нужно заранее создать.

Чтобы запустить программу, нужно выполнить generate.bat


# Build
`mvnw clean package`

# For development
1) Copy report-base.xls to ~/Workspace/report-base.xls
2) Run Application main() or `mvnw spring-boot:run`

Note: settings are in application.properties
