package com.app.comentarioserver.entity;

import com.app.comentarioserver.configuration.GrantedAuthorityDeserializer;
import com.app.comentarioserver.pojo.ImageData;
import com.app.comentarioserver.pojo.Token;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Document(collection = "users")
@Getter
@Setter
public class User implements UserDetails {

    @Id
    private String id;

    @NotEmpty
    @NotNull
    private String fullName;

    @NotEmpty
    @NotNull
    private String username;

    @NotEmpty
    @NotNull
    @Email
    private String mailId;

    @NotEmpty
    @NotNull
    @JsonIgnore
    private String password;

    @NotNull
    private transient Token verificationToken;

    @NotNull
    private ImageData imageData;

    private boolean isVerified;

    @DBRef
    private transient List<Board> boards;

    public void setBoards(Board board) {
        this.boards.add(board);
    }

    @JsonDeserialize(using = GrantedAuthorityDeserializer.class)
    private Collection<? extends GrantedAuthority> roles;

    public User() {
        this.boards = new LinkedList<>();
    }

    public User(String fullName, String username, String mailId, String password, ImageData imageData) {
        this.fullName = fullName;
        this.username = username;
        this.mailId = mailId;
        this.password = password;
        this.roles = List.of(new SimpleGrantedAuthority("USER"));
        this.isVerified = false;
        this.imageData = imageData;
        this.boards = new LinkedList<>();
    }

    public User(String mailId, String password, Collection<? extends GrantedAuthority> roles) {
        this.mailId = mailId;
        this.password = password;
        this.roles = roles;
        this.boards = new LinkedList<>();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        assert  this.roles != null;
        return this.roles.stream().toList();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }


   @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", mailId='" + mailId + '\'' +
                ", password='" + password + '\'' +
                ", verificationToken=" + verificationToken +
                ", profileImageUrl='" + imageData.getImageUrl() + '\'' +
                ", isVerified=" + isVerified +
                ", boards=" + boards +
                ", roles=" + roles +
                '}';
    }
    @Override
    public boolean isEnabled() {
        return this.isVerified;
    }
}
