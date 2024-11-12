package faang.school.projectservice.service;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.validator.ProjectValidator;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.mapper.project.ProjectMapper;
import faang.school.projectservice.mapper.project.UpdateProjectMapper;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectResponseDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.dto.project.UpdateSubProjectDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.statusUpdater.StatusUpdater;
import faang.school.projectservice.validator.ProjectValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectValidator projectValidator;
    private final ProjectMapper projectMapper;
    private final UpdateProjectMapper updateProjectMapper;
    private final List<Filter<Project, ProjectFilterDto>> projectFilters;
    private final List<StatusUpdater> statusUpdates;

    public ProjectDto createProject(ProjectDto dto) {
        projectValidator.validateUniqueProject(dto);

        Project project = projectMapper.toEntity(dto);
        project.setStatus(ProjectStatus.CREATED);
        Project savedProject = projectRepository.save(project);

        log.info("Project #{} successfully created.", savedProject.getId());

        return projectMapper.toDto(savedProject);
    }

    @Transactional
    public UpdateProjectDto updateProject(UpdateProjectDto dto) {
        Project project = projectRepository.getProjectById(dto.getId());

        if (dto.getDescription() != null && !dto.getDescription().isBlank()) {
            project.setDescription(dto.getDescription());
        }
        if (dto.getStatus() != null) {
            project.setStatus(dto.getStatus());
        }
        if (dto.getVisibility() != null) {
            project.setVisibility(dto.getVisibility());
        }

        project.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));
        Project updatedProject = projectRepository.save(project);

        log.info("Project #{} successfully updated. Current data: description: '{}'; status: '{}'; visibility: '{}'",
                updatedProject.getId(), updatedProject.getDescription(),
                updatedProject.getStatus(), updatedProject.getVisibility());

        return updateProjectMapper.toDto(updatedProject);
    }

    public List<ProjectDto> getProjectsByFilter(ProjectFilterDto filterDto, Long currentUserId) {
        List<Project> projects = getAllAccessibleProjects(currentUserId);

        List<ProjectDto> result = projectFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(projects.stream(),
                        (projectsStream, filter) -> filter.apply(projectsStream, filterDto),
                        (s1, s2) -> s1)
                .map(projectMapper::toDto)
                .toList();

        log.info("Projects filtered by {}.", filterDto);

        return result;
    }

    public List<ProjectDto> getAllProjectsForUser(Long currentUserId) {
        List<ProjectDto> result = getAllAccessibleProjects(currentUserId).stream()
                .map(projectMapper::toDto)
                .toList();

        log.info("Founded all projects, available for User #{}", currentUserId);

        return result;
    }

    public ProjectDto getAccessibleProjectsById(Long currentUserId, Long projectId) {
        Project project = projectRepository.getProjectById(projectId);

        if (!projectValidator.canUserAccessProject(project, currentUserId)) {
            log.error("Project #{} not found by User #{}", projectId, currentUserId);
            throw new EntityNotFoundException(String.format("Project #%d not found by User #%d",
                    projectId, currentUserId));
        }

        return projectMapper.toDto(project);
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }

    public List<Project> findAllById(List<Long> ids) {
        return projectRepository.findAllByIds(ids);
    }

    private List<Project> getAllAccessibleProjects(Long currentUserId) {
        return projectRepository.findAll().stream()
                .filter(project -> projectValidator.canUserAccessProject(project, currentUserId))
                .toList();
    }

    public ProjectResponseDto createSubProject(Long parentId, CreateProjectDto createProjectDto) {

        return null;
    }

    @Transactional
    public ProjectResponseDto updateSubProject(UpdateSubProjectDto updateSubProjectDto) {
        projectValidator.validateProjectExistsById(updateSubProjectDto.getId());
        Project project = getProjectById(updateSubProjectDto.getId());
        changeStatus(project, updateSubProjectDto);
        changeVisibility(project, updateSubProjectDto);
        // add Moment
        project.setUpdatedAt(LocalDateTime.now(ZoneId.of("UTC")));

        return projectMapper.toResponseDto(project);
    }

    public List<ProjectResponseDto> filterSubProjects(Long parentId, ProjectFilterDto filters) {

        Project project = projectRepository.getProjectById(parentId);
        projectValidator.validateProjectPublic(project);

        Stream<Project> childrenProjectsStream = project
                .getChildren()
                .stream()
                .filter(projectValidator::isPublicProject);

        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .reduce(childrenProjectsStream, (streamProject, filter) -> filter.apply(streamProject, filters),
                        (s1, s2) -> s1)
                .map(projectMapper::toResponseDto)
                .toList();
    }

    private void changeStatus(Project project, UpdateSubProjectDto updateSubProjectDto) {
        projectValidator.validateSameProjectStatus(project, updateSubProjectDto);
        projectValidator.validateProjectStatusCompletedOrCancelled(project);

        statusUpdates.stream()
                .filter(update -> update.isApplicable(updateSubProjectDto))
                .forEach(update -> update.changeStatus(project));
    }

    private void changeVisibility(Project project, UpdateSubProjectDto updateSubProjectDto) {
        if (updateSubProjectDto.getVisibility() != null) {
            project.setVisibility(updateSubProjectDto.getVisibility());
        }
    }
}

