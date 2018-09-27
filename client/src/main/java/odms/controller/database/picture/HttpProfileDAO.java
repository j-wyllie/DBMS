package odms.controller.database.picture;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import odms.controller.http.Request;

@Slf4j
public class HttpProfileDAO implements PictureDAO{


    @Override
    public void update(File chosenFile, String imageName) {

        System.out.println(chosenFile.toURI());
        System.out.println(chosenFile.toString());

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("name", String.valueOf(imageName));

        String url = "http://localhost:6969/api/v1/pictures/" + imageName;


        // Set body to contain chosenFile and image. TODO
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(chosenFile);
        } catch (FileNotFoundException e) {
            return;
        }


//        Request request = new Request(url, queryParams, chosenFile);
//        try {
//            request.patch();
//        } catch (IOException e) {
//            log.error(e.getMessage(), e);
//        }

    }


    @Override
    public void delete(String imageName) {
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("name", String.valueOf(imageName));

        String url = "http://localhost:6969/api/v1/pictures/";

        Request request = new Request(url, new HashMap<>());
        try {
            request.delete();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    public void get(String imageName) {

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("name", String.valueOf(imageName));

        String url = "http://localhost:6969/api/v1/pictures/";

        Request request = new Request(url, new HashMap<>());
        try {
            request.get();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }


}
