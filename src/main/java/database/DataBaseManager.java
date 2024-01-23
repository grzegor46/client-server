package database;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import user.User;
import utils.PropertiesUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DataBaseManager {

    private final String pathToFileDB = PropertiesUtils.databasePath;
    private static final ObjectMapper objectMapper = createObjectMapper();

    public List<User> readUsersFromJson() {
        File jsonFile = new File(this.pathToFileDB);

        if (!jsonFile.exists() || jsonFile.length() == 0) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(jsonFile, objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void writeUsersToJson(List<User> userList) {

        try {

            ObjectWriter objectWriter = objectMapper.writer().withRootValueSeparator("\n").withDefaultPrettyPrinter();
            objectWriter.writeValue(new File(pathToFileDB), userList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ObjectMapper createObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        // enable toString method of enums to return the value to be mapped
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return mapper;
    }
}
