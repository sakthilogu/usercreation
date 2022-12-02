package com.users.usercreation.resources;

import com.users.usercreation.Constants;
import com.users.usercreation.domain.User;
import com.users.usercreation.services.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserResource {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> loginUser(@RequestBody Map<String, Object> userMap) {
        String email = (String) userMap.get("email");
        String password = (String) userMap.get("password");
        User user = userService.validateUser(email, password);
        return new ResponseEntity<>(generateJWTToken(user), HttpStatus.OK);
    }
    @PutMapping("/{userId}")
    public ResponseEntity<Map<String, Boolean>> updateUser(HttpServletRequest request, @PathVariable("userId") Integer userId, @RequestBody User user) {
       // System.out.println((String)request.getAttribute("email")+ (String)request.getAttribute("password")+(Integer) request.getAttribute("userId"));

        //User userr = userService.validateUser((String)request.getAttribute("email"), (String)request.getAttribute("password"));
              // System.out.println((String)request.getAttribute("email")+ (String)request.getAttribute("password"));

        //int userIdd = (Integer) request.getAttribute("userId");
        userService.updateUser(userId, user);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
        //return new ResponseEntity<>(generateJWTToken(user), HttpStatus.OK);
    }
    @GetMapping("")
    public ResponseEntity<List<User>> getAllUsers(HttpServletRequest request) {
        List<User> users = userService.fetchAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserById(HttpServletRequest request, @PathVariable("userId") Integer userId) {
        //int userId = (Integer) request.getAttribute("userId");
        User transaction = userService.fetchUserById(userId);
        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }
    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody Map<String, Object> userMap) {
        String firstName = (String) userMap.get("firstName");
        String lastName = (String) userMap.get("lastName");
        String email = (String) userMap.get("email");
        String password = (String) userMap.get("password");
        User user = userService.registerUser(firstName, lastName, email, password);
        return new ResponseEntity<>(generateJWTToken(user), HttpStatus.OK);
    }
    @DeleteMapping("/{userId}")
    public ResponseEntity<Map<String, Boolean>> deleteUser(HttpServletRequest request,
                                                                  @PathVariable("userId") Integer userId) {
       // int userId = (Integer) request.getAttribute("userId");
        userService.removeUser(userId);
        Map<String, Boolean> map = new HashMap<>();
        map.put("success", true);
        return new ResponseEntity<>(map, HttpStatus.OK);
    }
    private Map<String, String> generateJWTToken(User user) {
        long timestamp = System.currentTimeMillis();
        String token = Jwts.builder().signWith(SignatureAlgorithm.HS256, Constants.API_SECRET_KEY)
                .setIssuedAt(new Date(timestamp))
                .setExpiration(new Date(timestamp + Constants.TOKEN_VALIDITY))
                .claim("userId", user.getUserId())
                .claim("email", user.getEmail())
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .compact();
        Map<String, String> map = new HashMap<>();
        map.put("token", token);
        return map;
    }

}