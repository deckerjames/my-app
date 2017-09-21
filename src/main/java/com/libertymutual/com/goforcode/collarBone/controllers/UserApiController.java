package com.libertymutual.com.goforcode.collarBone.controllers;

import static spark.Spark.notFound;

import java.util.HashMap;
import java.util.Map;

import com.libertymutual.com.goforcode.collarBone.models.Apartment;
import com.libertymutual.com.goforcode.collarBone.models.User;
import com.libertymutual.com.goforcode.collarBone.utilities.AutoCloseableDb;
import com.libertymutual.com.goforcode.collarBone.utilities.JsonHelper;
import com.libertymutual.com.goforcode.collarBone.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class UserApiController {

    public static final Route details = (Request req, Response res) -> {

        try (AutoCloseableDb db = new AutoCloseableDb()) {
            String idAsString = req.params("id");
            int id = Integer.parseInt(idAsString);
            User user = User.findById(id);
            if (user != null) {
                res.header("Content-Type", "application/json");
                return user.toJson(true);
            }
            notFound("Did not find that");
            return "";
        }

    };

    public static Route create = (Request req, Response res) -> {

        String json = req.body();
        Map map = JsonHelper.toMap(json);
        User user = new User();
        user.fromMap(map);

        try (AutoCloseableDb db = new AutoCloseableDb()) {
            user.saveIt();
            res.status(201);
            return user.toJson(true);
        }
    };
}
