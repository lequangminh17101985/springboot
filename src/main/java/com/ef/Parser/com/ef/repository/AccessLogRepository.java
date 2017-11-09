package com.ef.Parser.com.ef.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;
import java.util.Date;

/**
 * Created by minhle on 8/11/17.
 */

@Repository
public class AccessLogRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate ;

    public void insertBatch(final List<AccessLog> accessLogs){

        String sql = "INSERT INTO accesslog(ip, datetime, request, status, user_agent) VALUES (?,DATE_FORMAT(?,'%Y-%m-%d %H:%i:%s'),?,?,?)";

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                AccessLog accessLog = accessLogs.get(i);
                ps.setString(1, accessLog.getIp());
                ps.setString(2, accessLog.getDatetime());
                ps.setString(3, accessLog.getRequest());
                ps.setInt(4, accessLog.getStatus());
                ps.setString(5, accessLog.getUserAgent());
            }

            @Override
            public int getBatchSize() {
                return accessLogs.size();
            }
        });
    }

    public Map<String, Integer> findBlockIP(Date startDate, String duration, int threadhold ) {
        Map<String, Integer> result = new HashMap<>();

        Date endDate ;
        // convert date to calendar
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);

        if ("hourly".equalsIgnoreCase(duration)) {
            c.add(Calendar.HOUR, 1);
            endDate = c.getTime();
        }else if ("daily".equalsIgnoreCase(duration)) {
            c.add(Calendar.DATE, 1);
            endDate = c.getTime();
        }else {
            endDate = startDate;
        }
        jdbcTemplate.query(
                "Select ip, count(ip) as number_request from accesslog where `datetime`  BETWEEN DATE_FORMAT(?, '%Y-%m-%d %H:%i:%s')  " +
                        "AND  DATE_FORMAT(?, '%Y-%m-%d %H:%i:%s')\n" +
                        "group by ip \n" +
                        "HAVING count(ip) >= ? ", new Object[] {startDate, endDate, threadhold},
                (rs, rowNum) -> result.put(rs.getString("ip"), Integer.parseInt(rs.getString("number_request")))
        );

        return result;
    }

    public List<String> findRequestByIP(String ip) {
        List<String> result = jdbcTemplate.query(
                "SELECT request FROM accesslog WHERE ip = ?", new Object[] {ip},
                (rs, rowNum) -> rs.getString("request")
        );

        return result;
    }

    public void insertBlockIp (String ip, String comment) {
        jdbcTemplate.update("INSERT INTO blockip(ip, comment ) VALUES (?,?)",
                ip, comment);
    }
}
