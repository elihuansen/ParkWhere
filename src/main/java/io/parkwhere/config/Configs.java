package io.parkwhere.config;

public class Configs {

    public final static int PORT = 1337;
    public final static int DB_PORT    = getenv("DB_PORT", 1679);
    public final static String DB_HOST = getenv("DB_HOST", "localhost");
    public final static String DB_NAME = getenv("DB_NAME", "parkwhere");
    public final static String DB_USER = getenv("DB_USER", "parkwheredev");
    public final static String DB_PASS = getenv("DB_PASS", "parkwheredev");

    public static int getenv(String key, int defaultValue) {
        int envVariable;
        try {
            envVariable = Integer.parseInt(System.getenv(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
        return envVariable;
    }

    public static String getenv(String key, String defaultValue) {
        String envVariable = System.getenv(key);
        return (envVariable != null) ? envVariable : defaultValue;
    }

    public static void bxMethod() {
        System.out.println("DO SOMETHING STUPID");
    }
}
