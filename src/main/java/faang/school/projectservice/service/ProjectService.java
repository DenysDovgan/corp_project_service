package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.updater.ProjectUpdaterDto;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.filter.ProjectFilter;
import faang.school.projectservice.service.updater.ProjectUpdater;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;
    private final List<ProjectFilter> projectFilters;
    private final List<ProjectUpdater> projectUpdaters;

    public ProjectDto create(long ownerId, String name, String description) {
        if (projectRepository.existsByOwnerUserIdAndName(ownerId, name)) {
            throw new RuntimeException("Project " + name + " already created by " + ownerId);
        }
        Project project = Project.builder()
                .ownerId(ownerId)
                .name(name)
                .description(description)
                .status(ProjectStatus.CREATED)
                .build();
        return projectMapper.toDto(projectRepository.save(project));
    }

    public ProjectDto update(long id, ProjectUpdaterDto updater) {
        try {
            Project project = projectRepository.getProjectById(id);
            projectUpdaters.stream().filter(filter -> filter.isApplicable(updater))
                    .forEach(filter -> filter.apply(project, updater));
            return projectMapper.toDto(projectRepository.save(project));
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public List<ProjectDto> getProjectsWithFilters(long userId, ProjectFilterDto filters) {
        Stream<Project> publicProjects = projectRepository.findAll().stream()
                .filter(project -> project.getVisibility() == ProjectVisibility.PUBLIC);
        Stream<Project> privateProjects = projectRepository.findAll().stream()
                .filter(project -> project.getVisibility() == ProjectVisibility.PRIVATE)
                .filter(project -> project.getTeams().stream()
                        .anyMatch(team -> team.getTeamMembers().stream()
                                .anyMatch(member -> member.getId().equals(userId))));
        Stream<Project> resultStream = Stream.concat(publicProjects, privateProjects);
        return projectFilters.stream()
                .filter(filter -> filter.isApplicable(filters))
                .flatMap(filter -> filter.apply(resultStream, filters))
                .map(projectMapper::toDto)
                .toList();
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(projectMapper::toDto)
                .toList();
    }

    public ProjectDto getProjectById(long id) {
        try {
            return projectMapper.toDto(projectRepository.getProjectById(id));
        } catch (EntityNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
