package ua.cards.controller;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ua.cards.model.Group;
import ua.cards.model.User;
import ua.cards.model.Word;
import ua.cards.service.GroupService;
import ua.cards.service.WordService;
import ua.cards.util.DataToJSON;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Set;

@Controller
@Transactional
@RequestMapping("/words")
public class WordController {

    @Autowired
    WordService wordService;
    @Autowired
    GroupService groupService;

    @RequestMapping(method = RequestMethod.GET)
    public String getAllWords(HttpServletRequest request) {
        User user = (User) request.getAttribute("user");
        Set<Group> groups = groupService.get(user);
        request.setAttribute("groups", groups);
        return "words";
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public ResponseEntity<String> getWords(HttpServletRequest request,
                                           @RequestParam(defaultValue = "rus") String language,
                                           @RequestParam(value = "start") Integer offset,
                                           @RequestParam(value = "length") Integer limit,
                                           @RequestParam(value = "draw", defaultValue = "0") Integer draw,
                                           @RequestParam(value = "order[0][dir]", required = false) String dir,
                                           @RequestParam(value = "search[value]", required = false) String searchValue) {
        User user = (User) request.getAttribute("user");
        Set<Word> words = wordService.getAll(user, language, limit, offset, searchValue, dir);
        long totalCount, displayCount;
        totalCount = wordService.count(user, language);
        displayCount = wordService.count(user, language, searchValue);

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", HttpStatus.OK);
        jsonResponse.put("words", DataToJSON.convertSetWordsToJSON(words, false));
        jsonResponse.put("draw", draw);
        jsonResponse.put("iTotalRecords", totalCount);
        jsonResponse.put("iTotalDisplayRecords", displayCount);

        String resultJson = jsonResponse.toJSONString();
        return new ResponseEntity<String>(resultJson, HttpStatus.OK);
    }

    @RequestMapping(value = "/autocomplete", method = RequestMethod.GET, produces = "application/json; charset=utf-8")
    public ResponseEntity<String> autoCompleteWords(String criteria) throws UnsupportedEncodingException {
        Set<Word> words = wordService.likeByValue(URLDecoder.decode(criteria, "UTF-8"));
        JSONArray resultJSON = DataToJSON.convertSetWordsToJSON(words, true);
        return new ResponseEntity<String>(resultJSON.toJSONString(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ResponseEntity<String> saveWord(HttpServletRequest request,
                                           @RequestParam(value = "id", required = false) Long id,
                                           @RequestParam("native_word") String nativeWord,
                                           @RequestParam("language") String language,
                                           @RequestParam(value = "description", required = false) String description,
                                           @RequestParam(value = "comment", required = false) String comment,
                                           @RequestParam(value = "translations", required = false) String[] translations,
                                           @RequestParam(value = "group_id", required = false) Long groupId) {
        User user = (User) request.getAttribute("user");
        if (user == null)
            return new ResponseEntity<String>("unauthorized", HttpStatus.UNAUTHORIZED);
        String resultAction;
        if (id == null)
            resultAction = "insert";
        else resultAction = "update";
        Word word = new Word(id, nativeWord, language, description, comment);
        Group group = groupService.get(groupId);
        if (group != null) {
            word.getGroups().add(group);
        }

        wordService.save(word, translations);

        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("status", HttpStatus.OK);
        jsonResponse.put("action", resultAction);
        jsonResponse.put("word", DataToJSON.convertWordToJSONWithTranslation(word));
        String resultJson = jsonResponse.toJSONString();
        return new ResponseEntity<String>(resultJson, HttpStatus.OK);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteWord(HttpServletRequest request,
                                             @PathVariable Long id) {
        User user = (User) request.getAttribute("user");
        if (user == null)
            return new ResponseEntity<String>("unauthorized", HttpStatus.UNAUTHORIZED);
        if (user.isAdmin()) {
            wordService.delete(id);
            return new ResponseEntity<String>(HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("You must have admin privilege!", HttpStatus.FORBIDDEN);
        }
    }
}
