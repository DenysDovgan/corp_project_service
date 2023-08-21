package faang.school.projectservice.service;

import faang.school.projectservice.dto.ResourceDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.util.FileStore;
import faang.school.projectservice.validator.ResourcesValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ResourceRepository resourceRepository;
    private final ResourcesValidator resourcesValidator;
    private final TeamMemberService teamMemberService;
    private final ProjectService projectService;
    private final ResourceMapper resourceMapper;
    private final FileStore fileStore;

    public ResourceDto uploadFile(ResourceDto resourceDto, MultipartFile file, long userId) {
        Project project = projectService.getProjectEntityById(resourceDto.getProjectId());
        String key = resourceDto.getProjectId() + "_" + project.getName() + "/" + file.getOriginalFilename();

        Resource resource = fillResourceCreate(resourceDto, file, key, userId);
        updateProjectStorageCapacity(file, project);

        fileStore.uploadFile(file, key);

        Resource entity = resourceRepository.save(resource);
        return resourceMapper.toDto(entity);
    }

    public ResourceDto updateFile(long id, ResourceDto resourceDto, MultipartFile file, long userId) {
        Resource resource = getResourceById(id);
        Project project = projectService.getProjectEntityById(resourceDto.getProjectId());
        updateProjectStorageCapacity(file, project);

        String key = resourceDto.getProjectId() + "_" + project.getName() + "/" + file.getOriginalFilename();

        fileStore.deleteFile(resource.getKey());
        fillResourceUpdate(resourceDto, resource, file, key, userId);
        fileStore.uploadFile(file, key);

        return resourceMapper.toDto(resourceRepository.save(resource));
    }

    public void deleteResource(long id, long userId) {
        Resource resource = getResourceById(id);
        Project project = projectService.getProjectEntityById(id);

        resourcesValidator.checkRightsToDelete(resource, project, userId);

        fileStore.deleteFile(resource.getKey());

        subtractCapacity(project, resource);

        resource.setKey(null);
        resource.setSize(null);
        resource.setStatus(ResourceStatus.DELETED);
        resourceRepository.save(resource);
    }

    private Resource getResourceById(long id) {
        return resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
    }

    private Resource fillResourceCreate(ResourceDto resourceDto, MultipartFile file, String key, long userId) {
        TeamMember teamMember = teamMemberService.getTeamMemberById(userId);

        Resource resource = resourceMapper.toEntity(resourceDto);

        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);

        fillResource(resource, file, key, teamMember);

        return resource;
    }

    private void fillResourceUpdate(ResourceDto resourceDto, Resource resource, MultipartFile file, String key, long userId) {
        TeamMember teamMember = teamMemberService.getTeamMemberById(userId);

        resourceMapper.update(resourceDto, resource);

        List<TeamRole> roles = new ArrayList<>(teamMember.getRoles());

        resource.setAllowedRoles(roles);
        resource.setUpdatedBy(teamMember);
        resource.setUpdatedAt(null);

        fillResource(resource, file, key, teamMember);
    }

    private void fillResource(Resource resource, MultipartFile file, String key, TeamMember teamMember) {
        List<TeamRole> roles = new ArrayList<>(teamMember.getRoles());

        resource.setAllowedRoles(roles);
        resource.setName(file.getOriginalFilename());
        resource.setKey(key);
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setSize(BigInteger.valueOf(file.getSize()));
    }

    private void updateProjectStorageCapacity(MultipartFile file, Project project) {
        long newStorageCapacity = project.getStorageSize().intValue() + file.getSize();

        resourcesValidator.checkStorageCapacity(newStorageCapacity);

        project.setStorageSize(project.getStorageSize().add(BigInteger.valueOf(file.getSize())));
        projectService.saveProject(project);
    }

    private void subtractCapacity(Project project, Resource resource) {
        BigInteger newSizeCapacity = project.getStorageSize().subtract(resource.getSize());

        if (newSizeCapacity.compareTo(BigInteger.ZERO) < 0) {
            newSizeCapacity = BigInteger.ZERO;
        }

        project.setStorageSize(newSizeCapacity);
        projectService.saveProject(project);
    }
}
