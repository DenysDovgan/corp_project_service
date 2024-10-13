package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.dto.client.ProjectFilterDto;
import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface ProjectService {

    void createProject(ProjectDto projectDto);

    void updateStatus(ProjectDto projectDto, ProjectStatus status);

    void updateDescription(ProjectDto projectDto, String description);

    List<ProjectDto> getProjectsFilters(ProjectFilterDto filterDto, TeamMemberDto requester);

    List<ProjectDto> getProjects();

    boolean checkUserByPrivateProject(Project project, long requester);

    ProjectDto findById(long id);

    void addCoverImage(Long projectId, MultipartFile coverImage);

    void updateCoverImage(Long projectId, MultipartFile coverImage);

    InputStream getCoverImage(Long projectId);

    void deleteCoverImage(Long projectId);
  
    ProjectDto findById(long id, long userId);
}
