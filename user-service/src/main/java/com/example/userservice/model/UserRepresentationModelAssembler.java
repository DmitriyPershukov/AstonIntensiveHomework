package com.example.userservice.model;

import com.example.userservice.controller.UserController;
import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.mediatype.Affordances;
import org.springframework.hateoas.server.SimpleRepresentationModelAssembler;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.afford;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class UserRepresentationModelAssembler
        implements SimpleRepresentationModelAssembler<UserDto> {

    @Override
    public void addLinks(EntityModel<UserDto> resource) {
        resource.add(linkTo(UserController.class)
                .slash(resource.getContent().id())
                .withSelfRel());
        resource.add(linkTo(methodOn(UserController.class)
                .getAllUsers())
                .withRel("users"));
    }

    @Override
    public void addLinks(CollectionModel<EntityModel<UserDto>> resources) {
        resources.add(linkTo(methodOn(UserController.class)
                .getAllUsers())
                .withSelfRel());
    }
}
