package odms.controller;

public class UserNotFoundException extends Exception {

    private String username;
    private Integer userId;

    public UserNotFoundException(String message, int userId) {
        super(message);
        this.userId = userId;
    }

    public UserNotFoundException(String message, String username){
        this.username = username;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}
