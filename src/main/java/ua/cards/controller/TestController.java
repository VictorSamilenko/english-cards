package ua.cards.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import ua.cards.model.Group;
import ua.cards.model.User;
import ua.cards.service.GroupService;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Controller
@Transactional
@RequestMapping("/")
public class TestController {

    @Autowired
    GroupService groupService;

    @RequestMapping(method = RequestMethod.GET)
    public String test(HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        Set<Group> groups = groupService.get(user);
        request.setAttribute("groups", groups);
        return "index";
    }
}
