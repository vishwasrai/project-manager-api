package com.fse.projectmanager.entity;

import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "ParentTask")
public class ParentTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Parent_ID")
    private Integer parentId;

    @Column(name = "Parent_Task")
    private String parentTask;

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public void setParentTask(String parentTask) {
        this.parentTask = parentTask;
    }
}