package com.libertymutual.com.goforcode.collarBone.controllers;

import com.libertymutual.com.goforcode.collarBone.models.Apartment;
import com.libertymutual.com.goforcode.collarBone.utilities.AutoCloseableDb;
import com.libertymutual.com.goforcode.collarBone.utilities.JsonHelper;

import static spark.Spark.notFound;
import spark.Request;
import spark.Response;
import spark.Route;

public class ApartmentApiController {

        public static final Route details = (Request req, Response res) -> {
                try(AutoCloseableDb db = new AutoCloseableDb()) {
                        String idString = req.params("id");
                        int id = Integer.parseInt(idString);
                        Apartment apartment = Apartment.findById(id);
                        if(apartment != null) {
                                res.header("Content-Type", "application/json");
                                return apartment.toJson(true);
                        }
                        notFound("Failed to find an apartment");
                        return "";
                }
        };

        public static final Route create = (Request req, Response res) -> {
                Apartment apartment = new Apartment();
                String apartmentJson = req.body(); 
                apartment.fromMap(JsonHelper.toMap(apartmentJson));

                try(AutoCloseableDb db = new AutoCloseableDb()) {
                        apartment.saveIt();
                        res.status(201);
                        return apartment.toJson(true);
                }
        };
}