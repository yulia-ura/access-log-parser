public class UserAgent {
    final String osType;
    final String browser;
    final String userAgentString;

    public UserAgent(String userAgentString) {
        this.userAgentString = userAgentString;


        // Определение ОС
        if (userAgentString.contains("Windows")) {
            this.osType = "Windows";
        } else if (userAgentString.contains("Mac")) {
            this.osType = "macOS";
        } else if (userAgentString.contains("Linux")) {
            this.osType = "Linux";
        } else {
            this.osType = "Other";
        }

        // Определение браузера
        if (userAgentString.contains("Edg/")) {
            this.browser = "Edge";
        } else if (userAgentString.contains("Firefox/")) {
            this.browser = "Firefox";
        } else if (userAgentString.contains("Chrome/")) {
            this.browser = "Chrome";
        } else if (userAgentString.contains("Safari/") && !userAgentString.contains("Chrome/")) {
            this.browser = "Safari";
        } else if (userAgentString.contains("OPR/") || userAgentString.contains("Opera/")) {
            this.browser = "Opera";
        } else {
            this.browser = "Other";
        }
    }

    public String getOsType() {
        return osType;
    }

    public String getBrowser() {
        return browser;
    }

    public boolean isBot() {
        if (userAgentString == null) {
            return false;
        }
        String userAgentLower = userAgentString.toLowerCase();
        return userAgentLower.contains("bot");
    }
}
