package com.app.comentarioserver.entity;

import com.app.comentarioserver.dto.FeedbackDto;
import com.app.comentarioserver.pojo.Comment;
import com.app.comentarioserver.types.Category;
import com.app.comentarioserver.types.Roadmap;
import com.app.comentarioserver.types.Sentiment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Document(collection = "feedbacks")
@Getter
@Setter
public class Feedback {

    @Id
    private String id;

    private String title;

    private Category category;

    private String description;

    private Set<String> upVoteUsernames;

    private List<Comment> comments;

    private String boardId;

    private String username;

    private String profileUrl;

    private Roadmap roadmap;

    private Sentiment sentiment;

    public void setRoadmap(Roadmap roadmap) {
        this.roadmap = roadmap;
    }

    public void setComments(Comment comment) {
        this.comments.add(comment);
    }

    public void addUpVote(String upVoteUsername) {
        this.upVoteUsernames.add(upVoteUsername);
    }

    public void removeUpVote(String upVoteUsername) {
        this.upVoteUsernames.remove(upVoteUsername);
    }

    public boolean hasUpVoted(String upVoteUsername) {
        return this.upVoteUsernames.contains(upVoteUsername);
    }

    public int getUpVoteCount() {
        return this.upVoteUsernames.size();
    }

    public Feedback() {
        this.upVoteUsernames = new HashSet<>();
        this.comments = new LinkedList<>();
    }

    public Feedback(FeedbackDto feedbackDto) {
        this.title = feedbackDto.title();
        this.category = feedbackDto.category();
        this.description = feedbackDto.description();
        this.upVoteUsernames = new HashSet<>();
        this.comments = new LinkedList<>();
        this.roadmap = Roadmap.NONE;
        this.boardId = feedbackDto.boardId();
        this.username = feedbackDto.username();
        this.profileUrl = feedbackDto.profileUrl();
    }


}
