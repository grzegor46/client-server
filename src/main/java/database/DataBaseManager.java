package database;

import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.jooq.impl.DSL.*;
import static org.jooq.impl.SQLDataType.*;


public class DataBaseManager {

    private final String USER_NAME = "grzegor";
    private final String PASSWORD = "eztddwzz";
    private final String DATABASE = "grzegor_db";
    private final String URL = String.format("jdbc:postgresql://localhost:5432/%s", DATABASE);
    private Connection connection;

    public DataBaseManager() {
        startConnection();
    }

    public void startConnection() {
        try {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
            DSLContext context = DSL.using(connection, SQLDialect.POSTGRES);
            createDataBase(context);
            createUserTable(context);
            createUserMessageTable(context);
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }

    private void createDataBase(DSLContext context) {
        context.createDatabaseIfNotExists(DATABASE).execute();
    }


    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException sqlException) {
            System.out.println(sqlException.getMessage());
        }
    }

    private void createUserTable(DSLContext context) {
        context.createTableIfNotExists("user")
            .column("nickName", VARCHAR)
            .column("password", VARCHAR)
            .column("userRole",VARCHAR(5))
            .column("mailBox", VARCHAR)
            .execute();
    }

    private void createUserMessageTable(DSLContext context) {
        context.createTableIfNotExists("userMessage")
            .column("sender",   VARCHAR)
            .column("receiver", VARCHAR)
            .column("content",  VARCHAR(255))
            .column("isRead",   BOOLEAN)
            .column("user_id",  INTEGER)
            .constraints(
                constraint("fk").foreignKey("user_id").references("user","id")
            )
            .execute();
    }


//    private final String pathToFileDB = PropertiesUtils.databasePath;
//    private static final ObjectMapper objectMapper = createObjectMapper();
//
//    public List<User> readUsersFromJson() {
//        File jsonFile = new File(this.pathToFileDB);
//
//        if (!jsonFile.exists() || jsonFile.length() == 0) {
//            return new ArrayList<>();
//        }
//
//        try {
//            return objectMapper.readValue(jsonFile, objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
//        } catch (IOException e) {
//            e.printStackTrace();
//            return new ArrayList<>();
//        }
//    }
//
//    public void writeUsersToJson(List<User> userList) {
//
//        try {
//
//            ObjectWriter objectWriter = objectMapper.writer().withRootValueSeparator("\n").withDefaultPrettyPrinter();
//            objectWriter.writeValue(new File(pathToFileDB), userList);
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static ObjectMapper createObjectMapper() {
//        final ObjectMapper mapper = new ObjectMapper();
//        // enable toString method of enums to return the value to be mapped
//        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
//        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
//        return mapper;
//    }
}
