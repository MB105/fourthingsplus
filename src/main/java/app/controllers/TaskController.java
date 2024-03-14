package app.controllers;

import app.entities.Task;
import app.entities.User;
import app.exceptions.DatabaseException;
import app.persistence.ConnectionPool;
import app.persistence.TaskMapper;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;



public class TaskController {



    public static void addRoutes(Javalin app, ConnectionPool connectionpool) {

        app.post("addtask", ctx -> addtask(ctx, connectionpool));

    }

    private static void addtask(Context ctx, ConnectionPool connectionpool) {

        String task= ctx.formParam("task");

        User user=ctx.sessionAttribute("currentUser");
        try {
            Task newTask= TaskMapper.addTask(user,task,connectionpool);
            List<Task> taskList= TaskMapper.getAllTasksPerUser(user.getUserId(),connectionpool);
            ctx.attribute("taskList",taskList);

            ctx.render("task.html");

        } catch (DatabaseException e) {
            ctx.attribute("message","Noget gik galt. Prøv eventuelt igen!");
            ctx.render("task.html");
        }
    }
}
