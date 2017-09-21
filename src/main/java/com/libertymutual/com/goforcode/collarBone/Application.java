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
            User luka = new User("luka@gmail", encryptedPW, "Luka", "Ralic");
            luka.saveIt();

            Apartment.deleteAll();
            Apartment apartment = new Apartment(62000, 1, 0, 350, "123 Main St", "San Francisco", "CA", "95215");
            luka.add(apartment);
            apartment.saveIt();

            apartment = new Apartment(1459, 5, 6, 4000, "123 Cowboy Way", "Houston", "TX", "77006");
            apartment.set("is_active", false);
            luka.add(apartment);
            apartment.saveIt();

            encryptedPW = BCrypt.hashpw("t", BCrypt.gensalt());
            User test = new User("test@hotmail.com", encryptedPW, "first", "last");
            test.saveIt();
            apartment.add(test);
        }

        path("/apartments", () -> {
            before("/new", SecurityFilters.isAuthenticated);
            before("/mine", SecurityFilters.isAuthenticated);
            before("", SecurityFilters.isAuthenticated);

            get("/new", ApartmentController.newForm);
            get("/mine", ApartmentController.index);
            post("/:id/deactivations", ApartmentController.deactivate);
            post("/:id/activations", ApartmentController.activate);
            post("/:id/like", ApartmentController.like);
            get("/:id", ApartmentController.details);
            post("", ApartmentController.create);
        });

        path("/users", () -> {
            get("/new", UserController.newForm);
            post("/new", UserController.create);
        });

        path("/api", () -> {
            get("/apartments/:id", ApartmentApiController.details);
            post("/apartments", ApartmentApiController.create);
        });

        get("/", HomeController.index);
        get("/login", SessionController.newForm);
        post("/login", SessionController.create);
        get("/logout", SessionController.destroy);
    }
}