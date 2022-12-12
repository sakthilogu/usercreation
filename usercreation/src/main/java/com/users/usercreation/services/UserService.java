package com.users.usercreation.services;

import com.users.usercreation.domain.User;
import com.users.usercreation.exceptions.EtAuthException;
import com.users.usercreation.exceptions.UaResourceNotFoundException;

import java.util.List;

public interface UserService {

    User validateUser(String email, String password) throws EtAuthException;

    User registerUser(String firstName, String lastName, String email, String password) throws EtAuthException;
    void updateUser(Integer userId,User user)throws EtAuthException;
    List<User> fetchAllUsers();
    User fetchUserById(Integer userId) throws UaResourceNotFoundException;
    void removeUser(Integer userId) throws UaResourceNotFoundException;

}
