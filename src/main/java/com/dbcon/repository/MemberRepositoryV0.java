package com.dbcon.repository;

import com.dbcon.domain.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.support.JdbcUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.NoSuchElementException;

@Slf4j
public class MemberRepositoryV0 {

    private final DataSource dataSource;

    public MemberRepositoryV0(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(Member member) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        String sql = "insert into member (member_id, money) values (? , ?)";
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, member.getMemberId());
            pstmt.setInt(2, member.getMoney());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }

    public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = getConnection();
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
            close(con, pstmt, rs);
        }
    }

    public void delete(String memberId) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        String sql = "delete from member where member_id = ?";
        try{
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } finally {
            close(con, pstmt, null);
        }
    }

    public void update(String memberId, int money) throws SQLException {
        Connection con = null;
        PreparedStatement pstmt = null;
        String sql = "update member set money = ? where member_id = ?";
        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            pstmt.executeUpdate();
        } finally {
            close(con, pstmt, null);
        }
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

