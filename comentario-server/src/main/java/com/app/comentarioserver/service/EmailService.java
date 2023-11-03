package com.app.comentarioserver.service;

import com.app.comentarioserver.entity.Board;
import com.app.comentarioserver.entity.Feedback;
import com.app.comentarioserver.entity.User;
import com.app.comentarioserver.pojo.Token;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final UserService userService;

    private final JavaMailSender mailSender;

    private final BoardService boardService;


    public void sendRoadmapUpdatedMail(Feedback feedback) {
        User user = userService.getUserByUsername(feedback.getUsername());
        Board board = boardService.getBoardById(feedback.getBoardId());
        String to = user.getMailId();
        String subject = "Yay, your feedback is considered";
        String htmlContent = """
                <html>
                <body>
                """ +
                "<h1>Hello again, " + user.getFullName() + "</h1> " +
                "<p>Thanks for the feedback</p>" +
                "<p>Your feedback is being considered for" + board.getTitle() + "</p>" +
                "<a href=\"http://localhost:5173/roadmap/" + board.getId() + "\">Click here</a>" +
                """
                </body>
                </html>""";

        sendEmail(to, subject, htmlContent);
    }

    public void sendFeedbackMail(Feedback feedback) {
        Board board = boardService.getBoardById(feedback.getBoardId());
        User user = userService.getUserByUsername(board.getUsername());
        String to = user.getMailId();
        String subject = "You've a new feedback for " + board.getTitle();
        String htmlContent = """
                <html>
                <body>
                """ +
                "<h1>Hello again, " + user.getFullName() + "</h1> " +
                "<p>You've got a new feedback. please check.</p>" +
                "<a href=\"http://localhost:5173/board/" + board.getId() + "\">Click here</a>" +
                """
                </body>
                </html>""";

        sendEmail(to, subject, htmlContent);
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

    public void sendPasswordResetMail(String otp, String mailId) {
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
    }

    public void sendVerificationMail(User user, String verificationTokenValue) {
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
}
