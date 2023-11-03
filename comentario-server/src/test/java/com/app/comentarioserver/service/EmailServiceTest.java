package com.app.comentarioserver.service;

import com.app.comentarioserver.entity.Board;
import com.app.comentarioserver.entity.Feedback;
import com.app.comentarioserver.entity.User;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class EmailServiceTest {

    @InjectMocks
    private EmailService emailService;

    @Mock
    private UserService userService;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private BoardService boardService;

    @Test
    void test_send_roadmap_updated_mail() {
        User user = new User();
        user.setId("id");
        user.setUsername("iamgokul");
        user.setFullName("Gokul L");

        Board board = new Board();
        board.setId("id");
        board.setTitle("Test board 1");


        Mockito.when(userService.getUserByUsername(Mockito.anyString()));
        Mockito.when(boardService.getBoardById(Mockito.anyString())).thenReturn(board);


    }

    @Test
    void sendFeedbackMail() {
    }

    @Test
    void sendVerificationMail() {
    }

    @Test
    void sendPasswordResetMail() {
    }

    @Test
    void testSendVerificationMail() {
    }

    @Test
    void sendEmail() {
    }
}