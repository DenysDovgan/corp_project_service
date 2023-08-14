package faang.school.projectservice.service;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.filter.project.ProjectFilter;
import faang.school.projectservice.mapper.ProjectMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.repository.ProjectRepository;
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

    public ProjectDto createProject(ProjectDto projectDto) {
        if (projectRepository.existsByOwnerUserIdAndName(projectDto.getOwnerId(), projectDto.getName())) {
            throw new DataValidationException("You can't create project with name existed");
        }
        projectDto.setStatus(ProjectStatus.CREATED);
        Project project = projectRepository.save(projectMapper.toProject(projectDto));
        return projectMapper.toProjectDto(project);
    }

    public ProjectDto updateProject(Long id, ProjectDto projectDto) {
        projectRepository.getProjectById(id);
        Project projectUpdate = projectRepository.save(projectMapper.toProject(projectDto));
        return projectMapper.toProjectDto(projectUpdate);
    }

    public List<ProjectDto> getProjectByFilter(ProjectFilterDto projectFilterDto) {
        Stream<Project> stream = projectRepository.findAll().stream();
        return filterProjects(projectFilterDto, stream);
    }

    public List<ProjectDto> getAllProjects() {
        return projectRepository.findAll()
                .stream()
                .map(projectMapper::toProjectDto)
                .toList();
    }

    public ProjectDto getProjectById(Long id) {
        return projectMapper.toProjectDto(projectRepository.getProjectById(id));
    }

    private List<ProjectDto> filterProjects(ProjectFilterDto projectFilterDto, Stream<Project> projects) {
        List<ProjectFilter> stream = projectFilters.stream()
                .filter(projectFilter -> projectFilter.isApplicable(projectFilterDto)).toList();
        for (ProjectFilter projectFilter : stream) {
            projects = projectFilter.applyFilter(projects, projectFilterDto);
        }
        return projects
                .map(projectMapper::toProjectDto)
                .toList();
    }
}
