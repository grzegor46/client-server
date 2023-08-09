package repository;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import user.UserList;


import java.io.File;
import java.io.IOException;

public class UserRepository {

//    TODO method save --> this method will be saving User to Json File
//    TODO method findByName --> this method will be finding created user from json file
//    TODO method getAll --> this method will be get list of created users
//    TODO method delete --> this method will delete created users

    public void save(UserList userList) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.WRAP_ROOT_VALUE);

        mapper.writeValue(new File("src/main/java/database/UserDB.json"), userList);
    }

}
