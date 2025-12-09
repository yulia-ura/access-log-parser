import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

// Исключение для строк длиннее 1024 символов (Задание #1)
class VeryLongLineException extends RuntimeException {
    public VeryLongLineException(String message) {
        super(message);
    }
}

public class Main {
    public static void main(String[] args) {

        int correctFileCount = 0;

        // Основной цикл программы - обработка нескольких файлов
        while (true) {
            // Ввод пути к файлу
            System.out.println("Введите путь к файлу: ");
            String path = new Scanner(System.in).nextLine();

            // Проверка существования файла и что это не папка
            File file = new File(path);
            boolean fileExists = file.exists();

            boolean isDirectory = file.isDirectory();

            if (!fileExists) {
                System.out.println("Файл не существует: " + path);
                continue;
            }

            if (isDirectory) {
                System.out.println("Указанный путь ведет к папке, а не к файлу: " + path);
                continue;
            }

            // Файл корректен, начинаем обработку
            System.out.println("Путь указан верно");
            correctFileCount++;
            System.out.println("Это файл номер " + correctFileCount);

            // Переменные для статистики
            int lineCounter = 0; // Общее количество строк
            int googleBotCount = 0; // Счетчик Googlebot (Задание #2)
            int yandexBotCount = 0; // Счетчик YandexBot (Задание #2)

            Statistics statistics = new Statistics();// Для статистики трафика (Задание #3)

            try {
                // Открытие и чтение файла
                FileReader fileReader = new FileReader(path);
                BufferedReader reader = new BufferedReader(fileReader);
                String line;

                // Построчная обработка файла
                while ((line = reader.readLine()) != null) {
                    int length = line.length();

                    // Проверка длины строки (Задание #1)
                    if (length > 1024) {
                        throw new VeryLongLineException("Строка превышает 1024 символа");
                    }
                    lineCounter++;

                    // Создание LogEntry и сбор статистики трафика (Задание #3)
                    LogEntry entry = new LogEntry(line);
                    statistics.addEntry(entry);

                    // Анализ User-Agent для поиска поисковых ботов (Задание #2)
                    String[] parts = line.split("\"");
                    if (parts.length >= 5) {
                        String userAgent = parts[parts.length - 1];

                        int start = userAgent.indexOf("(compatible;");
                        int end = userAgent.indexOf(")", start);

                        if (start != -1 && end != -1 && end > start) {
                            String firstBrackets = userAgent.substring(start + 1, end);

                            String[] subParts = firstBrackets.split(";");

                            if (subParts.length >= 2) {
                                String fragment = subParts[1].trim();
                                String[] slashParts = fragment.split("/");

                                if (slashParts.length > 0) {
                                    String searchBot = slashParts[0].trim();

                                    if (searchBot.equals("Googlebot")) {
                                        googleBotCount++;
                                    } else if (searchBot.equals("YandexBot")) {
                                        yandexBotCount++;
                                    }
                                }
                            }
                        }
                    }
                }
                reader.close();

            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Вывод результатов
            System.out.println("Общее количество строк в файле: " + lineCounter);

            if (lineCounter > 0) {
                System.out.println("Доля Googlebot: " + (googleBotCount * 100.0) / lineCounter + "%");
                System.out.println("Доля YandexBot: " + ((yandexBotCount * 100.0) / lineCounter) + "%");
            } else {
                System.out.println("Файл пуст, доли ботов не вычислены");
            }

            // Вывод средней скорости трафика (Задание #3)
            System.out.println("Средняя скорость трафика: " + statistics.getTrafficRate() + " байт/час");
        }
    }
}