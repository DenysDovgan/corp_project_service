package faang.school.projectservice.service;

import faang.school.projectservice.model.project.Project;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ProjectService {
    private final ProjectRepository projectRepository;

    public Project findProjectById(long projectId) {
        return projectRepository.getProjectById(projectId);
    }
}
