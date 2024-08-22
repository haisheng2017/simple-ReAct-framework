package hao.simple.ai.tools;

import java.util.Locale;

public class LocalSearch {

    public String search(String keyword) {
        // TODO 都是些预先准备的问题
        if (keyword.toLowerCase(Locale.ROOT).contains("beijing")) {
            return "It is located in Northern China, and is governed as a municipality under the direct administration of the State Council with 16 urban, suburban, and rural...";
        }
        if (keyword.toLowerCase(Locale.ROOT).contains("weather")) {
            return "Beijing, Beijing, China Weather Forecast, with current conditions, wind, air quality, and what to expect for the next 3 days.";
        }
        return "Not found any related information.";
    }
}
