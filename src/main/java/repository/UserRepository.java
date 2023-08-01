package repository;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import user.User;

import java.io.File;
import java.io.IOException;

public class UserRepository {

//    TODO method save --> this method will be saving User to Json File
//    TODO method findByName --> this method will be finding created user from json file
//    TODO method getAll --> this method will be get list of created users
//    TODO method delete --> this method will delete created users

    public void save(ObjectNode user) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode rootNode = objectMapper.createObjectNode();
        File file = new File("src/main/java/database/UserDB.json");
        if(!file.exists()) {
            rootNode.set("User",user);
            objectMapper.writeValue(new File("src/main/java/database/UserDB.json"), user);
        } else {

        }
    }

}
