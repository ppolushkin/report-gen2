#Второй генератор отчетов




Перед тем как запустить нужно настроить параметры в application.properties

excelLocation - путь где лежит база. К сожалению, кирилица пока не работает.
sheetNumber - номер Листа в базе (excel)
startLine - зеленая линия, с которой начинается серия. См. картинку.
outputFolder - директория в которую будут записаны результаты. Ее нужно заранее создать.

В картинке есть пример заполнения. На зеленой линии должен быть указан лаборант.
Первая дата - дата рождения, затем идет дата забора анализов.

Чтобы запустить программу, нужно выполнить generate.bat

# For development
1) Copy Real-Time.xls to C:/TEMP/Real-Time.xls   
2) Run Application main()

Note: settings are in application.properties
