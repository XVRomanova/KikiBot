package com.example.app.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class Bot extends TelegramLongPollingBot {

    private static final Logger logger = LoggerFactory.getLogger(Bot.class);

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.name}")
    private String botUsername;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }


    public void sendMsg(Message message, String text) {
        SendMessage response = new SendMessage();
        response.enableMarkdown(true);

        Long chatId = message.getChatId();
        response.setChatId(String.valueOf(chatId));

        response.setReplyToMessageId(message.getMessageId());
        response.setText(text);

        try {
            setButtons(response);
            execute(response);
                    logger.info("Send message \"{}\" to {}", text, chatId);
        } catch (TelegramApiException e) {
                    logger.error("Failed to send message \"{}\" to {} due to error: {}", text, chatId, e.getMessage());
        }

    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {

            Message message = update.getMessage();

            switch (message.getText()) {
                case ("/start"):
                    sendMsg(message, "Hello! Let's translate something ;)");
                    break;
                case ("/help"):
                    sendMsg(message, "Type message, and it will be translated into Russian or English");
                    break;
                default:
                    try {
                        sendMsg(message,Translator.getTranslation(message.getText()));
                    } catch (IllegalArgumentException | IOException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    public void setButtons(SendMessage sendMessage){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        List<KeyboardRow> keyboardRowList = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();

        keyboardFirstRow.add(new KeyboardButton("/help"));

        keyboardRowList.add(keyboardFirstRow);
        replyKeyboardMarkup.setKeyboard(keyboardRowList);

    }

    @PostConstruct
    public void start() {
        logger.info("username: {}, token: {}", botUsername, botToken);
    }


}
