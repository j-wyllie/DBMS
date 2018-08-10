package server;

import static spark.Spark.*;

import org.sonar.api.user.User;
import server.controller.UserController;

/**
 * Main entry point for server application.
 */
public class Server {
    private static Integer port = 9980;

    private static UserController userController;

    /**
     * Server class should not be instantiated.
     */
    private Server() {
        throw new UnsupportedOperationException();
    }

    /**
     *
     * @param args parameters for application
     */
    public static void main (String[] args) {
        System.out.println("Server is alive!");

        for (String arg : args) {
            arg = arg.toLowerCase();
            switch (arg) {
                case "-port":
                    System.out.println("Example to set a custom port.");
            }
        }
        port(port);
        initExceptionHandler((e) -> System.out.println("Server init failed"));
        initRoutes();
        initControllers();
    }

    private static void initRoutes() {
        // user api endpoints.
        path("/users", () -> {

            get("", UserController::getAllUsers);
            post("", UserController::createUser);

            path("/:id", () -> {
                get("", UserController::getOneUsers);
                patch("", UserController::editUser);
                delete("", UserController::deleteUser);
            });
        });
    }

    private static void initControllers() {
        userController = new UserController();
    }

}
