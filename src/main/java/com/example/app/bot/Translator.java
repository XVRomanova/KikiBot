package com.example.app.bot;

import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.IdentifiedLanguages;
import com.ibm.watson.language_translator.v3.model.IdentifyOptions;
import com.ibm.watson.language_translator.v3.model.TranslateOptions;
import com.ibm.watson.language_translator.v3.model.TranslationResult;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class Translator {

    public static String getTranslation(String message) throws IOException {

        PropertyFileReader propertyFileReader = new PropertyFileReader();
        String apiKey = propertyFileReader.getPropValues()[0];
        String versionDate = propertyFileReader.getPropValues()[1];
        String url = propertyFileReader.getPropValues()[2];

        IamAuthenticator authenticator = new IamAuthenticator(apiKey);
        LanguageTranslator languageTranslator = new LanguageTranslator(versionDate, authenticator);
        languageTranslator.setServiceUrl(url);

        IdentifyOptions indenifyOptions = new IdentifyOptions.Builder().text(message).build();
        IdentifiedLanguages languages = languageTranslator.identify(indenifyOptions).execute().getResult();

        JSONObject language = new JSONObject(languages);

        JSONArray jsonArray = language.getJSONArray("languages");

        String languageToLanguage = "en-ru";
        for (int i = 0; i < jsonArray.length(); i++) {
            String lang = jsonArray.getJSONObject(i).getString("language");

            if (lang.equals("en")) {
                languageToLanguage = "en-ru";
                break;
            }

            if (lang.equals("ru")) {
                languageToLanguage = "ru-en";
                break;
            }
        }

        TranslateOptions translateOptions = new TranslateOptions.Builder().addText(message).modelId(languageToLanguage).build();

        TranslationResult result = languageTranslator.translate(translateOptions).execute().getResult();

        JSONObject translation = new JSONObject(result);


        String ans = "{" + languageToLanguage + "} \n" + translation.getJSONArray("translations").getJSONObject(0).getString("translation");
        System.out.println(ans);
        return  ans;


    }

}
