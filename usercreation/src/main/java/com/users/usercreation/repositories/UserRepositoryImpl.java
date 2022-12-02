package com.users.usercreation.repositories;

import com.users.usercreation.domain.User;
import com.users.usercreation.exceptions.EtAuthException;
import com.users.usercreation.exceptions.UaResourceNotFoundException;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private static final String SQL_CREATE = "INSERT INTO UA_USERS(USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD) VALUES(NEXTVAL('UA_USERS_SEQ'), ?, ?, ?, ?)";
    private static final String SQL_COUNT_BY_EMAIL = "SELECT COUNT(*) FROM UA_USERS WHERE EMAIL = ?";
    private static final String SQL_FIND_BY_ID = "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD " +
            "FROM UA_USERS WHERE USER_ID = ?";
    private static final String SQL_FIND_BY_EMAIL = "SELECT USER_ID, FIRST_NAME, LAST_NAME, EMAIL, PASSWORD " +
            "FROM UA_USERS WHERE EMAIL = ?";
    private static final String SQL_UPDATE = "UPDATE UA_USERS SET  FIRST_NAME=?, LAST_NAME=?, EMAIL=?, PASSWORD=? WHERE USER_ID=?";
    private static final String SQL_FIND_ALL="SELECT * FROM UA_USERS";
    private static final String SQL_DELETE = "DELETE FROM UA_USERS WHERE USER_ID = ?";

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public Integer create(String firstName, String lastName, String email, String password) throws EtAuthException {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt(10));
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(SQL_CREATE, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                ps.setString(3, email);
                ps.setString(4, hashedPassword);
                return ps;
            }, keyHolder);
            return (Integer) keyHolder.getKeys().get("USER_ID");
        }catch (Exception e) {
            throw new EtAuthException("Invalid details. Failed to create account");
        }
    }
    @Override
    public void update(Integer userId,User user)
    {
        try {
            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(10));
            jdbcTemplate.update(SQL_UPDATE, new Object[]{user.getFirstName(), user.getLastName(), user.getEmail(), hashedPassword,userId});

        }catch (Exception e) {
            throw new EtAuthException("Invalid details. Failed to update account");
        }

    }
    @Override
    public User findByEmailAndPassword(String email, String password) throws EtAuthException {
        try {
            User user = jdbcTemplate.queryForObject(SQL_FIND_BY_EMAIL, new Object[]{email}, userRowMapper);
            if(!BCrypt.checkpw(password, user.getPassword()))
                throw new EtAuthException("Invalid email/password");
            return user;
        }catch (EmptyResultDataAccessException e) {
            throw new EtAuthException("Invalid email/password");
        }
    }

    @Override
    public Integer getCountByEmail(String email) {
        return jdbcTemplate.queryForObject(SQL_COUNT_BY_EMAIL, new Object[]{email}, Integer.class);
    }

    @Override
    public User findById(Integer userId) {
        return jdbcTemplate.queryForObject(SQL_FIND_BY_ID, new Object[]{userId}, userRowMapper);
    }

    private RowMapper<User> userRowMapper = ((rs, rowNum) -> {
        return new User(rs.getInt("USER_ID"),
                rs.getString("FIRST_NAME"),
                rs.getString("LAST_NAME"),
                rs.getString("EMAIL"),
                rs.getString("PASSWORD"));
    });
    @Override
    public List<User> findAll() throws UaResourceNotFoundException {
        return jdbcTemplate.query(SQL_FIND_ALL, userRowMapper);
    }
    @Override
    public void removeById(Integer userId) throws UaResourceNotFoundException {
        int count = jdbcTemplate.update(SQL_DELETE, new Object[]{userId});
        if(count == 0)
            throw new UaResourceNotFoundException("Transaction not found");
    }
}

