package odms.controller.database.user;

import java.sql.SQLException;
import java.util.List;
import odms.commons.model.user.User;
import odms.controller.user.UserNotFoundException;

public class HttpUserDAO implements UserDAO {

    @Override
    public List<User> getAll() throws SQLException {
        return null;
    }

    @Override
    public User get(int userId) throws UserNotFoundException, SQLException {
        return null;
    }

    @Override
    public User get(String username) throws UserNotFoundException, SQLException {
        return null;
    }

    @Override
    public void add(User user) throws SQLException {

    }

    @Override
    public boolean isUniqueUsername(String username) throws SQLException {
        return false;
    }

    @Override
    public void remove(User user) throws SQLException {

    }

    @Override
    public void update(User user) throws SQLException {

    }

    @Override
    public List<User> search(String name) throws SQLException {
        return null;
    }

    @Override
    public List<User> search(int id) throws SQLException {
        return null;
    }
}
