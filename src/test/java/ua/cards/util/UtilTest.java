package ua.cards.util;


import org.junit.Assert;
import org.junit.Test;

public class UtilTest {

    @Test
    public void getTranslationLanguage() throws Exception {
        String language = Utils.getTranslationLanguage("rus");
        Assert.assertEquals(language,"eng");

        language = Utils.getTranslationLanguage("eng");
        Assert.assertEquals(language,"rus");
    }
}
