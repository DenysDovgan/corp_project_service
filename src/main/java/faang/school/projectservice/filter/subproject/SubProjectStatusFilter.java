package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.Objects;
import java.util.stream.Stream;

public class SubProjectStatusFilter implements SubProjectFilter {

    @Override
    public boolean isApplicable(SubProjectFilterDto filters) {
        return filters != null && filters.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, SubProjectFilterDto filters) {
        Objects.requireNonNull(projects, "Projects stream cannot be null");
        Objects.requireNonNull(filters, "Filters cannot be null");

        return projects.filter(project -> filters.getStatus().equals(project.getStatus()));
    }
}
