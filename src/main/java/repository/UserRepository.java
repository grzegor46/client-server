package repository;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import user.User;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class UserRepository implements Repository {

    private static final String pathToFileDB = "src/main/java/database/UserDB.json";
    private static final ObjectMapper objectMapper = createObjectMapper();
    private final List<User> userList = readUsersFromJson(pathToFileDB);

    public List<User> getAllUsers() {
        return this.userList;
    }

    public void save(User user) {
        this.userList.add(user);
        writeUsersToJson(this.userList);
    }

    public void update(User userWithNewUpdatedData) {
        User userToUpdate = findUserName(userWithNewUpdatedData.getNickName());
        if (userToUpdate != null) {
            this.delete(userToUpdate.getNickName());
            this.save(userToUpdate);
        } else {
            System.out.println("User not found.");
        }
    }

    public void delete(String nickname) {
        User userToDelete = findUserName(nickname);

        if (userToDelete != null) {
            boolean isUserDeleted = this.userList.remove(userToDelete);
            if (isUserDeleted) {
                writeUsersToJson(this.userList);
            }
        } else {
            System.out.println("User not found.");
        }
    }

    private List<User> readUsersFromJson(String fileName) {
        File jsonFile = new File(fileName);

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
            objectWriter.writeValue(new File(pathToFileDB), this.userList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User findUserName(String name) {
        for (User user : this.userList) {
            if (user.getNickName().equals(name.toLowerCase())) {
                return user;
            }
        }
        return null;
    }

    private static ObjectMapper createObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        // enable toString method of enums to return the value to be mapped
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return mapper;
    }
}
