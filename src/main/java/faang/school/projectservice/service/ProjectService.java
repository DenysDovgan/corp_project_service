package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    public ProjectDto createProject(ProjectDto projectDto) {
        if(projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("You can't create project with the same name");
        }
        projectDto.setCreatedAt(LocalDateTime.now());
        projectDto.setStatus(ProjectStatus.CREATED);
        Project project = projectRepository.save(projectMapper.toProject(projectDto));
        return projectMapper.toProjectDto(project);
    }

    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        Project projectToUpdate = projectRepository.getProjectById(id);
        ProjectDto projectDtoToUpdate = projectMapper.toProjectDto(projectToUpdate);
        updateProject(projectDtoToUpdate, projectDto);
        return projectDtoToUpdate;
    }

    private void updateProject(ProjectDto projectDtoToUpdate, ProjectDto projectDto) {
        if(!(projectDto.getDescription() == null)) {
            projectDtoToUpdate.setDescription(projectDto.getDescription());
        }
        if(!(projectDto.getStatus() == null)) {
            projectDtoToUpdate.setStatus(projectDto.getStatus());
        }
        projectRepository.save(projectMapper.toProject(projectDtoToUpdate));
    }
}
