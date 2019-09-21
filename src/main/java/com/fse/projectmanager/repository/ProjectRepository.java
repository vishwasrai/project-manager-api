package com.fse.projectmanager.repository;

import com.fse.projectmanager.entity.Project;
import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<Project, Integer> {
}
