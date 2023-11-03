package com.app.comentarioserver.service;

import com.app.comentarioserver.entity.Board;
import com.app.comentarioserver.entity.Feedback;
import com.app.comentarioserver.exception.BoardNotFoundException;
import com.app.comentarioserver.exception.FeedbackNotFoundException;
import com.app.comentarioserver.exception.UnableToAddFeedbackException;
import com.app.comentarioserver.repository.FeedbackRepository;
import com.app.comentarioserver.sentiment_analysis.AnalyzeSentiments;
import com.app.comentarioserver.types.Roadmap;
import com.app.comentarioserver.types.Sentiment;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class FeedbackServiceTest {

    @InjectMocks
    private FeedbackService feedbackService;

    @Mock
    private FeedbackRepository feedbackRepository;


    @Mock
    private EmailService emailService;

    @Mock
    private BoardService boardService;

    @Mock
    private AnalyzeSentiments sentiments;


    @Test
    void test_get_feedback_from_id() {
        Feedback feedback = new Feedback();
        feedback.setId("1");
        feedback.setTitle("Feedback 1");
        feedback.setUsername("User 1");

        String id = "1";

        Mockito.when(feedbackRepository.findById(id)).thenReturn(Optional.of(feedback));

        Feedback result = feedbackService.getFeedbackFormId(id);

        assertThat(result.getTitle()).isEqualTo("Feedback 1");

    }

    @Test
    void test_get_feedback_from_id_not_found() {
        Feedback feedback = new Feedback();
        feedback.setId("1");
        feedback.setTitle("Feedback 1");

        String id = "1";

        Mockito.when(feedbackRepository.findById(id)).thenReturn(Optional.empty());
        Exception exception = assertThrows(FeedbackNotFoundException.class, () -> feedbackService.getFeedbackFormId(id));
        assertThat(exception.getMessage()).isEqualTo("Unable to find feedback with id: " + id);

    }

    @Test
    void test_add_feedback() {
        Feedback feedback = new Feedback();
        feedback.setId("1");
        feedback.setTitle("Test feedback");
        feedback.setBoardId("1");
        feedback.setUsername("User");

        Board board = new Board();
        board.setId("id");
        board.setTitle("Title");
        board.setFeedbacks(feedback);

        Mockito.when(feedbackRepository.save(Mockito.any(Feedback.class))).thenReturn(feedback);
        Mockito.when(boardService.addFeedbackToTheBoard(Mockito.anyString(), Mockito.any(Feedback.class))).thenReturn(true);
        Mockito.doNothing().when(emailService).sendFeedbackMail(Mockito.any(Feedback.class));
        Feedback result = feedbackService.addFeedback(feedback);

        assertThat(result.getTitle()).isEqualTo("Test feedback");
    }

    @Test
    void test_add_feedback_to_roadmap() {
        Feedback feedback = new Feedback();
        feedback.setId("1");
        feedback.setTitle("Test feedback");

        Mockito.when(feedbackRepository.findById(Mockito.anyString())).thenReturn(Optional.of(feedback));
        Mockito.doNothing().when(emailService).sendRoadmapUpdatedMail(Mockito.any(Feedback.class));
        Mockito.when(feedbackRepository.save(Mockito.any(Feedback.class))).thenReturn(feedback);

        Feedback result = feedbackService.addFeedbackToRoadmap("1", Roadmap.INPROGRESS);

        assertThat(result.getRoadmap()).isEqualTo(Roadmap.INPROGRESS);
    }


    @Test
    void test_calculate_sentiment() {
        Mockito.when(sentiments.getSentimentScore(Mockito.anyString())).thenReturn(3);

        Sentiment result = feedbackService.calculateSentiment("title", "description");

        assertThat(result).isEqualTo(Sentiment.VERY_POSITIVE);
    }
}