import java.time.LocalDateTime;

public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
    }

    public void addEntry(LogEntry entry) {
        totalTraffic += entry.getResponseSize();

        LocalDateTime entryTime = entry.getDateTime();
        if (entryTime.isBefore(minTime)) {
            minTime = entryTime;
        }

        if (entryTime.isAfter(maxTime)) {
            maxTime = entryTime;
        }
    }

    public double getTrafficRate() {
        long hoursBetween = java.time.Duration.between(minTime, maxTime).toHours();

        if (hoursBetween == 0) {
            return 0.0;
        }
        return (double) totalTraffic / hoursBetween;
    }
}
