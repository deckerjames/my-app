package com.libertymutual.com.goforcode.collarBone.controllers;

import java.util.HashMap;
import java.util.Map;

import org.mindrot.jbcrypt.BCrypt;
import com.libertymutual.com.goforcode.collarBone.models.User;
import com.libertymutual.com.goforcode.collarBone.utilities.AutoCloseableDb;
import com.libertymutual.com.goforcode.collarBone.utilities.MustacheRenderer;
import spark.Request;
import spark.Response;
import spark.Route;

public class UserController {

    public static final Route newForm = (Request req, Response res) -> {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("currentUser", req.session().attribute("currentUser"));
        model.put("noUser", req.session().attribute("currentUser") == null);
        model.put("csrf", req.session().attribute("csrf"));
        return MustacheRenderer.getInstance().render("user/newForm.html", model);
    };

    public static final Route create = (Request req, Response res) -> {
        String encryptedPW = BCrypt.hashpw(req.queryParams("password"), BCrypt.gensalt());
        User user = new User(req.queryParams("email"), encryptedPW, req.queryParams("first_name"), req.queryParams("last_name"));

        try (AutoCloseableDb db = new AutoCloseableDb()) {
            user.saveIt();
            req.session().attribute("currentUser", user);
            res.redirect("/");
            return "";
        }
    };
}