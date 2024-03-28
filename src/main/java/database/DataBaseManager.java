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
import java.util.stream.Stream;

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
    DSLContext context;

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
            System.out.println(sqlException.getMessage());
        }
    }

    //TODO kiedy zamknac polaczenie z baza danych?
    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }

    private void createUserTable(DSLContext context) {
        context.createTableIfNotExists("users")
                .column("id", INTEGER.identity(true))
                .column("nickname", VARCHAR)
                .column("password", VARCHAR)
                .column("user_role", VARCHAR(5))
                .column("mail_box", VARCHAR)
                .constraints(
                        constraint().primaryKey("id"))
                .execute();
    }

    private void createUserMessageTable(DSLContext context) {
        context.createTableIfNotExists("usermessage")
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
//        Record record = context.select(asterisk()).from(table("users")).where(field("nickname").eq("user1")).fetchOne();
        context.insertInto(table(USERS_TABLE))
                .set(field("nickname"),user.getNickName())
                .set(field("password"), user.getPassword())
                .set(field("user_role"),user.getRole().toString())
                .execute();
//        Result record2 = context.select(asterisk()).from(table("users")).fetch();
//        System.out.println(record);
//        System.out.println(record2);
        Result record = context.select(asterisk()).from(table("users")).fetch();
        System.out.println(record);
    }

    public void addMessageToUserMessageTable(UserMessage sentMessage) {
        Record record = context.select(field("nickname")).from(table("users")).where(field("nickname").eq(sentMessage.getReceiver())).fetchOne();
        Record record1 = context.select(field("id")).from(table("users")).where(field("nickname").eq(sentMessage.getReceiver())).fetchOne();
        assert record1 != null;
        int idOfUser = Integer.parseInt(record1.getValue(field("id")).toString());
        assert record != null;
        String receiver = record.toString();
        if(receiver != null) {
            context.insertInto(table("usermessage"))
                .set(field("sender"), sentMessage.getSender())
                .set(field("receiver"),sentMessage.getReceiver())
                .set(field("content"), sentMessage.getContent())
                .set(field("is_read"),sentMessage.isRead())
                .set(field("user_id"),idOfUser)
                .execute();
    }

}

    public User findUserInDB(String name) {
        Record record1 = context.select(field("name")).from(table("users")).where(field("nickname").eq(name)).fetchOne();
        assert record1 != null;
        return new User(record1.getValue(field("nickname")).toString(),record1.getValue(field("password")).toString(),(Role) record1.getValue(field("user_role")));
    }
}
