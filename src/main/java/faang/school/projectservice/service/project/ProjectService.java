package faang.school.projectservice.service.project;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.project.filter.ProjectFilter;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private static final String PROJECT_FROM_USER_EXISTS =
            "The user (with id %d) has already created a project (with id %d) with this name";
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;

    @Transactional
    public List<ProjectDto> getAllProjects() {
        return projectMapper.toDtoList(projectRepository.findAll().toList());
    }

    @Transactional
    public ProjectDto getProject(long projectId) {
        validateProjectExists(projectId);
        return projectMapper.toDto(projectRepository.getProjectById(projectId));
    }

    @Transactional
    public ProjectDto createProject(ProjectDto projectDto) {
        validateOfExistingProjectFromUser(projectDto);
        Project project = projectMapper.toEntity(projectDto);

        project.setStatus(ProjectStatus.CREATED);
        return saveEntity(project);
    }

    @Transactional
    public ProjectDto updateProject(ProjectDto projectDto, long projectId) {
        return projectRepository.findById(projectId)
                .map(project -> {
                    projectMapper.updateFromDto(projectDto, project);
                    return saveEntity(project);
                })
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Project with id %d does not exist.", projectDto.getId())));
    }

    @Transactional
    public List<ProjectDto> getProjects(ProjectFilterDto filters) {
        return filterProjects(projectRepository.findAll(), filters);
    }

    public ProjectDto createSubProject(ProjectDto projectDto) {
        validateParentProjectExist(projectDto);
        validateVisibilityConsistency(projectDto);
        validateSubProjectUnique(projectDto);

        Project subProject = projectMapper.toEntity(projectDto);
        Project parentProject = projectRepository.getProjectById(projectDto.getParentId());
        subProject.setParentProject(parentProject);
        subProject.setStatus(ProjectStatus.CREATED);
        Project savedSubProject = projectRepository.save(subProject);
        parentProject.getChildren().add(savedSubProject);

        return projectMapper.toDto(savedSubProject);
    }

    private ProjectDto saveEntity(Project project) {
        project = projectRepository.save(project);
        return projectMapper.toDto(project);
    }

    private List<ProjectDto> filterProjects(Stream<Project> projects, ProjectFilterDto filters) {
        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(projects, filters))
                .map(projectMapper::toDto)
                .toList();
    }

    private void validateProjectExists(long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new DataValidationException(String.format("Project with id %s does not exist", projectId));
        }
    }

    private void validateOfExistingProjectFromUser(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getId(), projectDto.getName())) {
            throw new DataValidationException(String
                    .format(PROJECT_FROM_USER_EXISTS, projectDto.getOwnerId(), projectDto.getId()));
        }
    }

    private void validateParentProjectExist(ProjectDto projectDto) {
        if (!projectRepository.existsById(projectDto.getParentId())) {
            throw new DataValidationException("No such parent project");
        }
    }

    private void validateVisibilityConsistency(ProjectDto projectDto) {
        Project parentProject = projectRepository.getProjectById(projectDto.getParentId());

        if (!projectDto.getVisibility().equals(parentProject.getVisibility())) {
            throw new DataValidationException("The visibility of the subproject must be - " +
                    parentProject.getVisibility() + " like the parent project");
        }
    }

    private void validateSubProjectUnique(ProjectDto projectDto) {
        Project parentProject = projectRepository.getProjectById(projectDto.getParentId());
        String subProjectName = projectDto.getName();
        boolean subProjectExists = parentProject.getChildren().stream().anyMatch(
                subProject -> subProject.getName().equals(subProjectName));

        if (subProjectExists) {
            throw new DataValidationException("Subproject with name " + subProjectName + " already exists");
        }
    }
}
