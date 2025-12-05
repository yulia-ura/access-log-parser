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
            int longestLine = 0;
            int shortestLine = Integer.MAX_VALUE;


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

                    if (length > longestLine) {
                        longestLine = length;
                    }

                    if (length < shortestLine) {
                        shortestLine = length;
                    }
                }
                reader.close();
            } catch (Exception ex) {
                    ex.printStackTrace();
                }

            System.out.println("Общее количество строк в файле: " + lineCounter);
            System.out.println("Длина самой длинной строки в файле: " + longestLine);
            System.out.println("Длина самой короткой строки в файле: " + shortestLine);
            }
        }
    }
