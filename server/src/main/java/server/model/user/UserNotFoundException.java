package server.model.user;

public class UserNotFoundException extends Exception {

    private final String username;
    private final Integer userId;

    public UserNotFoundException(String message, int userId) {
        super(message);
        this.username = null;
        this.userId = userId;
    }

    public UserNotFoundException(String message, String username) {
        super(message);
        this.username = username;
        this.userId = null;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
