package com.example.rest;

import com.example.entity.Todo;
import com.example.service.TodoService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("todo")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TodoRest {

    @Inject
    private TodoService todoService;

    @POST
    public Response create(Todo todo) {
        todoService.create(todo);
        return Response.status(Response.Status.CREATED)
                .build();
    }

    @PATCH
    public Response update(Todo todo) {
        todoService.update(todo);
        return Response.status(Response.Status.NO_CONTENT)
                .build();
    }

    @Path("{id}")
    @GET
    public Todo findById(@PathParam("id") Long id) {
        return todoService.findById(id);
    }

    @Path("findall")
    @GET
    public List<Todo> findAll() {
        return todoService.findAll();
    }
}
