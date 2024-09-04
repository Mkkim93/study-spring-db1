package com.dbcon.repository;
/**
 * JDBC Connection Parameter 로 넘기기
 */

import com.dbcon.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV2 {

    private final DataSource dataSource;

    public MemberRepositoryV2(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Connection con, Member member) throws SQLException {
        PreparedStatement pstmt = null;
        String sql = "insert into member (member_id, money) values (? , ?)";
        try{
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public Member findById(Connection con, String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else {
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            // connection 은 여기서 닫지 않는다.
            JdbcUtils.closeResultSet(rs);
           //  JdbcUtils.closeConnection(con);
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public void delete(Connection con, String memberId) throws SQLException {
        PreparedStatement pstmt = null;
        String sql = "delete from member where member_id = ?";
        try{
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtils.closeStatement(pstmt);
        }
    }

    public void update(Connection con, String memberId, int money) throws SQLException {
        PreparedStatement pstmt = null;
        String sql = "update member set money = ? where member_id = ?";
        try {
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            pstmt.executeUpdate();
        } finally {
            JdbcUtils.closeStatement(pstmt);
        }
    }



    private void close(Connection con, Statement stmt, ResultSet rs) {
        JdbcUtils.closeResultSet(rs);
        JdbcUtils.closeConnection(con);
        JdbcUtils.closeStatement(stmt);
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return con;
    }
}

