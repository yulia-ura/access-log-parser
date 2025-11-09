import java.io.File;
import java.util.Scanner;

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
        }
    }
}
