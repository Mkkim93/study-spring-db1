package com.dbcon.repository;

import com.dbcon.domain.Member;

import java.sql.SQLException;

public interface MemberRepository {

    Member save(Member member);

    Member findById(String memberId);

    void delete(String memberId);

    void update(String memberId, int money);
}
