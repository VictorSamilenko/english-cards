package ua.cards.util;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import ua.cards.model.Group;
import ua.cards.model.Word;

import java.util.Iterator;
import java.util.Set;

public class DataToJSON {

    public static JSONObject convertWordToJSONWithTranslation(Word word) {
        JSONObject nativeJSONObject = convertWordToJSONWithOutTranslation(word);
        JSONArray jsonArrayTranslate = new JSONArray();
        for (Word translateWord : word.getTranslations()) {
            JSONObject translateJSONObject = new JSONObject();
            translateJSONObject.put("id", translateWord.getId());
            translateJSONObject.put("language", translateWord.getLanguage());
            translateJSONObject.put("native", translateWord.getNativeWord());
            translateJSONObject.put("comment", translateWord.getComment());
            translateJSONObject.put("description", translateWord.getDescription());
            translateJSONObject.put("group_id", (translateWord.getGroup() == null) ? "" : translateWord.getGroup().getId());
            translateJSONObject.put("group", (translateWord.getGroup() == null) ? "" : translateWord.getGroup().getName());
            jsonArrayTranslate.add(translateJSONObject);
        }
        nativeJSONObject.put("translation", jsonArrayTranslate);
        return nativeJSONObject;
    }

    public static JSONObject convertWordToJSONWithOutTranslation(Word word) {
        JSONObject nativeJSONObject = new JSONObject();
        nativeJSONObject.put("id", word.getId());
        nativeJSONObject.put("language", word.getLanguage());
        nativeJSONObject.put("native", word.getNativeWord());
        nativeJSONObject.put("comment", word.getComment());
        nativeJSONObject.put("description", word.getDescription());
        nativeJSONObject.put("group_id", (word.getGroup() == null) ? "" : word.getGroup().getId());
        nativeJSONObject.put("group", (word.getGroup() == null) ? "" : word.getGroup().getName());
        return nativeJSONObject;
    }

    public static JSONArray convertSetWordsToJSON(Set<Word> words, Boolean withOutTranslation) {
        JSONArray jsonArrayWords = new JSONArray();
        Iterator<Word> iterator = words.iterator();
        while (iterator.hasNext()) {
            Word word = iterator.next();
            JSONObject nativeJSONObject;
            if (withOutTranslation) {
                nativeJSONObject = convertWordToJSONWithOutTranslation(word);
            } else {
                nativeJSONObject = convertWordToJSONWithTranslation(word);
            }
            jsonArrayWords.add(nativeJSONObject);
        }
        return jsonArrayWords;
    }

    public static JSONArray convertSetWordsToValueAndDataJSON(Set<Word> set) {
        JSONArray jsonArrayWords = new JSONArray();
        Iterator<Word> iterator = set.iterator();
        while (iterator.hasNext()) {
            Word word = iterator.next();
            JSONObject nativeJSONObject = new JSONObject();
            nativeJSONObject.put("value", word.getNativeWord());
            nativeJSONObject.put("data", word.getId());
            jsonArrayWords.add(nativeJSONObject);
        }
        return jsonArrayWords;
    }

    public static JSONObject convertGroupToJSON(Group group) {
        JSONObject nativeJSONObject = new JSONObject();
        nativeJSONObject.put("id", group.getId());
        nativeJSONObject.put("name", group.getName());
        nativeJSONObject.put("comment", group.getComment());
        return nativeJSONObject;
    }

    public static JSONArray convertSetGroupsToJSON(Set<Group> set) {
        JSONArray jsonArrayWords = new JSONArray();
        Iterator<Group> iterator = set.iterator();
        while (iterator.hasNext()) {
            Group group = iterator.next();
            JSONObject jsonObject = null;
            jsonObject = convertGroupToJSON(group);
            jsonArrayWords.add(jsonObject);
        }
        return jsonArrayWords;
    }
}
