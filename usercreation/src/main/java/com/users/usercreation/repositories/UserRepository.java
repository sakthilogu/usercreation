package com.users.usercreation.repositories;

import com.users.usercreation.domain.User;
import com.users.usercreation.exceptions.EtAuthException;
import com.users.usercreation.exceptions.UaResourceNotFoundException;

import java.util.List;

public interface UserRepository {

    Integer create(String firstName, String lastName, String email, String password) throws EtAuthException;

    User findByEmailAndPassword(String email, String password) throws EtAuthException;

    Integer getCountByEmail(String email);

    User findById(Integer userId);
    void update(Integer userId,User user);
    List<User> findAll();
    void removeById(Integer userId) throws UaResourceNotFoundException;

}
