package server;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.initExceptionHandler;
import static spark.Spark.patch;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;

import lombok.extern.slf4j.Slf4j;
import server.controller.ConditionController;
import server.controller.CountriesController;
import server.controller.DrugController;
import server.controller.Middleware;
import server.controller.OrganController;
import server.controller.ProcedureController;
import server.controller.ProfileController;
import server.controller.UserController;

/**
 * Main entry point for server application.
 */
@Slf4j
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
        log.info("Server is alive!");
        log.info("Listening on port: " + port);

        port(port);
        initExceptionHandler((e) -> {
            log.error("Server init failed");
            log.error(e.getMessage(), e);
        });
        initRoutes();
        initControllers();
    }

    private static void initRoutes() {
        // user api routes.
        path("/api/v1", () -> {
            path("/users", () -> {

                path("/login", () -> post("", UserController::checkCredentials));

                before(((request, response) -> {
                    if(!(Middleware.isAdminAuthenticated(request))) {
                        halt(401, "Unauthorized");
                    }
                }));
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

                post("/login", ProfileController::checkCredentials);

                before(((request, response) -> {
                    if(!(Middleware.isAdminAuthenticated(request))) {
                        if(!(Middleware.isAuthenticated(request))) {
                            halt(401, "Unauthorized");
                        }
                    }
                }));
                get("/all", ProfileController::getAll);
                get("", ProfileController::get);
                post("", ProfileController::create);

                get("/password", ProfileController::hasPassword);
                post("/password", ProfileController::savePassword);

                get("/receivers", ProfileController::getReceiving);
                get("/dead", ProfileController::getDead);

                path("/:id", () -> {
                    patch("", ProfileController::edit);
                    delete("", ProfileController::delete);

                    // drugs api endpoints.
                    path("/drugs", () -> {
                        get("", DrugController::getAll);
                        post("", DrugController::add);
                    });

                    // organs api endpoints.
                    path("/organs", () -> {
                        get("", OrganController::getAll);
                        post("", OrganController::add);
                        delete("", OrganController::delete);

                        // organs api endpoints.
                        path("/expired", () -> {
                            get("", OrganController::getExpired);
                            post("", OrganController::setExpired);
                            delete("", OrganController::delete);
                        });
                    });

                    // condition api endpoints.
                    path("/conditions", () -> {
                        get("", ConditionController::getAll);
                        post("", ConditionController::add);
                    });

                    // procedure api endpoints.
                    path("/procedures", () -> {
                        get("", ProcedureController::getAll);
                        post("", ProcedureController::add);
                    });
                });

                get("/count", ProfileController::count);
            });

            before(((request, response) -> {
                if(!(Middleware.isAdminAuthenticated(request))) {
                    if(!(Middleware.isAuthenticated(request))) {
                        halt(401, "Unauthorized");
                    }
                }
            }));

            // condition api endpoints.
            path("/conditions", () -> {

                path("/:id", () -> {
                    patch("", ConditionController::edit);
                    delete("", ConditionController::delete);
                });
            });

            // procedure api endpoints.
            path("/procedures", () -> {

                // id refers to procedure id
                path("/:id", () -> {
                    patch("", ProcedureController::edit);
                    delete("", ProcedureController::delete);

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
