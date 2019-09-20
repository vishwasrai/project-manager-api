package com.fse.projectmanager.repository;

import com.fse.projectmanager.entity.ParentTask;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParentTaskRepository extends CrudRepository<ParentTask, Integer> {
    ParentTask findFirstByParentTask(String parentTask);
}
