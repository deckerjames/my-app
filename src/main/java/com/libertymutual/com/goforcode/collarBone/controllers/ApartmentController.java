package com.libertymutual.com.goforcode.collarBone.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.libertymutual.com.goforcode.collarBone.models.Apartment;
import com.libertymutual.com.goforcode.collarBone.utilities.MustacheRenderer;

import spark.Request;
import spark.Response;
import spark.Route;

public class ApartmentController {

    public static final Route details = (Request req, Response res) -> {
        String idAsString = req.params("id");
        int id = Integer.parseInt(idAsString);
        Apartment apartment = Apartment.findById(id);
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("apartment", apartment);
        return MustacheRenderer.getInstance().render("apartment/details.html", model);
    };
}
