package com.souf.karate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DbUtils{

    private static JdbcTemplate jdbc;

    public static void initialize(Map<String, Object> config) {
        if (UtilsConstants.VALIDATE_DB && !UtilsConstants.DB_INITIALIZED){
            UtilsConstants.DB_INITIALIZED = true;
            String url = (String) config.get("url");
            String username = (String) config.get("user");
            String password = (String) config.get("pass");
            String driver = (String) config.get("driver");
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(driver);
            dataSource.setUrl(url);
            dataSource.setUsername(username);
            dataSource.setPassword(password);
            jdbc = new JdbcTemplate(dataSource);
        }
    }

    public static Map<String, Object> validateDBEvents(String tranId, List<Integer> expectedEvents){
        Map<String, Object> returnedMap = new HashMap<>();
        if (UtilsConstants.VALIDATE_DB){
            List<Map<String, Object>> rows = jdbc.queryForList("select * from events where TRAN_ID = '" + tranId + "'");
            List<Integer> eventsIds = jdbc.queryForList("SELECT EVT_ID FROM EVENTS WHERE TRAN_ID = '" + tranId + "' order by EVT_ID asc", Integer.class);
            Collections.sort(expectedEvents);
            returnedMap.put("passed",expectedEvents.equals(eventsIds));
            returnedMap.put("reason",UtilsConstants.COMPARISON_RESULT);
            returnedMap.put("Expected Event IDs",expectedEvents);
            returnedMap.put("Actual Event IDs",eventsIds);
            returnedMap.put(UtilsConstants.FULL_DATA_KEY,rows);
        }else{
            returnedMap.put("passed",true);
            returnedMap.put("reason",UtilsConstants.VALIDATION_SKIPPED);
        }
        return returnedMap;
    }

}
