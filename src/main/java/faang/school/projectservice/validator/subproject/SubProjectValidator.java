package faang.school.projectservice.validator.subproject;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.service.project.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubProjectValidator {
    private final ProjectService projectService;

    public void validateVisibility(ProjectVisibility visibility, ProjectVisibility parentVisibility) {
        if (visibility == ProjectVisibility.PUBLIC && parentVisibility == ProjectVisibility.PRIVATE) {
            throw new DataValidationException("You can't make public project in private project");
        }
    }
    public void validateSubProjectStatus(long projectId) {
        Project project = projectService.getProjectById(projectId);
        ProjectStatus status = project.getStatus();

        if (status == ProjectStatus.COMPLETED && project.getChildren() != null) {
            if (!checkStatusChildren(project.getChildren())) {
                throw new DataValidationException("You can make the project completed only after finishing all subprojects");
            }
        }
    }

    private boolean checkStatusChildren(List<Project> projects) {
        for (Project project : projects) {
            if (project.getStatus() == ProjectStatus.COMPLETED ||
                    project.getStatus() == ProjectStatus.CANCELLED) {
                continue;
            }
            return false;
        }
        return true;
    }
}
