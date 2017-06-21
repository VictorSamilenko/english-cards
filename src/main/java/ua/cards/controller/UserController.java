package ua.cards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ua.cards.model.User;
import ua.cards.service.UserService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
@Transactional
public class UserController {
    @Autowired
    UserService service;

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<String> logIN(@RequestHeader("User-Agent") String userAgent,
                                        HttpServletRequest request,
                                        String login,
                                        String password) {
        User user = service.getUser(login, password);
        if (user != null) {
            String hashCode = service.createUserSession(request.getRemoteAddr(), userAgent, user);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Set-Cookie", "HashCode=" + hashCode);
            return new ResponseEntity<String>("successful", headers, HttpStatus.OK);
        }
        return new ResponseEntity<String>("Incorrect login or password", HttpStatus.UNAUTHORIZED);
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logOUT(HttpServletResponse response,
                         @CookieValue("HashCode") String hashCode,
                         @RequestParam(required = false) String path) {
        Cookie cookie = new Cookie("HashCode", "0");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        service.deleteUserSession(hashCode);
        return "redirect:/" + ((path == null) ? "" : path);
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public ResponseEntity<String> loginValidate(String login, String password, String email) {
        if (service.getUser(login) != null) {
            return new ResponseEntity<String>("User already exists", HttpStatus.FORBIDDEN);
        }
        User user = new User(login, password, email);
        user.setRole(User.UserRoles.USER);
        service.save(user);
        return new ResponseEntity<String>("Registration successful", HttpStatus.OK);
    }

    @RequestMapping(value = "/login/validate", method = RequestMethod.GET)
    public ResponseEntity<String> loginValidate(@RequestParam String login) {
        return new ResponseEntity<String>(service.getUser(login) == null ? "valid" : "invalid", HttpStatus.OK);
    }
}
