package ua.cards.controller;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ua.cards.model.Group;
import ua.cards.model.User;
import ua.cards.service.GroupService;
import ua.cards.util.DataToJSON;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

@Controller
@Transactional
@RequestMapping(value = "/groups")
public class GroupController {

    @Autowired
    GroupService service;

    @RequestMapping(method = RequestMethod.GET)
    public String getView(Model model) {
        model.addAttribute("groups", service.getAll());
        return "groups";
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ResponseEntity<String> saveGroup(HttpServletRequest request,
                                            @RequestParam("name") String name,
                                            @RequestParam("comment") String comment,
                                            @RequestParam("id") Long id) {
        User user = (User) request.getAttribute("user");
        if (user == null)
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);

        String resultAction;
        if (id == null)
            resultAction = "insert";
        else resultAction = "update";

        Group group = new Group(id, name, comment);
        service.save(user, group);

        JSONObject resultJSON = new JSONObject();
        resultJSON.put("status", HttpStatus.OK);
        resultJSON.put("action", resultAction);
        resultJSON.put("group", DataToJSON.convertGroupToJSON(group));
        return new ResponseEntity<String>(resultJSON.toJSONString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public ResponseEntity<String> getGroups(HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        Set<Group> groups = service.get(user);
        JSONObject resultJSON = new JSONObject();
        resultJSON.put("status", HttpStatus.OK);
        resultJSON.put("groups",DataToJSON.convertSetGroupsToJSON(groups));
        return new ResponseEntity<String>(resultJSON.toJSONString(), HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteGroup(HttpServletRequest request,
                                              @PathVariable Long id) {
        User user = (User) request.getAttribute("user");
        if (user == null)
            return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);

        if (user.isAdmin()) {
            service.delete(id);
            return new ResponseEntity<String>(HttpStatus.OK);
        }

        if (service.delete(user, id)) {
            return new ResponseEntity<String>(HttpStatus.OK);
        } else {
            return new ResponseEntity<String>(HttpStatus.FORBIDDEN);
        }
    }
}
