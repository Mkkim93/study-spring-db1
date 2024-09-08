package com.dbcon.repository;

import com.dbcon.domain.Member;

import java.sql.SQLException;

public interface MemberRepositoryEx {

    void save(Member member) throws SQLException;

    Member findById(String memberId)throws SQLException;

    void delete(String memberId)throws SQLException;

    void update(String memberId, int money)throws SQLException;
}
