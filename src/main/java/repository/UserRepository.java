package repository;


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

    public void save(User user) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

        this.userList.add(user);
        writeUsersToJson(pathToFileDB, this.userList);
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
                writeUsersToJson(pathToFileDB, this.userList);
            }
        } else {
            System.out.println("User not found.");
        }
    }

    private List<User> readUsersFromJson(String fileName) {
        ObjectMapper objectMapper = new ObjectMapper();
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

        private void writeUsersToJson (String fileName, List<User> userList){
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            try {
                ObjectWriter objectWriter = objectMapper.writerFor(objectMapper
                        .getTypeFactory()
                        .constructCollectionType(List.class, User.class));
                objectWriter.writeValue(new File(fileName), userList);
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

    }
