package com.fse.projectmanager.repository;

import com.fse.projectmanager.entity.Task;
import com.fse.projectmanager.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByTask(Task task);

    List<User> findAllByTaskIsNull();
}
