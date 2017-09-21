package com.libertymutual.com.goforcode.collarBone.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.libertymutual.com.goforcode.collarBone.models.Apartment;
import com.libertymutual.com.goforcode.collarBone.models.ApartmentsUsers;
import com.libertymutual.com.goforcode.collarBone.models.User;
import com.libertymutual.com.goforcode.collarBone.utilities.AutoCloseableDb;
import com.libertymutual.com.goforcode.collarBone.utilities.MustacheRenderer;
import spark.Request;
import spark.Response;
import spark.Route;

public class ApartmentController {

    public static final Route details = (Request req, Response res) -> {
        try (AutoCloseableDb db = new AutoCloseableDb()) {
            Apartment apartment = Apartment.findById(Integer.parseInt(req.params("id")));
            List<User> likes = apartment.getAll(User.class);
            User currentUser = req.session().attribute("currentUser");
            boolean canLike = false;
            boolean isOwner = false;

            if (currentUser != null && apartment.get("user_id").equals(currentUser.getId()) == false && ApartmentsUsers.where("apartment_id = ? and user_id = ?", apartment.getId(), currentUser.getId()).isEmpty()) {
                canLike = true;
            }

            if (currentUser != null && apartment.get("user_id").equals(currentUser.getId())) {
                isOwner = true;
            }

            Map<String, Object> model = new HashMap<String, Object>();
            model.put("likes", likes);
            model.put("apartment", apartment);
            model.put("currentUser", req.session().attribute("currentUser"));
            model.put("noUser", req.session().attribute("currentUser") == null);
            model.put("canLike", canLike);
            model.put("isOwner", isOwner);
            model.put("isActive", apartment.get("is_active"));
            model.put("notActive", apartment.get("is_active").equals(false));
            return MustacheRenderer.getInstance().render("apartment/details.html", model);
        }
    };

    public static final Route newForm = (Request req, Response res) -> {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("currentUser", req.session().attribute("currentUser"));
        model.put("noUser", req.session().attribute("currentUser") == null);
        return MustacheRenderer.getInstance().render("apartment/newForm.html", model);

    };

    public static final Route create = (Request req, Response res) -> {
        Apartment apt = new Apartment(Integer.parseInt(req.queryParams("rent")), Integer.parseInt(req.queryParams("number_of_bedrooms")), Double.parseDouble(req.queryParams("number_of_bathrooms")), Integer.parseInt(req.queryParams("square_footage")), req.queryParams("address"), req.queryParams("city"), req.queryParams("state"), req.queryParams("zip_code"));

        try (AutoCloseableDb db = new AutoCloseableDb()) {
            User currentUser = req.session().attribute("currentUser");
            currentUser.add(apt);
            apt.saveIt();
            res.redirect("/");
            return "";
        }

    };

    public static final Route index = (Request req, Response res) -> {
        try (AutoCloseableDb db = new AutoCloseableDb()) {
            User currentUser = req.session().attribute("currentUser");
            // List<Apartment> apartments = Apartment.where("user_id = ?", currentUser.getId());
            List<Apartment> active = Apartment.where("user_id = ? and is_active = ?", currentUser.getId(), true);
            List<Apartment> inactive = Apartment.where("user_id = ? and is_active = ?", currentUser.getId(), false);

            Map<String, Object> model = new HashMap<String, Object>();

            model.put("isActive", active);
            model.put("notActive", inactive);

            model.put("currentUser", req.session().attribute("currentUser"));
            model.put("noUser", currentUser == null);
            return MustacheRenderer.getInstance().render("apartment/index.html", model);
        }
    };

    public static final Route deactivate = (Request req, Response res) -> {
        try (AutoCloseableDb db = new AutoCloseableDb()) {
            int id = Integer.parseInt(req.params("id"));
            Apartment apartment = Apartment.findById(id);
            apartment.set("is_active", false);
            apartment.saveIt();
            res.redirect("/apartments/" + id);
            return "";
        }
    };

    public static final Route activate = (Request req, Response res) -> {
        try (AutoCloseableDb db = new AutoCloseableDb()) {
            int id = Integer.parseInt(req.params("id"));
            Apartment apartment = Apartment.findById(id);
            apartment.set("is_active", true);
            apartment.saveIt();
            res.redirect("/apartments/" + id);
            return "";
        }
    };

    public static final Route like = (Request req, Response res) -> {
        try (AutoCloseableDb db = new AutoCloseableDb()) {
            User currentUser = req.session().attribute("currentUser");
            int id = Integer.parseInt(req.params("id"));
            Apartment apartment = Apartment.findById(id);
            apartment.add(currentUser);
            res.redirect("/apartments/" + id);
            return "";
        }
    };

}