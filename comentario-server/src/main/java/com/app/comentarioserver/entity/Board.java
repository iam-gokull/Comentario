package com.app.comentarioserver.entity;

import com.app.comentarioserver.dto.BoardDto;
import com.app.comentarioserver.pojo.ImageData;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.LinkedList;
import java.util.List;

@Document(collection = "boards")
@Getter
@Setter
public class Board {

    @Id
    private String id;

    @NotNull
    private ImageData imageData;

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private String url;

    private boolean isSelf;

    @NotNull
    private String username;

    @DBRef
    private List<Feedback> feedbacks;

    private int urlClickCount;

    public void setFeedbacks(Feedback feedback) {
        this.feedbacks.add(feedback);
    }

    public void setUrlClick() {
       this.urlClickCount = urlClickCount + 1;
    }

    public Board() {
        this.feedbacks = new LinkedList<>();
    }

    public Board(BoardDto boardDto) {
        this.title = boardDto.title();
        this.description = boardDto.description();
        this.url = boardDto.url();
        this.isSelf = boardDto.isSelf();
        this.username = boardDto.username();
        this.feedbacks = new LinkedList<>();
        this.urlClickCount = 0;
    }

}
