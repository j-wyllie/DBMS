package odms.controller.database.picture;

import java.io.File;
import java.sql.SQLException;

public interface PictureDAO {

    void update(File chosenFile, String image);

    void delete(String image);

    void get(String image) ;

}
