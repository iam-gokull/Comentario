package com.app.comentarioserver.service;

import com.app.comentarioserver.dto.AuthRequest;
import com.app.comentarioserver.dto.UserRequest;
import com.app.comentarioserver.entity.Board;
import com.app.comentarioserver.entity.Feedback;
import com.app.comentarioserver.imagekit_upload.ImageUpload;
import com.app.comentarioserver.pojo.ImageData;
import com.app.comentarioserver.pojo.Token;
import com.app.comentarioserver.entity.User;
import com.app.comentarioserver.exception.InvalidTokenException;
import com.app.comentarioserver.exception.UserAlreadyExistsException;
import com.app.comentarioserver.exception.UserNotEnabledException;
import com.app.comentarioserver.jwt.JwtTokenProvider;
import com.app.comentarioserver.repository.BoardRepository;
import com.app.comentarioserver.repository.FeedbackRepository;
import com.app.comentarioserver.repository.UserRepository;
import io.imagekit.sdk.exceptions.*;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

import static com.app.comentarioserver.jwt.JwtTokenFilter.HEADER_PREFIX;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    private final BoardRepository boardRepository;
    private final FeedbackRepository feedbackRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final JavaMailSender mailSender;

    private final ImageUpload imageUpload;

    private final PasswordEncoder encoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    // Delete everything from above

    public User addUser(UserRequest userRequest) {
        if (checkIfMailIdExists(userRequest.getMailId())) {
            throw new UserAlreadyExistsException("User already exists with this Email");
        }

        if (checkIfUsernameExists(userRequest.getUsername())) {
            throw new UserAlreadyExistsException("User already exists with Username");
        }

        User user = new User(userRequest.getFullName(), userRequest.getUsername(), userRequest.getMailId(), encoder.encode(userRequest.getPassword()), userRequest.getImageData());
        user.setRoles(List.of(new SimpleGrantedAuthority("User")));
        sendVerificationMail(user);
        return userRepository.save(user);
    }

    public void sendVerificationMail(User user) {
        user.setVerified(false);
        Token token = new Token();
        user.setVerificationToken(token);

        String verificationTokenValue = user.getVerificationToken().getUserToken();
        String to = user.getMailId();
        String subject = "Welcome, " + user.getFullName();
        String htmlContent = """
                <html>
                <body>
                <h1>Welcome</h1>
                <p>Please click the below button to verify your account</p>""" +
                "<a href=\"http://localhost:8080/users/verify-register-token?token=" + verificationTokenValue + "&email=" + user.getMailId() + "\">Verify Email</a>" +
                """
                </body>
                </html>""";

        sendEmail(to, subject, htmlContent);
    }

    public void sendEmail(String to, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper;
        try {
            mimeMessageHelper = new MimeMessageHelper(message, true, "UTF-8");
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setText(htmlContent, true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        mailSender.send(message);
    }

    private boolean checkIfMailIdExists(String mailId) {
        return userRepository.findByMailId(mailId).isPresent();
    }

    private boolean checkIfUsernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public String fetchPasswordResetOtp(String mailId) {
        int number = new Random().nextInt(999999);
        String otp = String.format("%06d", number);

        String subject = "Password reset";
        String htmlContent = """
                <html>
                <body>
                <h1>Welcome</h1>
                <p>Here is your one time token for password reset</p>
                <p>It'll expire in a day, kindly don't share it with anyone</p>""" +
                otp +
                """
                </body>
                </html>
                        """;
        sendEmail(mailId, subject, htmlContent);
        return otp;
    }

    public boolean resetPassword(String mailId, String password) {
        User user = getByMailId(mailId);
        user.setPassword(encoder.encode(password));
        userRepository.save(user);
        return true;
    }

    public String loginUser(AuthRequest authRequest, Authentication authentication) {

        String jwtToken = jwtTokenProvider.createToken(authentication);

        if (!checkUserVerification(authRequest.getIdentifier())) {
            Token token = new Token();
            User user = loadByIdentifier(authRequest.getIdentifier());
            user.setVerificationToken(token);

            String verificationTokenValue = user.getVerificationToken().getUserToken();
            String to = user.getMailId();
            String subject = "Welcome, " + user.getFullName();
            String htmlContent = """
                <html>
                <body>
                <h1>Welcome</h1>
                <p>Please click the below button to verify your account</p>""" +
                    "<a href=\"http://localhost:8080/users/verify-register-token?token=" + verificationTokenValue + "&email=" + user.getMailId() + "\">Verify Email</a>" +
                    """
                    </body>
                    </html>""";

            sendEmail(to, subject, htmlContent);
            userRepository.save(user);
            throw new UserNotEnabledException("User not verified");
        }
        return jwtToken;
    }

    public boolean checkUserVerification(String identifier) {
        User user = loadByIdentifier(identifier);
        return user.isEnabled();
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) {
        return loadByIdentifier(identifier);
    }

    public User loadByIdentifier(String identifier) {
        if (Pattern.compile("^(.+)@(\\S+)$").matcher(identifier).matches()) {
            return getByMailId(identifier);
        } else {
            return getUserByUsername(identifier);
        }
    }

    public String getUsernameFromMailId(String mailId) {
        return loadByIdentifier(mailId).getUsername();
    }

    public String getUsernameFromToken(String token) {
        String jwtToken = token.substring(HEADER_PREFIX.length()).trim();

        if (jwtTokenProvider.validateToken(jwtToken)) {
            return jwtTokenProvider.getUsernameFromToken(jwtToken);
        } else {
            throw new InvalidTokenException("Token is invalid");
        }
    }

    public boolean validateVerificationToken(String token, String mailId) {
        User user = userRepository.findByMailId(mailId).orElseThrow();
        Token verificationToken = user.getVerificationToken();

        if (!Objects.equals(token, verificationToken.getUserToken())) {
            log.info(("token not matching"));
            return false;
        }

        if (verificationToken.getExpiryDate().before(new Date())) {
            log.info(("token expired"));
            return false;
        }

        updateUserVerification(user);
        return true;
    }

    public void updateUserVerification(User user) {
        user.setVerified(true);
        userRepository.save(user);
    }

    public User updateFullName(String username, String fullName) {
        User user = loadByIdentifier(username);
        user.setFullName(fullName.replace('"', ' ').trim());
        return userRepository.save(user);
    }

    public User updateUsername(String username, String newUsername) {
        User user = loadByIdentifier(username);
        username = user.getUsername();
        newUsername = newUsername.replace('"', ' ').trim();
        if (checkIfUsernameExists(newUsername)) {
            throw new UserAlreadyExistsException("User already exists with Username");
        }
        user.setUsername(newUsername);
        User updatedUser = userRepository.save(user);
        log.info(username);
        updateBoardUsername(username, updatedUser.getUsername());
        updateFeedbackUsername(username, updatedUser.getUsername());
        return updatedUser;
    }

    public User updateMailId(String username, String mailId) {
        User user = loadByIdentifier(username);
        mailId = mailId.replace('"', ' ').trim();
        if (checkIfMailIdExists(mailId)) {
            throw new UserAlreadyExistsException("User already exists with this Email");
        }
        user.setMailId(mailId);
        sendVerificationMail(user);
        return userRepository.save(user);
    }

    public User updatePassword(String username, String password) {
        User user = loadByIdentifier(username);
        user.setPassword(encoder.encode(password.replace('"', ' ').trim()));
        return userRepository.save(user);
    }

    public void updateBoardUsername(String username, String newUsername) {
        boardRepository.findByUsername(username).ifPresent((board) -> {
            board.setUsername(newUsername);
            boardRepository.save(board);
        });

    }

    public User updateProfileImage(String username, MultipartFile file) throws ForbiddenException, TooManyRequestsException, InternalServerException, UnauthorizedException, BadRequestException, UnknownException, IOException {
        User user = loadByIdentifier(username);
        imageUpload.deleteImage(user.getImageData().getImageId());
        ImageData imageData = imageUpload.uploadImage(file, "User-profile/");
        user.setImageData(imageData);
        updateFeedbackUserProfile(username, imageData.getImageUrl());
        return userRepository.save(user);
    }

    public void updateFeedbackUsername(String username, String newUsername) {
        feedbackRepository.findByUsername(username).ifPresent((feedback -> {
            feedback.setUsername(newUsername);
            feedback.getComments().forEach(comment -> {
                if (comment.getUsername().equals(username)) {
                    comment.setUsername(newUsername);
                }
            });
            log.info(feedback.getUsername());
            feedbackRepository.save(feedback);
        }));
    }

    public void updateFeedbackUserProfile(String username, String profileUrl) {
        Feedback feedback = feedbackRepository.findByUsername(username).orElseThrow();
        feedback.setProfileUrl(profileUrl);
        feedback.getComments().forEach(comment -> {
            if (comment.getUsername().equals(username)) {
                comment.setProfileUrl(profileUrl);
            }
        });

        feedbackRepository.save(feedback);
    }

    public User getByMailId(String mailId) {
        Optional<User> optionalUser = userRepository.findByMailId(mailId);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("Email does not exist");
        }

        return optionalUser.get();
    }

    public User getUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("Username does not exist");
        }

        return optionalUser.get();
    }

    public void addBoardToTheUser(Board board, String username) {
        User user = getUserByUsername(username);
        user.setBoards(board);
        userRepository.save(user);
    }

}
