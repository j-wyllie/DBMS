package server;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.initExceptionHandler;
import static spark.Spark.patch;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;

import server.controller.ConditionController;
import server.controller.CountriesController;
import server.controller.DrugController;
import server.controller.OrganController;
import server.controller.ProcedureController;
import server.controller.ProfileController;
import server.controller.UserController;

/**
 * Main entry point for server application.
 */
public class Server {
    private static Integer port = 6969;

    private static UserController userController;
    private static ProfileController profileController;
    private static ProcedureController procedureController;
    private static OrganController organController;
    private static DrugController drugController;
    private static CountriesController countriesController;
    private static ConditionController conditionController;


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
        System.out.println("Listening on port: " + port);

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
        path("/api/v1", () -> {
            path("/users", () -> {
                get("/all", UserController::getAll);
                get("", UserController::get);
                post("", UserController::create);

                path("/:id", () -> {
                    patch("", UserController::edit);
                    delete("", UserController::delete);
                });
            });

            // profile api routes.
            path("/profiles", () -> {

                get("/all", ProfileController::getAll);
                get("", ProfileController::get);
                post("", ProfileController::create);

                path("/:id", () -> {
                    patch("", ProfileController::edit);
                    delete("", ProfileController::delete);

                    // drugs api endpoints.
                    path("/drugs", () -> {
                        get("", DrugController::getAll);
                        post("", DrugController::add);
                    });
                });

                get("/count", ProfileController::count);

                // organs api endpoints.
                path("/organs", () -> {
                    get("", OrganController::getAll);
                    post("", OrganController::add);
                    delete("", OrganController::delete);
                });
            });

            // condition api endpoints.
            path("/conditions", () -> {
                get("", ConditionController::getAll);
                post("", ConditionController::add);

                path("/:id", () -> {
                    patch("", ConditionController::edit);
                    delete("", ConditionController::delete);
                });
            });

            // procedure api endpoints.
            path("/procedures", () -> {
                path("/:id", () -> {
                    get("", ProcedureController::getAll);
                    patch("", ProcedureController::edit);
                    delete("", ProcedureController::delete);        // id refers to procedure id
                    post("", ProcedureController::add);

                    path("/organs", () -> {
                        get("", ProcedureController::getOrgans);
                        post("", ProcedureController::addOrgan);
                        delete("", ProcedureController::deleteOrgan);
                    });
                });
            });

            // drugs api endpoints.
            path("/drugs", () -> {
                path("/:id", () -> {
                    patch("", DrugController::edit);
                    delete("", DrugController::delete);
                });
            });

            // countries api endpoints.
            path("/countries", () -> {
                get("", CountriesController::getAll);
                patch("", CountriesController::edit);
            });
        });
    }

    private static void initControllers() {

        userController = new UserController();
        profileController = new ProfileController();
        organController = new OrganController();
        drugController = new DrugController();
        countriesController = new CountriesController();
        conditionController = new ConditionController();
    }

}
