package utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CSVTestDataFilter {
    private static List<Map<String, String>> allData;

    static {
        // Load CSV data once when the class is initialized
        allData = CSVReaderUtil.readCSVData("User_Data_Rest_Assured.csv");
    }

    public static List<Map<String, String>> getDataByRequestName(String requestName) {
        return allData.stream()
                .filter(row -> row.get("RequestName").equalsIgnoreCase(requestName))
                .collect(Collectors.toList());
    }
}

