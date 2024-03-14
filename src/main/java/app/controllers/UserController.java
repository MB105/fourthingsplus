package app.controllers;

import app.entities.Task;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.TaskMapper;
import app.persistence.UserMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;


public class UserController {

    public static void addRoutes(Javalin app, ConnectionPool connectionpool) {

        app.post("/login", ctx -> login(ctx, connectionpool));
        app.get("logout",ctx->logout(ctx));
        app.get("createuser",ctx->ctx.render("createuser.html"));
        app.post("createuser",ctx->createUser(ctx,connectionpool));

    }

    private static void createUser(Context ctx,ConnectionPool connectionPool) {

        String username = ctx.formParam("username");
        String password1 = ctx.formParam("password1");
        String password2 = ctx.formParam("password2");

        if (password1.equals(password2)) {

            try {
                UserMapper.createuser(username, password1, connectionPool);
                ctx.attribute("message", "Du er hermed oprettet med brugernavn:" + username);
                ctx.render("index.html");


            } catch (DatabaseException e) {
                ctx.attribute("message", "Dit brugernavn findes allerede!Prøv igen eller login");
                ctx.render("createuser.html");
            }

        } else {

            ctx.attribute("message", "Dine to passwords matcher ikke!Prøv igen");
            ctx.render("createuser.html");

        }
    }
    private static void logout(Context ctx) {

        ctx.req().getSession().invalidate();
        ctx.render("index.html");
    }

    public static void login(Context ctx, ConnectionPool connectionPool) {

        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        try {
            User user = UserMapper.login(username, password, connectionPool);
            ctx.sessionAttribute("currentUser",user);

            List<Task> taskList= TaskMapper.getAllTasksPerUser(user.getUserId(),connectionPool);
            ctx.attribute("taskList",taskList);

            ctx.render("task.html");

        } catch (DatabaseException e) {

            ctx.attribute("message", e.getMessage());


            ctx.render("index.html");
        }


    }
}