package faang.school.projectservice.service.file;

import faang.school.projectservice.dto.resource.ResourceDto;
import org.apache.commons.imaging.ImageReadException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceService {
    ResourceDto addResource(Long projectId, MultipartFile file)  throws IOException, ImageReadException;
    ResourceDto updateResource(Long resourceId, Long userDtoId, MultipartFile file) throws IOException, ImageReadException;
    void deleteResource(Long resourceId, Long userId);
}
