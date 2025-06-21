package com.example.bankingsystem.repository.impl;

import com.example.bankingsystem.model.Account;
import com.example.bankingsystem.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepositoryImpl implements AccountRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Account> accountRowMapper = (rs, rowNum) -> new Account(
            rs.getString("id"),
            rs.getInt("creation_timestamp"),
            rs.getInt("balance"),
            rs.getInt("total_outgoing")
    );

    @Autowired
    public AccountRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Boolean save(int timeStamp, String accountId) {
        String sql = "INSERT INTO ACCOUNTS(id, " +
                "creation_timestamp) VALUES (?,?)";
        try {
            int rowsAffected = jdbcTemplate.update(sql,
                    accountId,
                    timeStamp);
            return rowsAffected >0;
        } catch (Exception e) {
            return false;
        }
    }
}
