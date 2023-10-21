package repository;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import user.User;
import user.UserList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class UserRepository {

    final String pathToFileDB = "src/main/java/database/UserDB.json";


//    TODO method save --> this method will be saving User to Json File
//    TODO method findByName --> this method will be finding created user from json file
//    TODO method getAll --> this method will be get list of created users
//    TODO method delete --> this method will delete created users

    public void save(User user) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

        boolean isDBExists = Path.of(pathToFileDB).getFileName().endsWith("UserDB.json");
        List<User> userList = readUsersFromJson(pathToFileDB);
        userList.add(user);
        writeUsersToJson(pathToFileDB, userList);
    }

    public void delete(String nickname) {
        User userToDelete = findUserName(nickname);
        List<User> userList = readUsersFromJson(pathToFileDB);
        userList.remove(userToDelete);
        writeUsersToJson(pathToFileDB,userList);
    }

    private static List<User> readUsersFromJson(String fileName) {
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

        private static void writeUsersToJson (String fileName, List<User> userList){
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

        private User findUserName(String name) {
            List<User> userList = readUsersFromJson(pathToFileDB);
            for(User user : userList) {
                if(user.getNickName().contains(name)) {
                    return user;
                }
            }
            return null;
        }


    }
