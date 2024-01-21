package message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.time.Duration;
import java.time.Instant;

public class ServerMessage {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String getHelp() {
        ObjectNode helpCommandAsJson = objectMapper.createObjectNode();
        helpCommandAsJson.put("info", "zwraca numer wersji serwera, datę jego utworzenia");
        helpCommandAsJson.put("uptime", "zwraca czas życia serwera");
        helpCommandAsJson.put("help", "zwraca listę dostępnych komend z krótkim opisem");
        helpCommandAsJson.put("stop", "zatrzymuje jednocześnie serwer i klienta");
        helpCommandAsJson.put("create user","powoduje utworzenie nowego użytkownika");
        helpCommandAsJson.put("login", "logowanie się na konto użytkownika");

        return helpCommandAsJson.toString();
    }

    public static String getInfo(String createdServerDate, String applicationVersion) {
        ObjectNode infoCommandAsJson = objectMapper.createObjectNode();
        infoCommandAsJson.put("createdServerDate", createdServerDate);
        infoCommandAsJson.put("appVersion", applicationVersion);

        return infoCommandAsJson.toString();
    }

    public static String getUpTime(Instant createdInstant) {
        ObjectNode upTimeCommandAsJson = objectMapper.createObjectNode();
        Duration duration = Duration.between(createdInstant, Instant.now());
        long durationSeconds = duration.getSeconds() % 60;
        long durationMinutes = duration.toMinutes() % 60;
        long durationHours = duration.toHours();
        long durationDays = duration.toDays();
        upTimeCommandAsJson.put("days", durationDays);
        upTimeCommandAsJson.put("hours", durationHours);
        upTimeCommandAsJson.put("minutes", durationMinutes);
        upTimeCommandAsJson.put("seconds", durationSeconds);

        return upTimeCommandAsJson.toString();
    }

}
