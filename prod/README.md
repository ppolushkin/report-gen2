# Report-gen2

## Установка
- сохранить https://raw.githubusercontent.com/ppolushkin/report-gen2/master/prod/install.ps1 в директории
- запустить install.ps1 с помощью powerShell (правая кнопка мыши -> выполнить с помощью)

## Использование

Перед тем как запустить нужно настроить параметры в **application.properties**:

- `excelLocation` - путь где лежит база. К сожалению, кирилица пока не работает.
- `sheetNumber` - номер Листа в базе (excel)
- `startLine` - линия с которой начинаем
- `endLine` - линия на которой заканчиваем (включительно)
- `outputFolder` - директория в которую будут записаны результаты. Ее нужно заранее создать.

Чтобы запустить программу, нужно выполнить generate2.bat

### Известные проблемы:
- java.lang.OutOfMemoryError - убедитесь, что в `generate2.bat` отсутствует ключ -Xmx
- EmptyFileException - проверьте путь `excelLocation`