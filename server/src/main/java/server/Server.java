package server;

import static spark.Spark.before;
import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.initExceptionHandler;
import static spark.Spark.patch;
import static spark.Spark.path;
import static spark.Spark.port;
import static spark.Spark.post;

import lombok.extern.slf4j.Slf4j;
import server.controller.ConditionController;
import server.controller.DrugController;
import server.controller.HospitalController;
import server.controller.HLAController;
import server.controller.OrganController;
import server.controller.ProcedureController;
import server.controller.ProfileController;
import server.controller.SettingsController;
import server.controller.UserController;
import server.controller.*;

/**
 * Main entry point for server application.
 */
@Slf4j
public class Server {
    private static Integer port = 8080;

    /**
     * Server class should not be instantiated.
     */
    private Server() {
        throw new UnsupportedOperationException();
    }

    /**
     * The main server start.
     * @param args parameters for application
     */
    public static void main(String[] args) {
        log.info("Server is alive!");
        log.info("Listening on port: " + port);

        port(port);
        initExceptionHandler(e -> {
            log.error("Server init failed");
            log.error(e.getMessage(), e);
        });

        initRoutes();
    }

    /**
     * Initialises the server endpoints.
     */
    private static void initRoutes() {
        // user api routes.
        path("/api/v1", () -> {

            // Initial interactions.

            post("/setup", CommonController::setup);
            post("/login", CommonController::checkCredentials);
            post("/logout", CommonController::logout);
            get("/setup/password", ProfileController::hasPassword);
            post("/setup/password", ProfileController::savePassword);
            post("/setup/create", ProfileController::create);

            path("/users", () -> {
                // user api routes.
                before("/*", Middleware::isAdminAuthenticated);
                before("", Middleware::isAdminAuthenticated);
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

                // No authentication required.
                //post("/create", ProfileController::create);

                // Profile authentication required.
                before("", Middleware::isAuthenticated);
                before("/:id", Middleware::isAuthenticated);

                get("", ProfileController::get);



                path("/:id", () -> {
                    patch("", ProfileController::edit);
                    path("/blood-donation", () -> post("", ProfileController::updateBloodDonation));

                    // organs api endpoints.
                    path("/organs", () -> {
                        get("", OrganController::getAll);
                        post("", OrganController::add);
                        delete("", OrganController::delete);
                    });

                    // condition api endpoints.
                    path("/conditions", () -> {
                        get("", ConditionController::getAll);
                    });

                    // procedure api endpoints.
                    path("/procedures", () -> {
                        get("", ProcedureController::getAll);
                    });
                });

                // Admin or clinician authentication required.
                get("/all", ProfileController::getAll);
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

            // condition api endpoints.
            path("/conditions", () -> {
                before("/*", Middleware::isAdminAuthenticated);

                path("/:id", () -> {
                    patch("", ConditionController::edit);
                    delete("", ConditionController::delete);
                });
            });

            // procedure api endpoints.
            path("/procedures", () -> {
                before("/*", Middleware::isAdminAuthenticated);

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
                before("/*", Middleware::isAdminAuthenticated);

                path("/:id", () -> {
                    patch("", DrugController::edit);
                    delete("", DrugController::delete);
                });
            });

            // countries api endpoints.
            path("/settings", () -> {
                before("/*", Middleware::isAuthenticated);

                // countries api endpoints.
                get("/countries", SettingsController::getAllCountries);
                patch("/countries", SettingsController::editCountries);

                // locale api endpoints.
                get("/locale", SettingsController::getLocale);
                post("/locale", SettingsController::setLocale);
            });

            // hospitals api endpoints.
            path("/hospitals", () -> {
                before("/*", Middleware::isAdminAuthenticated);
                before("", Middleware::isAdminAuthenticated);

                get("/all", HospitalController::getAll);
                get("", HospitalController::get);
                post("", HospitalController::create);
                patch("", HospitalController::edit);
                delete("", HospitalController::delete);
            });

            // hla api endpoints
            path("/hla", () -> {
                before("/*", Middleware::isAuthenticated);
                before("", Middleware::isAuthenticated);

                // id references profile
                path("/:id", () -> {
                    get("", HLAController::get);
                    post("", HLAController::add);
                    delete("", HLAController::delete);
                });
            });
        });
    }
}
