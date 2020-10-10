package com.phil.movieland.data.entity;

import org.springframework.context.annotation.Primary;

import javax.persistence.*;

@MappedSuperclass
public class EntityWithId {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    public EntityWithId() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id=id;
    }
}
