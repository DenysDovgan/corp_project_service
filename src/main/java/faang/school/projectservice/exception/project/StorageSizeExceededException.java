package faang.school.projectservice.exception.project;

public class StorageSizeExceededException extends RuntimeException {
    public StorageSizeExceededException(String message) {
        super(message);
    }
}
