package faang.school.projectservice.model;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

import faang.school.projectservice.model.stage.Stage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "project")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 128, nullable = false)
    private String name;

    @Column(name = "description", length = 4096)
    private String description;

    @Column(name = "storage_size")
    private BigInteger storageSize;

    @Column(name = "max_storage_size")
    private BigInteger maxStorageSize;

    @Column(name = "owner_id")
    private Long ownerId;

    @OneToMany(mappedBy = "project")
    private List<Task> tasks;

    @OneToMany(mappedBy = "project")
    private List<Resource> resources;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus status;

    @Column(name = "cover_image_id")
    private String coverImageId;

    @OneToMany(mappedBy = "project")
    private List<Team> teams;

    @OneToOne(mappedBy = "project")
    private Schedule schedule;

    @OneToMany(mappedBy = "project")
    private List<Stage> stages;

    @OneToMany(mappedBy = "project")
    private List<Vacancy> vacancies;
}
