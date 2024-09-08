package com.dbcon.connection;

import com.dbcon.repository.MemberRepositoryV3;
import com.dbcon.service.MemberServiceV3_3;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.relational.core.sql.SQL;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.dbcon.connection.ConnectionConst.*;
@Slf4j
public class DBConnectionUtil {


    public static Connection getConnection() {

        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            log.info("get connection={}, class={}", connection, connection.getClass());
            return connection;
        } catch(SQLException e) {
            throw new IllegalStateException(e);
        }
    }
}
