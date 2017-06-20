package ua.cards.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import ua.cards.model.Group;
import ua.cards.model.Word;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class DataToJSONTest {

    private Set<Word> words;

    {
        words = new LinkedHashSet<>(3);
        Set<Word> translation = new LinkedHashSet<>(2);
        Word word;
        for (long i = 0; i < 2; i++) {
            word = new Word(i, "native" + i);
            word.setComment("comment" + i);
            word.setDescription("description" + i);
            word.setLanguage("eng");
            word.setGroup(new Group(0L, "group", "comment"));
            translation.add(word);
        }

        for (long i = 0; i < 3; i++) {
            word = new Word(i, "native" + i);
            word.setComment("comment" + i);
            word.setDescription("description" + i);
            word.setLanguage("rus");
            word.setGroup(new Group(0L, "group", "comment"));
            word.setTranslations(translation);
            words.add(word);
        }
    }

    @Test
    public void testConvertSetGroupsToJSON() {
        Set<Group> set = new LinkedHashSet<>(3);
        JSONArray source = new JSONArray();
        for (long i = 0; i < 3; i++) {
            Group group = new Group(i, "Group" + i, "Comment" + i);
            set.add(group);
            JSONObject object = new JSONObject();
            object.put("id", i);
            object.put("name", "Group" + i);
            object.put("comment", "Comment" + i);
            source.add(object);
        }
        JSONArray test = DataToJSON.convertSetGroupsToJSON(set);
        Assert.assertEquals(source, test);
    }

    @Test
    public void testConvertGroupToJSON() {
        Group testGroup = new Group(1L, "Group", "Comment");
        JSONObject object = new JSONObject();
        object.put("id", 1);
        object.put("name", "Group");
        object.put("comment", "Comment");

        JSONObject test = DataToJSON.convertGroupToJSON(testGroup);
        Assert.assertEquals(object.toJSONString(), test.toJSONString());
    }

    @Test
    public void testConvertSetWordsToValueAndDataJSON() {
        JSONArray general = new JSONArray();
        JSONArray source = DataToJSON.convertSetWordsToValueAndDataJSON(words);
        JSONObject object = new JSONObject();
        object.put("data", 0);
        object.put("value", "native0");
        general.add(object);
        object = new JSONObject();
        object.put("data", 1);
        object.put("value", "native1");
        general.add(object);
        object = new JSONObject();
        object.put("data", 2);
        object.put("value", "native2");
        general.add(object);
//        Assert.assertEquals(general, source);
        Assert.assertEquals(general.toJSONString(), source.toJSONString());
    }

}
