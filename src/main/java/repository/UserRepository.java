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



public class UserRepository {

    private static final String pathToFileDB = "src/main/java/database/UserDB.json";
    private List<User> userList = readUsersFromJson(pathToFileDB);


    public List<User> getAllUsers(){
        return this.userList;
    }

    public void save(User user) {
        this.readUsersFromJson(pathToFileDB);
        ObjectMapper objectMapper = createObjectMapper();
        objectMapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

        this.userList.add(user);
        writeUsersToJson(this.userList);
    }

    public void update(String nickname, String passwordToChange) {
        User user = findUserName(nickname);
        if (user != null) {
            this.delete(nickname);
            user.setPassword(passwordToChange);
            this.save(user);
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
        ObjectMapper objectMapper = createObjectMapper();

        File jsonFile = new File(fileName);

        // Jeśli plik nie istnieje, tworzymy nową pustą listę
        if (!jsonFile.exists()) {
            return new ArrayList<>();
        }

        try {
            return objectMapper.readValue(jsonFile, objectMapper.getTypeFactory()
                    .constructCollectionType(List.class, User.class));
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

        public void writeUsersToJson (List<User> userList){
            ObjectMapper objectMapper = createObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            try {
                ObjectWriter objectWriter = objectMapper.writerFor(objectMapper
                        .getTypeFactory()
                        .constructCollectionType(List.class, User.class));
                objectWriter.writeValue(new File(pathToFileDB), userList);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public User findUserName(String name) {
            for(User user : this.userList) {
                if(user.getNickName().toLowerCase().contains(name.toLowerCase())){
                    return user;
                }
            }
            return null;
        }

    private ObjectMapper createObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();
        // enable toString method of enums to return the value to be mapped
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        return mapper;
    }

    }
