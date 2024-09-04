package com.dbcon.repository;

import com.dbcon.domain.Member;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static com.dbcon.connection.ConnectionConst.*;

class MemberRepository2Test {

    private MemberRepositoryV1 repository;
    private static final int SetMoney = 1000;

    @BeforeEach
    void beforeEach() {
        // 기본 DriverManager - 항상 새로운 커넥션을 획득
        // DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    void MoneyInOut() throws SQLException {
        // int memberIdChecked1 = 0;
        // int memberIdChecked2 = 0;
        Member fromMember = new Member("newId1", SetMoney);
        Member toMember = new Member("newId2", SetMoney);

        /*memberIdChecked1 = repository.checkedMemberId(toMember.getMemberId());
        memberIdChecked2 = repository.checkedMemberId(fromMember.getMemberId());
        if (memberIdChecked1 == 0 || memberIdChecked2 == 0) {
            System.exit(0);
        }*/
        repository.MoneyInOut(toMember, fromMember);

        // 실제 잔액 확인
        // Member fromMemberAfter = repository.findById(fromMember.getMemberId());
        // Member toMemberAfter = repository.findById(toMember.getMemberId());

        // assertEquals(SetMoney - fromMember.getMoney(), fromMemberAfter.getMoney());
        // assertEquals(SetMoney + toMember.getMoney(), toMemberAfter.getMoney());
    }
}