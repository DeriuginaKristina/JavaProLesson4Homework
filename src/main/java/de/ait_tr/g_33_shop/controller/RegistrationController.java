package de.ait_tr.g_33_shop.controller;

import de.ait_tr.g_33_shop.domain.entity.User;
import de.ait_tr.g_33_shop.exception_handling.Response;
import de.ait_tr.g_33_shop.service.interfaces.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
public class RegistrationController {

    private final UserService service;

    public RegistrationController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public Response register(@RequestBody User user) {
        service.register(user);
        return new Response("Registration complete. Please check your email.");
    }

    @GetMapping
    public Response validateCode(@RequestParam String  code){
        service.validateCode(code);
        return new Response("Thank you for registration");
    }

}
