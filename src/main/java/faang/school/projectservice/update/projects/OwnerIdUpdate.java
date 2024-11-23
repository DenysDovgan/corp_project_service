package faang.school.projectservice.update.projects;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.update.ProjectUpdate;
import org.springframework.stereotype.Component;

@Component
public class OwnerIdUpdate implements ProjectUpdate {
    @Override
    public boolean isApplicable(ProjectDto projectDto) {
        return projectDto.getOwnerId() != null;
    }

    @Override
    public void apply(Project project, ProjectDto projectDto) {
        project.setOwnerId(projectDto.getOwnerId());
    }
}