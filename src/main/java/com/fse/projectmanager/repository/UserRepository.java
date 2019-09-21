package com.fse.projectmanager.repository;

import com.fse.projectmanager.entity.Project;
import com.fse.projectmanager.entity.Task;
import com.fse.projectmanager.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByProject(Project project);

    User findByTask(Task task);

    List<User> findAllByProjectIsNull();

    List<User> findAllByTaskIsNull();
}
