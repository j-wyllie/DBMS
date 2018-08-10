package server;

import static spark.Spark.*;

/**
 * Main entry point for server application.
 */
public class Server {
    private static Integer port = 9980;

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
        // user api routes.
        path("/users", () -> {

            get("", null);
            post("", null);

            path("/:id", () -> {

                get("", null);
                patch("", null);
                delete("", null);
            });
        });

        // profile api routes.
        path("/profiles", () -> {

        });
    }

    private static void initControllers() {

    }

}
