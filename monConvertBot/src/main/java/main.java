import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.SneakyThrows;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;

public class main extends TelegramLongPollingBot {
    public static void main(String[] args) {
        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();
        try {
            api.registerBot(new main());
        } catch (TelegramApiRequestException e) {
//            e.printStackTrace();
        }
    }

    String fromMoney = "", toMoney = "";
    MB fromObject, toObject;
    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup().setOneTimeKeyboard(true).setResizeKeyboard(true).setSelective(true).setKeyboard(null);
        SendMessage send = new SendMessage().setChatId(update.getMessage().getChatId()).setReplyMarkup(replyMarkup);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        URL url = new URL("https://cbu.uz/oz/arkhiv-kursov-valyut/json/");
        URLConnection urlCon = url.openConnection();
        BufferedReader reader = new BufferedReader(new InputStreamReader(urlCon.getInputStream()));
        Type type = new TypeToken<ArrayList<MB>>(){}.getType();
        ArrayList<MB> mbs = gson.fromJson(reader, type);
        MB mb = new MB();
        mb.setMCcy("UZS");
        mb.setMRate("1");
        mbs.add(0, mb);

        Message message = update.getMessage();
        ArrayList<KeyboardRow> rows = new ArrayList<>();

        if (message.getText().equals("/start")) {
            String user = message.getChat().getUserName();
            if (user == null) user = message.getChat().getFirstName();
            send.setText("Assalomu alaykum @" + user + " botimizga xush kelibsiz\n\nBu bot orqali siz valyutalarni convertatsiya qilishingiz mumkin.");

            KeyboardRow row = new KeyboardRow();
            row.add("/convert");
            rows.add(row);
        } else if (message.getText().equals("/convert")) {
            fromMoney = "";
            toMoney = "";
            for (int j = 0; j < mbs.size(); j += 4) {
                KeyboardRow row = new KeyboardRow();
                row.add(mbs.get(j).getMCcy());
                row.add(mbs.get(j + 1).getMCcy());
                row.add(mbs.get(j + 2).getMCcy());
                row.add(mbs.get(j + 3).getMCcy());
                rows.add(row);
            }
            send.setText("FromCourseni tanlang.");
        } else {
            boolean tick = true, tick1 = true;
            String courseText = "Oh No! I'm sorry!";
            for (int i = 0; i < mbs.size() && fromMoney.equals(""); i++) {
                if (mbs.get(i).getMCcy().equals(message.getText())) {
                    fromMoney = mbs.get(i).getMRate();
                    fromObject = mbs.get(i);
                    courseText = "FromCourse o'rnatildi (1 " + mbs.get(i).getMCcy() + " = " + fromMoney + ").\nToCourse ni tanlang.";
                    tick = false;
                } else courseText = "FromCourse topilmadi. Qaytadan tanlang.";
            }
            for (int i = 0; i < mbs.size() && !fromMoney.equals("") && toMoney.equals("") && tick; i++) {
                if (mbs.get(i).getMCcy().equals(message.getText())) {
                    toMoney = mbs.get(i).getMRate();
                    toObject = mbs.get(i);
                    tick1 = false;
                    courseText = "ToCourse o'rnatildi (1 " + mbs.get(i).getMCcy() + " = " + toMoney + "), " + fromObject.getMCcy() + "=>" + mbs.get(i).getMCcy() + ".\nPulni kiriting.";
                } else courseText = "ToCourse topilmadi. Qaytadan tanlang.";
            }

            if (!fromMoney.equals("") && !toMoney.equals("") && tick && tick1 && !fromMoney.equals(toMoney)) {
                KeyboardRow row = new KeyboardRow();
                row.add("/convert");
                rows.add(row);
                courseText = message.getText() + " " + fromObject.getMCcy() + " = " + Double.toString(Double.parseDouble(message.getText()) * Double.parseDouble(fromMoney) / Double.parseDouble(toMoney)) + " " +toObject.getMCcy() + ".\nPulni kiriting.";
            }

            send.setText(courseText);
        }

        replyMarkup.setKeyboard(rows);

        try {
            execute(send);
        } catch (TelegramApiException e) {
//            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "conMoneyBot";
    }

    @Override
    public String getBotToken() {
        return "5295550275:AAHMJ6Q6T48lezoWWhtYLUKMERZ9OnOjoOU";
    }
}
