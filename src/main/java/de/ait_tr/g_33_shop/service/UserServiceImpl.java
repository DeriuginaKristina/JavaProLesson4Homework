package de.ait_tr.g_33_shop.service;

import de.ait_tr.g_33_shop.domain.entity.ConfirmationCode;
import de.ait_tr.g_33_shop.domain.entity.Role;
import de.ait_tr.g_33_shop.domain.entity.User;
import de.ait_tr.g_33_shop.exception_handling.CodeNotFoundException;
import de.ait_tr.g_33_shop.exception_handling.exceptions.UserAlreadyExistsException;
import de.ait_tr.g_33_shop.repository.ConfirmationCodeRepository;
import de.ait_tr.g_33_shop.repository.UserRepository;
import de.ait_tr.g_33_shop.service.interfaces.EmailService;
import de.ait_tr.g_33_shop.service.interfaces.RoleService;
import de.ait_tr.g_33_shop.service.interfaces.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ConfirmationCodeRepository confirmationCodeRepository;
    private final BCryptPasswordEncoder encoder;
    private final RoleService roleService;
    private final EmailService emailService;

    public UserServiceImpl(UserRepository userRepository, ConfirmationCodeRepository confirmationCodeRepository, BCryptPasswordEncoder encoder, RoleService roleService, EmailService emailService) {
        this.userRepository = userRepository;
        this.confirmationCodeRepository = confirmationCodeRepository;
        this.encoder = encoder;
        this.roleService = roleService;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format("User %s not found", username))
        );
    }

    @Override
    public void register(User user) {
        //** note that user can regestration not thirst time fo ex/ if 1 time he dont have time for this
        user.setId(null);
        user.setPassword(encoder.encode(user.getPassword()));

        Role userRole = roleService.getRoleUser();
        user.setRoles(Set.of(userRole));
        user.setActive(false);
        //TODO all error processing

        try {
            //   if(!user.getUsername().matches(existing)) userRepository.save(user);
            if (!userRepository.existsByUsername(user.getUsername()))
                userRepository.save(user);
        } catch (Exception e) {
            throw new UserAlreadyExistsException(e.getMessage());
        }
        emailService.sendConfirmationEmail(user);

    }

    @Override
    public void validateCode(String code) {
        Optional<ConfirmationCode> confirmationCode = Optional.ofNullable
                (confirmationCodeRepository.findByCode(code).orElseThrow(
                        () -> new CodeNotFoundException(String.format("Code %s not found", code))
        ));
        System.out.println(confirmationCode);

        confirmationCode.ifPresent(codeDetails -> {
            if (codeDetails.getExpired().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("Code has expired");
            }

            if (!codeDetails.isUserActivated()) {
                // Activate the user
                codeDetails.activateUser();
                confirmationCodeRepository.save(codeDetails);  // Save the updated state to the repository
            }
        });
    }
        }

            //TODO method if code had been expired and user has not been activated
            //if time is expected 20.46 throw NewRunTimeException


            //TODO user will be active after all checking


//public class CodeNotFoundException extends RuntimeException {
//    public CodeNotFoundException(String message) {
//        super(message);
//    }
//}
//
//    @Override
//    public void validateCode(String code) {
//        Optional<ConfirmationCode> confirmationCode = Optional.ofNullable(
//                confirmationCodeRepository.findByCode(code).orElseThrow(
//                        () -> new CodeNotFoundException(String.format("Code %s not found", code))
//                )
//        );
//        System.out.println(confirmationCode);
//
//        confirmationCode.ifPresent(codeDetails -> {
//            // Assuming codeDetails has a method getExpirationTime() that returns a LocalDateTime
//            if (codeDetails.getExpirationTime().isBefore(Instant.from(LocalDateTime.now()))) {
//                throw new RuntimeException("Code has expired");
//            }
//
//            // Assuming codeDetails has a method isUserActivated() that returns a boolean
//            if (!codeDetails.isUserActivated()) {
//                // Activate the user
//                // Assuming codeDetails has a method activateUser() to activate the user
//                codeDetails.activateUser();
//                confirmationCodeRepository.save(codeDetails);  // Save the updated state to the repository
//            }
//        });
//    }

//    @Override
//    public void validateCode(String code) {
//        Optional<ConfirmationCode> confirmationCode = Optional.ofNullable
//                (confirmationCodeRepository.findByCode(code).orElseThrow(
//                        () -> new RuntimeException("Code not found")// TODO use String.format please
////               ()-> new CodeNotFoundException()// TODO create CodeNotFoundException please
//                ));
//        System.out.println(confirmationCode);
//        //TODO method if code had been expired and user has not been activated
//        //if time is expected 20.46 throw NewRunTimeException
//
//
//        //TODO user will be active after all checking
