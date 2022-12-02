package com.users.usercreation.services;

import com.users.usercreation.domain.User;
import com.users.usercreation.exceptions.EtAuthException;
import com.users.usercreation.exceptions.UaResourceNotFoundException;
import com.users.usercreation.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public User validateUser(String email, String password) throws EtAuthException {
        if(email != null) email = email.toLowerCase();
        return userRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public User registerUser(String firstName, String lastName, String email, String password) throws EtAuthException {
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        if(email != null) email = email.toLowerCase();
        if(!pattern.matcher(email).matches())
            throw new EtAuthException("Invalid email format");
        Integer count = userRepository.getCountByEmail(email);
        if(count > 0)
            throw new EtAuthException("Email already in use");
        Integer userId = userRepository.create(firstName, lastName, email, password);
        return userRepository.findById(userId);
    }
    @Override
    public void updateUser(Integer userId, User user) throws EtAuthException {
        Pattern pattern = Pattern.compile("^(.+)@(.+)$");
        if(!pattern.matcher(user.getEmail().toLowerCase()).matches())
            throw new EtAuthException("Invalid email format");
        Integer count = userRepository.getCountByEmail(user.getEmail());
        if(count > 0)
            throw new EtAuthException("Email already in use");
        else
            userRepository.update(userId, user);

    }
    @Override
    public List<User> fetchAllUsers() {
        return userRepository.findAll();
    }
    @Override
    public User fetchUserById(Integer userId) throws UaResourceNotFoundException {
        return userRepository.findById(userId);
    }
    @Override
    public void removeUser(Integer userId) throws UaResourceNotFoundException {
        userRepository.removeById(userId);
    }
}

