package com.libertymutual.com.goforcode.collarBone.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.libertymutual.com.goforcode.collarBone.models.Apartment;
import com.libertymutual.com.goforcode.collarBone.utilities.AutoCloseableDb;
import com.libertymutual.com.goforcode.collarBone.utilities.MustacheRenderer;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;

public class HomeController {

    public static final Route index = (Request req, Response res) -> {

        try (AutoCloseableDb db = new AutoCloseableDb()) {
            List<Apartment> activeApartments = Apartment.where("is_active = true");
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("activeApartments", activeApartments);
            model.put("currentUser", req.session().attribute("currentUser"));
            model.put("noUser", req.session().attribute("currentUser") == null);
            model.put("csrf", req.session().attribute("csrf"));
            return MustacheRenderer.getInstance().render("home/index.html", model);
        }
    };

}