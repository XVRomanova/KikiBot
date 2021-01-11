package com.example.app.bot;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.language_translator.v3.LanguageTranslator;
import com.ibm.watson.language_translator.v3.model.*;
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

        String languageTranslation = language.getJSONArray("languages").getJSONObject(0).getString("language");

        String languageToLanguage = "en-ru";
        if (languageTranslation.equals("ru")){
            languageToLanguage = "ru-en";
        }

        TranslateOptions translateOptions = new TranslateOptions.Builder().addText(message).modelId(languageToLanguage).build();

        TranslationResult result = languageTranslator.translate(translateOptions).execute().getResult();

        JSONObject translation = new JSONObject(result);

        return translation.getJSONArray("translations").getJSONObject(0).getString("translation");


    }

}
