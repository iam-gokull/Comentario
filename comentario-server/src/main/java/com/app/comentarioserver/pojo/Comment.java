package com.app.comentarioserver.pojo;

import com.app.comentarioserver.types.Sentiment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class Comment {
    private String commentId;
    private String profileUrl;
    private String username;
    private String commentTitle;
    private Sentiment sentiment;

    public Comment(String profileUrl, String username, String commentTitle) {
        this.commentId = UUID.randomUUID().toString();
        this.profileUrl = profileUrl;
        this.username = username;
        this.commentTitle = commentTitle;
    }

}
