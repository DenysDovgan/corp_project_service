package faang.school.projectservice.controller.project;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.controller.ApiPath;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.service.project.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.PROJECTS_PATH)
public class ProjectController {

    private final ProjectService projectService;
    private final UserContext userContext;
    
    @PostMapping
    public ResponseEntity<ProjectDto> createProject(@RequestBody @Valid ProjectDto projectDto) {
        projectDto.setOwnerId(userContext.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(projectService.createProject(projectDto));
    }

    @PutMapping("/{id}")
    public ProjectDto updateProject(@PathVariable("id") long id, @RequestBody @Valid ProjectUpdateDto projectUpdateDto) {
        return projectService.updateProject(id, projectUpdateDto);
    }

    @GetMapping("/{id}")
    public ProjectDto getProject(@PathVariable("id") long id) {
        return projectService.retrieveProject(id);
    }

}
