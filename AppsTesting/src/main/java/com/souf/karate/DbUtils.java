package com.souf.karate;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import java.util.List;
import java.util.Map;

public class DbUtils {

    private final JdbcTemplate jdbc;

    public DbUtils(Map<String, Object> config) {
        String url = (String) config.get("dbUrl");
        String username = (String) config.get("dbUsername");
        String password = (String) config.get("dbPassword");
        String driver = (String) config.get("dbDriver");
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        jdbc = new JdbcTemplate(dataSource);
    }
    public Object readValue(String query) {
        return jdbc.queryForObject(query, Object.class);
    }
    public Map<String, Object> readRow(String query) {
        return jdbc.queryForMap(query);
    }
    public List<Map<String, Object>> readRows(String query) {
        return jdbc.queryForList(query);
    }
    public List<Integer> readEvents(String tranId){
        String query = "SELECT EVT_ID FROM EVENTS WHERE TRAN_ID = '" + tranId + "' order by EVT_ID asc";
        return jdbc.queryForList(query,Integer.class);
    }
}
