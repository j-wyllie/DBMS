package server.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import javax.servlet.http.Part;
import odms.commons.model.profile.Profile;
import odms.controller.data.ImageDataIO;
import org.sonar.api.internal.google.gson.Gson;
import server.model.enums.KeyEnum;
import server.model.enums.ResponseMsgEnum;
import spark.Request;
import spark.Response;
import sun.nio.ch.IOUtil;

public class PictureController {



    public static String update(Request req, Response res) {

        try {

            // Get file and profile nhi from req
            // Update image in server, save.
            String imageName =  req.queryParams("name");

//            Part imagePart =  req.raw().getPart("myfile");
//
//            InputStream inputStream = imagePart.getInputStream();
//            OutputStream imageOut = new FileOutputStream("/profile_pictures/" + imageName);
//            IOUtil.copy(inputStream, outputStream);


//            File image = new File(imageOut.toString());
//            ImageDataIO.deleteAndSaveImage(
//                    image, imageName
//            );


        } catch (Exception e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }


        res.status(200);
        return "Picture Updated";


    }


    public static String create(Request req, Response res) {


        try {

            // save image to server
            String imageName =  req.queryParams("name");


        } catch (Exception e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }


        res.status(200);
        return "Picture Uploaded";


    }

    public static String delete(Request req, Response res) {
        try {

            // delete image in server
            String imageName =  req.queryParams("name");

            ImageDataIO.deleteImage(imageName);


        } catch (Exception e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }


        res.status(200);
        return "Picture Deleted";


    }


    public static String get(Request req, Response res) {
        try {

            // get image from server
            String imageName =  req.queryParams("name");


        } catch (Exception e) {
            res.status(400);
            return ResponseMsgEnum.BAD_REQUEST.toString();
        }


        res.status(200);
        return "Picture Deleted";


    }




}
