import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;

class VeryLongLineException extends RuntimeException {
    public VeryLongLineException(String message) {
        super(message);
    }
}

public class Main {
    public static void main(String[] args) {

        int correctFileCount = 0;

        while (true) {
            System.out.println("Введите путь к файлу: ");
            String path = new Scanner(System.in).nextLine();

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

            System.out.println("Путь указан верно");
            correctFileCount++;
            System.out.println("Это файл номер " + correctFileCount);

            int lineCounter = 0;
            int googleBotCount = 0;
            int yandexBotCount = 0;

            try {
                FileReader fileReader = new FileReader(path);
                BufferedReader reader = new BufferedReader(fileReader);
                String line;
                while ((line = reader.readLine()) != null) {
                    int length = line.length();

                    if (length > 1024) {
                        throw new VeryLongLineException("Строка превышает 1024 символа");
                    }
                    lineCounter ++;

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

            System.out.println("Общее количество строк в файле: " + lineCounter);
            System.out.println("Доля Googlebot: " + (googleBotCount * 100.0) / lineCounter + "%");
            System.out.println("Доля YandexBot: " + ((yandexBotCount * 100.0) / lineCounter) + "%");
            }
        }
    }
