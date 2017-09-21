package com.libertymutual.com.goforcode.collarBone.filters;

import static spark.Spark.before;
import static spark.Spark.halt;

import com.libertymutual.com.goforcode.collarBone.models.Apartment;
import com.libertymutual.com.goforcode.collarBone.models.User;
import com.libertymutual.com.goforcode.collarBone.utilities.AutoCloseableDb;

import spark.Filter;
import spark.Request;
import spark.Response;

public class SecurityFilters {

    public static Filter isAuthenticated = (Request req, Response res) -> {
        if (req.session().attribute("currentUser") == null) {
            res.redirect("/login?returnPath=" + req.pathInfo());
            halt();
        }
    };

    public static Filter isOwner = (Request req, Response res) -> {
        User currentUser = req.session().attribute("currentUser");
        try (AutoCloseableDb db = new AutoCloseableDb()) {
            Apartment apartment = Apartment.findById(Integer.parseInt(req.params("id")));

            if (currentUser == null || apartment.get("user_id") == currentUser.getId()) {
                res.redirect("/login?returnPath=/apartments/" + apartment.get("id"));
                halt();
            }
        }
    };

}