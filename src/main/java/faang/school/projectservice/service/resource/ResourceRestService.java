package faang.school.projectservice.service.resource;

import com.amazonaws.services.kms.model.NotFoundException;
import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.service.s3.AmazonS3Service;
import faang.school.projectservice.validator.resource.ResourceValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.util.HashSet;

@Service
@RequiredArgsConstructor
public class ResourceRestService {
    private final AmazonS3Service amazonS3Service;
    private final ResourceRepository resourceRepository;
    private final ProjectRepository projectRepository;
    private final ResourceMapper resourceMapper;
    private final TeamMemberJpaRepository teamMemberRepository;
    private final ResourceValidator resourceValidator;

    @Transactional
    public ResourceDto saveFile(Long teamMemberId, MultipartFile file) {
        TeamMember teamMember = getTeamMember(teamMemberId);
        Project project = teamMember.getTeam().getProject();

        resourceValidator.validateMaxFreeStorageSize(project, file.getSize());
        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));

        String folder = project.getName() + project.getId();
        String key = String.format("%s/%d%s", folder, System.currentTimeMillis(), file.getOriginalFilename());
        Resource resource = amazonS3Service.uploadFile(key, file);

        resource.setKey(key);
        resource.setName(file.getOriginalFilename());
        resource.setSize(BigInteger.valueOf(file.getSize()));
        resource.setType(ResourceType.getResourceType(file.getContentType()));
        resource.setAllowedRoles(new HashSet<>(teamMember.getRoles()));
        resource.setStatus(ResourceStatus.ACTIVE);
        resource.setCreatedBy(teamMember);
        resource.setUpdatedBy(teamMember);
        resource.setProject(project);
        //TODO Murzin34* TeamRole это Enum, а в модели Resource фигурирует role_id bigint. Возможно нужно изменить таблицу.

        resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        project.getResources().add(resource);
        projectRepository.save(project);

        return resourceMapper.toDto(resource);
    }

    @Transactional(readOnly = true)
    public InputStreamResource getFile(Long teamMemberId, Long resourceId) {
        TeamMember teamMember = getTeamMember(teamMemberId);
        Resource resource = getResource(resourceId);

        resourceValidator.validateDownloadFilePermission(teamMember, resource);

        return amazonS3Service.downloadFile(resource.getKey());
    }

    @Transactional
    public void deleteFile(Long userid, Long resourceId) {
        TeamMember teamMember = getTeamMember(userid);
        Resource resource = getResource(resourceId);
        Project project = teamMember.getTeam().getProject();

        resourceValidator.validateDeleteFilePermission(teamMember, resource);

        amazonS3Service.deleteFile(resource.getKey());

        project.getResources().remove(resource);
        project.setStorageSize(project.getStorageSize().subtract(resource.getSize()));
        projectRepository.save(project);

        resource.setKey(null);
        resource.setSize(null);
        resource.setAllowedRoles(null);
        resource.setStatus(ResourceStatus.DELETED);
        resource.setUpdatedBy(teamMember);

        resourceRepository.save(resource);
    }

    private Resource getResource(Long resourceId) {
        return resourceRepository.findById(resourceId)
                .orElseThrow(() -> new NotFoundException("Not found resource with this id: " + resourceId));
    }

    private TeamMember getTeamMember(Long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId)
                .orElseThrow(() -> new NotFoundException("Not found teamMember with this userId: " + teamMemberId));
    }
}