import java.time.LocalDateTime;
import java.util.*;

public class Statistics {
    private int totalTraffic;
    private long totalVisits;
    private long nonBotVisits;
    private LocalDateTime minTime;
    private LocalDateTime maxTime;
    private Set<String> existingPages;
    private Map<String, Integer> osCounts;
    private Set<String> notFoundPages;
    private Map<String, Integer> browserCounts;
    private long errorCount;
    private Set<String> uniqueNonBotIps;
    private List<LogEntry> allEntries;


    public Statistics() {
        this.totalTraffic = 0;
        this.totalVisits = 0;
        this.nonBotVisits = 0;
        this.minTime = LocalDateTime.MAX;
        this.maxTime = LocalDateTime.MIN;
        this.existingPages = new HashSet<>();
        this.osCounts = new HashMap<>();
        this.notFoundPages = new HashSet<>();
        this.browserCounts = new HashMap<>();
        this.errorCount = 0;
        this.uniqueNonBotIps = new HashSet<>();
        this.allEntries = new ArrayList<>();
    }

    public void addEntry(LogEntry entry) {
        allEntries.add(entry);
        totalVisits++;

        if(!entry.getUserAgent().isBot()) {
            nonBotVisits++;
            uniqueNonBotIps.add(entry.getIpAddress());
        }

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

        if (entry.getResponseCode() == 404) {
            notFoundPages.add(entry.getPath());
        }

        int responseCode = entry.getResponseCode();
        if (responseCode >= 400 && responseCode < 600) {
            errorCount++;
        }

        String os = entry.getUserAgent().getOsType();
        osCounts.put(os, osCounts.getOrDefault(os, 0) + 1);

        String browser = entry.getUserAgent().getBrowser();
        browserCounts.put(browser, browserCounts.getOrDefault(browser, 0) + 1);
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

    public Set<String> getNotFoundPages() {
        return notFoundPages;
    }

    public Map<String, Double> getBrowserStatistics() {
        Map<String, Double> result = new HashMap<>();

        int total = 0;
        for (int count : browserCounts.values()) {
            total += count;
        }

        if (total == 0) {
            return result;
        }

        for (Map.Entry<String, Integer> entry : browserCounts.entrySet()) {
            String browser = entry.getKey();
            int count = entry.getValue();
            double share = (double) count / total;
            result.put(browser, share);
        }
        return result;
    }

    public double getAverageVisitsPerHour() {
        if (allEntries.isEmpty()) {
            return 0.0;
        }
        LocalDateTime earliest = allEntries.stream()
                .map(LogEntry::getDateTime)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        LocalDateTime latest = allEntries.stream()
                .map(LogEntry::getDateTime)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        long hours = java.time.Duration.between(earliest, latest).toHours();
        if (hours == 0) {
            hours = 1;
        }

        long nonBotVisitsCount = allEntries.stream()
                .filter(entry -> !entry.getUserAgent().isBot())
                .count();

        return (double) nonBotVisitsCount / hours;
    }


    public double getAveragePerHour() {
        if (allEntries.isEmpty()) {
            return 0.0;
        }


        LocalDateTime earliest = allEntries.stream()
                .map(LogEntry::getDateTime)
                .min(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        LocalDateTime latest = allEntries.stream()
                .map(LogEntry::getDateTime)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        long hours = java.time.Duration.between(earliest, latest).toHours();

        if (hours == 0) {
            hours = 1;
        }

        long errorCount = allEntries.stream()
                .filter(entry -> {
                    int code = entry.getResponseCode();
                    return code >= 400 && code < 600;
                })
                .count();
        if (errorCount == 0) {
            return 0.0;
        }
        return (double) hours / errorCount;
    }

    public double getAverageVisitsPerUser() {
        if (allEntries.isEmpty()) {
            return 0.0;
        }

        java.util.List<LogEntry> nonBotEntries = allEntries.stream()
                .filter(entry -> !entry.getUserAgent().isBot())
                .collect(java.util.stream.Collectors.toList());

                if (nonBotEntries.isEmpty()) {
                    return 0.0;
                }

                long totalNonBotVisits = nonBotEntries.size();

                long uniqueUsers = nonBotEntries.stream()
                        .map(LogEntry::getIpAddress)
                        .distinct()
                        .count();

                if (uniqueUsers == 0) {
                    return 0.0;
                }
                return (double) totalNonBotVisits / uniqueUsers;
    }
    }
