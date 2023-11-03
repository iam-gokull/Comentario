package com.app.comentarioserver.service;

import com.app.comentarioserver.entity.Feedback;
import com.app.comentarioserver.pojo.Comment;
import com.app.comentarioserver.repository.FeedbackRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private FeedbackService feedbackService;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Test
    void test_post_comment() {
        Comment comment = new Comment();
        comment.setCommentId("1");
        comment.setCommentTitle("Test comment");

        Feedback feedback = new Feedback();
        feedback.setId("1");
        feedback.setTitle("Test feedback");

        Mockito.when(feedbackRepository.findById(Mockito.anyString())).thenReturn(Optional.of(feedback));
        Mockito.when(feedbackRepository.save(Mockito.any(Feedback.class))).thenReturn(feedback);

        Feedback result = commentService.postComment("1", comment);

        assertThat(result.getComments()).contains(comment);
        assertThat(result.getTitle()).isEqualTo("Test feedback");

    }

    @Test
    void test_delete_comment() {
        Feedback feedback = new Feedback();
        feedback.setId("1");
        feedback.setTitle("Test feedback");

        Comment comment = new Comment();
        comment.setCommentId("1");
        comment.setCommentTitle("Test comment");
        feedback.setComments(comment);

        Mockito.when(feedbackService.getFeedbackFormId(Mockito.anyString())).thenReturn(feedback);
        Mockito.when(feedbackRepository.save(Mockito.any(Feedback.class))).thenReturn(feedback);

        boolean result = commentService.deleteComment(feedback.getId(), comment.getCommentId());
        assertThat(result).isTrue();
    }
}