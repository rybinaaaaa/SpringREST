package com.rybina.firstRestAPI.controllers;

import com.rybina.firstRestAPI.models.Person;
import com.rybina.firstRestAPI.services.PeopleService;
import com.rybina.firstRestAPI.util.PersonErrorResponse;
import com.rybina.firstRestAPI.util.PersonNotCreatedException;
import com.rybina.firstRestAPI.util.PersonNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/people")
public class PeopleController {

    private final PeopleService peopleService;

    @Autowired
    public PeopleController(PeopleService peopleService) {
        this.peopleService = peopleService;
    }

    @GetMapping()
    public List<Person> getPeople() {
        return peopleService.findAll(); // Jackson конвертирует эти объекты в JSON
    }

    @GetMapping("/{id}")
    public Person getPerson(@PathVariable("id") int id) {
        return peopleService.findOne(id); // Jackson конвертирует в JSON
    }

    @PostMapping
    public ResponseEntity<HttpStatus> create(@RequestBody @Valid Person person, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder();

            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError e : errors) {
                errorMsg.append(e.getField())
                        .append(" - ")
                        .append(e.getDefaultMessage())
                        .append(";");
            }

            throw new PersonNotCreatedException(errorMsg.toString());
        }
        peopleService.save(person);

        return ResponseEntity.ok(HttpStatus.OK);
    }


    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotFoundException e) {
        PersonErrorResponse response = new PersonErrorResponse("Person with this id wasn't found",
                System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<PersonErrorResponse> handleException(PersonNotCreatedException e) {
        PersonErrorResponse response = new PersonErrorResponse("Person wasn't created",
                System.currentTimeMillis());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}