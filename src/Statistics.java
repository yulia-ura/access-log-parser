import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

public class Statistics {
    private int totalTraffic;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private Set<String> existingPages;
    private Map<String, Integer> osCounts;

    public Statistics() {
        this.totalTraffic = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
        this.existingPages = new HashSet<>();
        this.osCounts = new HashMap<>();
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

        if (entry.getResponseCode() == 200) {
            existingPages.add(entry.getPath());
        }

        String os = entry.getUserAgent().getOsType();
        osCounts.put(os, osCounts.getOrDefault(os, 0) + 1);
    }

    public double getTrafficRate() {
        long hoursBetween = java.time.Duration.between(minTime, maxTime).toHours();

        if (hoursBetween == 0) {
            return 0.0;
        }
        return (double) totalTraffic / hoursBetween;
    }

    public Set<String> getExistingPages() {
        return existingPages;
    }

    public Map<String, Double> getOsStatistics() {
        Map<String, Double> result = new HashMap<>();

        int total = 0;
        for (int count : osCounts.values()) {
            total += count;
        }

        if (total == 0) {
            return result;
        }

        for (Map.Entry<String, Integer> entry : osCounts.entrySet()) {
            String os = entry.getKey();
            int count = entry.getValue();
            double share = (double) count / total;
            result.put(os, share);
        }
        return result;
    }
}
