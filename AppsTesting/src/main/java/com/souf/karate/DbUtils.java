package com.souf.karate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.*;

public class DbUtils{

    private static Map<String,JdbcTemplate> jdbcTemplateMap = new HashMap<>();

    public static void initialize(Map<String, Object> config) {
        if (UtilsConstants.VALIDATE_DB){
            List<String> apps = Arrays.asList(System.getProperty("apps").split(","));
            if (apps.contains(UtilsConstants.APP_A_NAME)){
                startJdbcTemplate(UtilsConstants.APP_A_NAME,
                        (String) config.get("urlAppA"), (String) config.get("userAppA"),
                        (String) config.get("passAppA"),(String) config.get("driverAppA")
                );
            }
            if (apps.contains(UtilsConstants.APP_B_NAME)){
                startJdbcTemplate(UtilsConstants.APP_B_NAME,
                        (String) config.get("urlAppB"), (String) config.get("userAppB"),
                        (String) config.get("passAppB"),(String) config.get("driverAppB")
                );
            }
            if (apps.contains(UtilsConstants.APP_C_NAME)){
                startJdbcTemplate(UtilsConstants.APP_C_NAME,
                        (String) config.get("urlAppC"), (String) config.get("userAppC"),
                        (String) config.get("passAppC"),(String) config.get("driverAppC")
                );
            }
        }
    }

    private static void startJdbcTemplate(String appName, String url, String user, String pass,String driver){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(pass);
        jdbcTemplateMap.put(appName,new JdbcTemplate(dataSource));
    }

    public static Map<String, Object> validateDBEvents(String appName, String tranId, List<Integer> expectedEvents){
        Map<String, Object> returnedMap = new HashMap<>();
        if (UtilsConstants.VALIDATE_DB){
            List<Map<String, Object>> rows = jdbcTemplateMap.get(appName)
                    .queryForList("select * from events where TRAN_ID = '" + tranId + "'");
            List<Integer> eventsIds = jdbcTemplateMap.get(appName)
                    .queryForList("SELECT EVT_ID FROM EVENTS WHERE TRAN_ID = '" + tranId
                            + "' order by EVT_ID asc", Integer.class);
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

    public static Map<String, Object> validateValueInDBEvent(String appName, String tranId, int eventID,
                                                             String column, String expectedValue){
        Map<String, Object> returnedMap = new HashMap<>();
        if (UtilsConstants.VALIDATE_DB){
            String query = "select " + column + " from events where TRAN_ID = '" + tranId + "' and EVT_ID=" + eventID;
            String valueToDB = jdbcTemplateMap.get(appName).queryForObject(query,String.class);
            if (expectedValue != null){
                returnedMap.put("passed",expectedValue.equals(valueToDB));
            }else{
                if (valueToDB == null){
                    returnedMap.put("passed",true);
                }else{
                    returnedMap.put("passed",false);
                }
            }
            returnedMap.put("reason",UtilsConstants.COMPARISON_RESULT);
            returnedMap.put("Expected Value",expectedValue);
        }else{
            returnedMap.put("passed",true);
            returnedMap.put("reason",UtilsConstants.VALIDATION_SKIPPED);
        }
        return returnedMap;
    }

    public static Map<String, Object> validateDBLogin(String appName, boolean isGoodLogin,
                          String tranId, int count, String expectedUsername, String expectedInfo){

        Map<String, Object> returnedMap = new HashMap<>();
        if (UtilsConstants.VALIDATE_DB){
            String tableName;
            String infoType;
            String infoColumn;
            if (isGoodLogin){
                tableName = "login_requests";
                infoType = "Expected Login Info";
                infoColumn = "LOGIN_INFO";
            }else{
                tableName = "bad_logins";
                infoType = "Expected Reason";
                infoColumn = "REASON";
            }

            String query = "select * from " + tableName + " where TRAN_ID = '" + tranId + "'";
            List<Map<String, Object>> rows = jdbcTemplateMap.get(appName).queryForList(query);
            boolean passed = true;
            if (rows.size() != count
                    || !OtherUtils.twoStringsEqual(expectedUsername,(String)rows.get(0).get("USERNAME"))
                    || !OtherUtils.twoStringsEqual(expectedInfo,(String)rows.get(0).get(infoColumn))){
                passed = false;
            }
            returnedMap.put("reason",UtilsConstants.COMPARISON_RESULT);
            returnedMap.put("Expected Username",expectedUsername);
            returnedMap.put(infoType,expectedInfo);
            returnedMap.put(UtilsConstants.FULL_DATA_KEY,rows);
            returnedMap.put("passed",passed);
        }else{
            returnedMap.put("passed",true);
            returnedMap.put("reason",UtilsConstants.VALIDATION_SKIPPED);
        }
        return returnedMap;
    }

}
