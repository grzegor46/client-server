package database;

import constant.Role;
import message.UserMessage;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import user.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;


import static org.jooq.impl.DSL.*;
import static org.jooq.impl.SQLDataType.*;

public class DataBaseManager {

    private final String USER_NAME = "grzegor";
    private final String PASSWORD = "eztddwzz";
    private final String DATABASE = "grzegor_db";
    private final String USERS_TABLE = "users";
    private final String USER_MESSAGE = "usermessage";
    private final String URL = String.format("jdbc:postgresql://localhost:5432/%s", DATABASE);
    private Connection connection;
    private DSLContext context;
    private static final Logger logger = LoggerFactory.getLogger(DataBaseManager.class);


    public DataBaseManager() {
        startConnection();
    }

    public void startConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            this.context = DSL.using(connection, SQLDialect.POSTGRES);
            createUserTable(context);
            createUserMessageTable(context);
        } catch (SQLException sqlException) {
            logger.error("Błąd podczas otwierania połączenia z bazą danych: {}", sqlException.getMessage());
            closeDBConnection();
        }
    }

    public void closeDBConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException sqlException) {
            logger.error("Błąd podczas zamykania połączenia: {}", sqlException.getMessage());
        }
    }

    private void createUserTable(DSLContext context) {
        context.createTableIfNotExists(USERS_TABLE)
                .column("id", INTEGER.identity(true))
                .column("nickname", VARCHAR)
                .column("password", VARCHAR)
                .column("user_role", VARCHAR(5))
                .constraints(
                        constraint().primaryKey("id"))
                .execute();
    }

    private void createUserMessageTable(DSLContext context) {
        context.createTableIfNotExists(USER_MESSAGE)
                .column("sender", VARCHAR)
                .column("receiver", VARCHAR)
                .column("content", VARCHAR(255))
                .column("is_read", BOOLEAN)
                .column("user_id", INTEGER)
                .constraints(
                        constraint().foreignKey("user_id")
                                .references("users", "id")  // Ustawienie klucza obcego
                )
                .execute();
    }
    public void add(User user) {
        context.insertInto(table(USERS_TABLE))
                .set(field("nickname"),user.getNickName())
                .set(field("password"), user.getPassword())
                .set(field("user_role"),user.getRole().toString())
                .execute();

        Result<Record> record = context.select(asterisk()).from(table("users")).fetch();
        System.out.println(record);
    }

    public void addMessageToUserMessageTable(UserMessage sentMessage) {
        Record record1 = context.select(field("id")).from(table(USERS_TABLE)).where(field("nickname").eq(sentMessage.getReceiver())).fetchOne();
        if (record1 != null) {
            int idOfUser = record1.getValue(field("id", Integer.class));
            context.insertInto(table(USER_MESSAGE))
                    .set(field("sender"), sentMessage.getSender())
                    .set(field("receiver"), sentMessage.getReceiver())
                    .set(field("content"), sentMessage.getContent())
                    .set(field("is_read"), sentMessage.isRead())
                    .set(field("user_id"), idOfUser)
                    .execute();
        } else {
            logger.info("Użytkownik o podanym nicku nie istnieje.");
        }
    }

    public User findUserInDB(String name) {
        try {
            Record record1 = context.select(asterisk()).from(table(USERS_TABLE)).where(field("nickname").eq(name)).fetchOne();
            assert record1 != null;

            String nickname = record1.getValue(field("nickname", String.class));
            String password = record1.getValue(field("password", String.class));
            String roleString = record1.getValue(field("user_role", String.class));
            Role role = Role.valueOf(roleString);

            return new User(nickname, password, role);
        }
        catch (Exception e) {
            logger.error("nie znaleziono takiego uzytkownika w bazie danych: {}", e.getMessage());
        }
        return new User();
    }

    public int countUnReadUserMessages(User user) {
        int userId = getUserId(user);

        return context.fetchCount(
                table(USER_MESSAGE)
                        .where(field("is_read").eq(true).and(field("user_id").eq(userId)))
        );
    }

    public List<UserMessage> getUserMessages(User user) {
        int idUser = getUserId(user);

        Result<Record> result = context.select().from(table(USER_MESSAGE)).where(field("user_id").eq(idUser)).fetch();

        List<UserMessage> userMessages = new ArrayList<>();
        for (Record record : result) {
            String sender = record.getValue(field("sender", String.class));
            String receiver = record.getValue(field("receiver", String.class));
            String content = record.getValue(field("content", String.class));
            boolean isRead = record.getValue(field("is_read", Boolean.class));

            UserMessage userMessage = new UserMessage(sender, receiver, content);
            userMessage.setRead(isRead);

            userMessages.add(userMessage);
        }

        return userMessages;
    }

    public void deleteUserFromDB(User user) {
        int userId = getUserId(user);
        context.deleteFrom(table("users"))
                .where(field("id").eq(userId))
                .execute();
    }

    public List<String> getAllExistingUsers() {
        Result<Record1<String>> result = context.select(field("nickname", String.class)).from(table(USERS_TABLE)).fetch();

        return result.stream()
                .map(record -> record.getValue(field("nickname", String.class)))
                .collect(Collectors.toList());
    }

    public void updateUserData(User updatedUser) {
        int userId = getUserId(updatedUser);
        if(userId != 0) {
            List<UserMessage> userMessages = updatedUser.getMailBox();

            context.update(table(USERS_TABLE))
                    .set(field("nickname"), updatedUser.getNickName())
                    .set(field("password"), updatedUser.getPassword())
                    .set(field("user_role"), updatedUser.getRole().toString())
                    .where(field("id").eq(userId))
                    .execute();

            for (UserMessage message : userMessages) {
                context.update(table(USER_MESSAGE))
                        .set(field("content"), message.getContent())
                        .set(field("is_read"), message.isRead())
                        .where(field("user_id").eq(userId)
                                .and(field("sender").eq(message.getSender()))
                                .and(field("receiver").eq(message.getReceiver())))
                        .execute();
            }
        }else {
            logger.info("nie znaleziono uzytkownika");
        }
    }

    private int getUserId(User user){
        Record1<Object> record1 = context.select(field("id")).from(table(USERS_TABLE)).where(field("nickname").eq(user.getNickName())).fetchOne();
        if(record1 != null) {
            return (int) record1.getValue(field("id"));
        }
        return 0;
    }
}
