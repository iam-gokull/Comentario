package com.app.comentarioserver.service;

import com.app.comentarioserver.entity.Feedback;
import com.app.comentarioserver.pojo.Comment;
import com.app.comentarioserver.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final FeedbackService feedbackService;

    private final FeedbackRepository feedbackRepository;

    public Feedback postComment(String id, Comment comment) {
        comment.setSentiment(feedbackService.calculateSentiment(comment.getCommentTitle(), null));
        Feedback feedback = feedbackRepository.findById(id).orElseThrow();
        feedback.setComments(comment);
        return feedbackRepository.save(feedback);
    }

    public boolean deleteComment(String feedbackId, String commentId) {
        Feedback feedback = feedbackService.getFeedbackFormId(feedbackId);
        boolean status = feedback.getComments().removeIf(comment -> comment.getCommentId().equals(commentId));
        feedbackRepository.save(feedback);
        return status;
    }
}
