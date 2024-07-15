package faang.school.projectservice.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;


public interface ProjectFilter {
    boolean isApplicable(ProjectFilterDto filters);

    Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filters);
}