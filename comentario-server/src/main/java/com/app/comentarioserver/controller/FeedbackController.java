package com.app.comentarioserver.controller;

import com.app.comentarioserver.dto.FeedbackDto;
import com.app.comentarioserver.pojo.Comment;
import com.app.comentarioserver.entity.Feedback;
import com.app.comentarioserver.service.CommentService;
import com.app.comentarioserver.types.Roadmap;
import com.app.comentarioserver.service.FeedbackService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Slf4j
@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "Authorization")
@RequiredArgsConstructor
@RequestMapping(path = "/feedbacks", produces = "application/json")
public class FeedbackController {

    private final FeedbackService feedbackService;

    private final CommentService commentService;

    @GetMapping("/feedback/{feedbackId}")
    public ResponseEntity<Feedback> getFeedback(@PathVariable String feedbackId) {
        return new ResponseEntity<>(feedbackService.getFeedbackFormId(feedbackId), HttpStatus.OK);
    }

    @PostMapping(value = "/add", consumes = "application/json")
    public ResponseEntity<Feedback> addFeedback(@RequestBody FeedbackDto feedbackDto) {

        Feedback feedback = new Feedback(feedbackDto);
        return new ResponseEntity<>(feedbackService.addFeedback(feedback), HttpStatus.OK);
    }

    @PutMapping(value = "/comment/post")
    public  ResponseEntity<Feedback> postComment(@RequestParam("id") String id, @RequestBody Comment comment) {
        return new ResponseEntity<>(commentService.postComment(id, comment), HttpStatus.OK);
    }

    @PutMapping(value = "/feedback/add-plan")
    public  ResponseEntity<Feedback> addRoadmap(@RequestParam("id") String id, @RequestBody String value) {
        Roadmap roadmap = Roadmap.valueOf(value.toUpperCase().replace("\"", ""));
        return new ResponseEntity<>(feedbackService.addFeedbackToRoadmap(id, roadmap), HttpStatus.OK);
    }

    @DeleteMapping("/{feedbackId}/delete-comment/{commentId}")
    public boolean deleteComment(@PathVariable String feedbackId, @PathVariable String commentId) {
        return commentService.deleteComment(feedbackId, commentId);
    }
}
