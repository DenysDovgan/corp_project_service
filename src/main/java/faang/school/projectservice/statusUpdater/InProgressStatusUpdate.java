package faang.school.projectservice.statusUpdater;

import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InProgressStatusUpdate implements StatusUpdater {
    private final ProjectValidator projectValidator;
    private final ProjectRepository projectRepository;

    @Override
    public boolean isApplicable(UpdateSubProjectDto updateSubProjectDto) {
        return updateSubProjectDto.getStatus() == ProjectStatus.IN_PROGRESS;
    }

    @Override
    public void changeStatus(Project project) {
        project.setStatus(ProjectStatus.IN_PROGRESS);
        if (projectValidator.hasParentProject(project)) {
            Project parentProject = project.getParentProject();
            changeStatus(parentProject);
        }
        projectRepository.save(project);
    }
}
