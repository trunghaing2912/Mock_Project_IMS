package fa.training.fjb04.ims.entity.candidates;

import fa.training.fjb04.ims.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HighestLevel extends BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "highest_level_id")
    private Integer id;

    @Column(name = "highest_level_name")
    private String highestLevelName;

    @OneToMany(mappedBy = "highestLevel")
    private Set<Candidates> candidatesSet = new HashSet<>();
}
