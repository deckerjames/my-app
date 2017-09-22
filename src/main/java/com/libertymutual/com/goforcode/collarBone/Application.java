package com.libertymutual.com.goforcode.collarBone;

import static spark.Spark.*;
import org.mindrot.jbcrypt.BCrypt;

import com.libertymutual.com.goforcode.collarBone.controllers.ApartmentApiController;
import com.libertymutual.com.goforcode.collarBone.controllers.ApartmentController;
import com.libertymutual.com.goforcode.collarBone.controllers.HomeController;
import com.libertymutual.com.goforcode.collarBone.controllers.SessionController;
import com.libertymutual.com.goforcode.collarBone.controllers.UserController;
import com.libertymutual.com.goforcode.collarBone.filters.SecurityFilters;
import com.libertymutual.com.goforcode.collarBone.models.Apartment;
import com.libertymutual.com.goforcode.collarBone.models.ApartmentsUsers;
import com.libertymutual.com.goforcode.collarBone.models.User;
import com.libertymutual.com.goforcode.collarBone.utilities.AutoCloseableDb;

public class Application {

    public static void main(String[] args) {
        String encryptedPW = BCrypt.hashpw("password", BCrypt.gensalt());

        try (AutoCloseableDb db = new AutoCloseableDb()) {
            ApartmentsUsers.deleteAll();
            User.deleteAll();
            Apartment.deleteAll();
        }

        before("/*", SecurityFilters.isNewSession);

        path("/apartments", () -> {
            before("/new", SecurityFilters.isAuthenticated);
            before("/mine", SecurityFilters.isAuthenticated);
            before("/:id/likes", SecurityFilters.isAuthenticated);
            before("/:id/activations", SecurityFilters.isOwner);
            before("/:id/deactivations", SecurityFilters.isOwner);
            before("", SecurityFilters.isAuthenticated);

            get("/new", ApartmentController.newForm);
            get("/mine", ApartmentController.index);
            before("/:id/deactivations", SecurityFilters.checkCsrf);
            post("/:id/deactivations", ApartmentController.deactivate);
            before("/:id/activations", SecurityFilters.checkCsrf);
            post("/:id/activations", ApartmentController.activate);
            before("/:id/likes", SecurityFilters.checkCsrf);
            post("/:id/likes", ApartmentController.like);
            get("/:id", ApartmentController.details);
            before("", SecurityFilters.checkCsrf);
            post("", ApartmentController.create);
        });

        path("/users", () -> {
            get("/new", UserController.newForm);
            before("/new", SecurityFilters.checkCsrf);
            post("/new", UserController.create);
        });

        path("/api", () -> {
            get("/apartments/:id", ApartmentApiController.details);
            post("/apartments", ApartmentApiController.create);
        });

        get("/", HomeController.index);
        get("/login", SessionController.newForm);
        before("/login", SecurityFilters.checkCsrf);
        post("/login", SessionController.create);
        before("/logout", SecurityFilters.checkCsrf);
        post("/logout", SessionController.destroy);
    }
}