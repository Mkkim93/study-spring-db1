package com.dbcon.repository;

import com.dbcon.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;

@Slf4j
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void MoneyInOut(Member toMember, Member fromMember) throws SQLException {
        int count = 0;
        Connection con = null;
        PreparedStatement pstmt = null;
        String sqlToMember = "update member set money = money - ? where member_id = ?";
        String sqlFromMember = "update member set money = money + ? where member_id = ?";

        try {
            con = getConnection();
            con.setAutoCommit(false);

            if (!checkedMemberId(con, fromMember.getMemberId()) || !checkedMemberId(con, toMember.getMemberId())) {
                System.exit(count);
                throw new SQLException("One or both member IDs do not exist.");
            }

            pstmt = con.prepareStatement(sqlToMember);
            pstmt.setInt(1, toMember.getMoney());
            pstmt.setString(2, toMember.getMemberId());
            pstmt.executeUpdate();

            pstmt = con.prepareStatement(sqlFromMember);
            pstmt.setInt(1, fromMember.getMoney());
            pstmt.setString(2, fromMember.getMemberId());
            pstmt.executeUpdate();

            con.commit();

        } catch (SQLException e) {
            if (con != null) {
                con.rollback();
            }
            log.error("Cash In Out Error", e);
            throw e;
        }
    }

    public boolean checkedMemberId(Connection con, String memberId) throws SQLException {
        String sqlCheckMemberId = "select count(*) from member where member_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sqlCheckMemberId)){
          pstmt.setString(1, memberId);
          try(ResultSet rs = pstmt.executeQuery()) {
              if (rs.next()) {
                  return rs.getBoolean(1);
              }
          }
        }
        return false;
    }

    public Member findById(String memberId) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        String sql = "select * from member where member_id = ?";
        int count = 0;
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();

        } finally {
            close(con, pstmt, null);
        }
        return null;
    }

    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeConnection(con);
        JdbcUtils.closeStatement(stmt);
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                log.error("stmt close error", e);
                throw new RuntimeException(e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.error("con close error", e);
                throw new RuntimeException(e);
            }
        }

        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.error("rs close error", e);
                throw new RuntimeException(e);
            }
        }
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}
