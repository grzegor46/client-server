package clientServer.server.integrationTests;

import message.ServerMessage;
import org.junit.jupiter.api.Test;
import utils.PropertiesUtils;

import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ServerMessageTest {

    private final String createdServerDate = LocalDate.now().toString();
    private final String appVersion = PropertiesUtils.applicationVersion;

    @Test
    void shouldReturnServerInfo(){

        String infoCommand = ServerMessage.getInfo(createdServerDate, appVersion);
        assertEquals(infoCommand, "{\"createdServerDate\":\""+createdServerDate+"\",\"appVersion\":\""+appVersion+"\"}");
    }

    @Test
    void shouldReturnServerUpTimeAfterSpecificTime() throws InterruptedException {
        Instant createdInstant = Instant.now();
        Thread.sleep(5000);
        String upTimeCommand = ServerMessage.getUpTime(createdInstant);
        String time = "{\"days\":0,\"hours\":0,\"minutes\":0,\"seconds\":5}";
        assertEquals(upTimeCommand, time);
    }

    @Test
    void shouldReturnServerHelpCommand(){
        String helpCommand = ServerMessage.getHelp();
        assertEquals(helpCommand, "{\"info\":\"zwraca numer wersji serwera, date jego utworzenia\",\"uptime\":\"zwraca czas zycia serwera\",\"help\":\"zwraca liste dostepnych komend z krotkim opisem\",\"stop\":\"zatrzymuje jednoczesnie serwer i klienta\",\"create user\":\"powoduje utworzenie nowego uzytkownika\",\"login\":\"logowanie sie na konto uzytkownika\"}");
    }
}
