package cn.jxufe.valuexu.softwarestoreserver.domain;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "scores")
@DynamicUpdate
@DynamicInsert
public class Scores implements Serializable {
    @Id
    @GeneratedValue
    private long id;
    @Column(name = "username")
    private String username;
    @Column(name = "software_id")
    private long softwareId;
    @Column(name = "score")
    private long score;
    @Column(name = "comment")
    private String comment;

    public Scores(String username, long softwareId, long score, String comment) {
        this.id = id;
        this.username = username;
        this.softwareId = softwareId;
        this.score = score;
        this.comment = comment;
    }

    public Scores() {
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(long softwareId) {
        this.softwareId = softwareId;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
