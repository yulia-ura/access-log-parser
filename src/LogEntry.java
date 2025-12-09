import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogEntry {
    final String ipAddress;
    final LocalDateTime dateTime;
    final HttpMethod httpMethod;
    final String path;
    final int responseCode;
    final int responseSize;
    final String referer;
    final UserAgent userAgent;

    public LogEntry(String logEntry) {
        // Разделяем строку лога по кавычкам для парсинга
        String[] parts = logEntry.split("\"");
        String firstPart = parts[0];

        // Извлекаем IP-адрес
        int firstSpaceIndex = firstPart.indexOf(' ');
        String ipAddress = firstPart.substring(0, firstSpaceIndex);

        // Извлекаем дату и время
        int bracketStart = firstPart.indexOf('[');
        int bracketEnd = firstPart.indexOf(']');
        String dateTimeString = firstPart.substring(bracketStart + 1, bracketEnd);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z");
        LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, formatter);

        this.ipAddress = ipAddress;
        this.dateTime = dateTime;


        // Извлекаем строку User-Agent
        String userAgentString = "";
        int lastQuoteIndex = logEntry.lastIndexOf('"');
        int secondLastQuoteIndex = logEntry.lastIndexOf('"', lastQuoteIndex - 1);

        if (lastQuoteIndex != -1 && secondLastQuoteIndex != -1 && lastQuoteIndex > secondLastQuoteIndex) {
            userAgentString = logEntry.substring(secondLastQuoteIndex + 1, lastQuoteIndex).trim();
        }

        this.userAgent = new UserAgent(userAgentString);

        // Парсим основную информацию о запросе: метод, путь, код ответа, размер и referer
        if (parts.length >= 3) {
            String methodAndPath = parts[1].trim();
            String[] methodPathParts = methodAndPath.split("\\s+");

            if (methodPathParts.length >= 2) {
                this.path = methodPathParts[1];
            } else {
                this.path = "";
            }

            HttpMethod method = HttpMethod.GET;
            if (methodPathParts.length > 0) {
                try {
                    method = HttpMethod.valueOf(methodPathParts[0].toUpperCase());
                } catch (IllegalArgumentException e) {
                    method = HttpMethod.OTHER;
                }
            }

            this.httpMethod = method;

            String responseCodeAndDataSize = parts[2].trim();
            String[] responseCodeParts = responseCodeAndDataSize.split("\\s+");
            if (responseCodeParts.length >= 2) {
                this.responseCode = Integer.parseInt(responseCodeParts[0]);
                this.responseSize = Integer.parseInt(responseCodeParts[1]);
            } else {
                this.responseCode = 0;
                this.responseSize = 0;
            }

            if (parts.length > 3) {
                this.referer = parts[3].trim();
            } else {
                this.referer = "-";
            }
        } else {
            // Инициализация по умолчанию для некорректных строк лога
            this.httpMethod = HttpMethod.GET;
            this.path = "";
            this.responseCode = 0;
            this.responseSize = 0;
            this.referer = "-";
        }
    }

    // Геттеры для всех полей
    public String getIpAddress() {
        return ipAddress;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public int getResponseSize() {
        return responseSize;
    }

    public String getReferer() {
        return referer;
    }

    public UserAgent getUserAgent() {
        return userAgent;
    }
}
