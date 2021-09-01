package cn.jxufe.valuexu.softwarestoreserver.domain;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Table(name = "record")
@DynamicUpdate
@DynamicInsert
public class Record {
    @Id
    @GeneratedValue
    private long id;
    @Column(name = "software_id")
    private long softwareId;
    @Column(name = "username")
    private String userName;
    @CreatedDate
    @Column(name = "time")
    private Timestamp time;

    public Record(long softwareId, String userName,Timestamp date) {
        this.softwareId = softwareId;
        this.userName = userName;
        this.time = date;
    }

    public Record() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(long softwareId) {
        this.softwareId = softwareId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTime() {
        return time.getTime();
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }
}
