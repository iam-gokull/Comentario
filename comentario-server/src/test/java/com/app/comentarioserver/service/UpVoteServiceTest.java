package com.app.comentarioserver.service;


import com.app.comentarioserver.entity.Feedback;
import com.app.comentarioserver.repository.FeedbackRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UpVoteServiceTest {

    @InjectMocks
    private UpVoteService upVoteService;

    @Mock
    private UserService userService;

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Test
    void test_check_up_vote_for_non_up_voted_user() {
        Feedback feedback = new Feedback();

        String title = "Test feedback";
        String identifier = "User1";
        String id = "1";

        feedback.setId(id);
        feedback.setUsername(identifier);
        feedback.setTitle(title);

        Mockito.when(feedbackService.getFeedbackFormId(id)).thenReturn(feedback);
        Mockito.when(userService.getUsernameFromMailId(identifier)).thenReturn(identifier);

        Map<Boolean, Integer> result = upVoteService.checkUpVote(identifier, id);

        assertThat(result.containsKey(true)).isFalse();
        assertThat(result.getOrDefault(true, 0).intValue()).isZero();
    }

    @Test
    void test_check_up_vote_for_up_voted_user() {
        Feedback feedback = new Feedback();

        String title = "Test feedback";
        String identifier = "User1";
        String id = "1";

        feedback.setId(id);
        feedback.setUsername(identifier);
        feedback.setTitle(title);
        feedback.addUpVote(identifier);

        Mockito.when(feedbackService.getFeedbackFormId(id)).thenReturn(feedback);
        Mockito.when(userService.getUsernameFromMailId(identifier)).thenReturn(identifier);

        Map<Boolean, Integer> result = upVoteService.checkUpVote(identifier, id);

        assertThat(result).containsKey(true);
        assertThat(result.get(true).intValue()).isEqualTo(1);
    }

    @Test
    void test_add_up_vote() {
        Feedback feedback = new Feedback();

        String title = "Test feedback";
        String identifier = "User1";
        String id = "1";

        feedback.setId(id);
        feedback.setUsername(identifier);
        feedback.setTitle(title);

        Mockito.when(feedbackService.getFeedbackFormId(id)).thenReturn(feedback);
        Mockito.when(userService.getUsernameFromMailId(identifier)).thenReturn(identifier);
        Mockito.when(feedbackRepository.save(feedback)).thenReturn(feedback);

        Feedback result = upVoteService.upVote(identifier, id);

        assertThat(result.hasUpVoted(identifier)).isTrue();

    }

    @Test
    void test_remove_up_vote() {
        Feedback feedback = new Feedback();

        String title = "Test feedback";
        String identifier = "User1";
        String id = "1";

        feedback.setId(id);
        feedback.setUsername(identifier);
        feedback.setTitle(title);

        Mockito.when(feedbackService.getFeedbackFormId(id)).thenReturn(feedback);
        Mockito.when(userService.getUsernameFromMailId(identifier)).thenReturn(identifier);
        Mockito.when(feedbackRepository.save(feedback)).thenReturn(feedback);

        Feedback result = upVoteService.removeUpVote(identifier, id);

        assertThat(result.hasUpVoted(identifier)).isFalse();

    }

}