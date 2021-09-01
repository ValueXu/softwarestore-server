package cn.jxufe.valuexu.softwarestoreserver.domain;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Table(name = "rules")
@DynamicUpdate
@DynamicInsert
public class Rules {
    private static final long serialVersionUID=1L;

    @Id
    @GeneratedValue
    private long id;
    @Column(name = "name")
    private String name;

    public Rules(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Rules() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
