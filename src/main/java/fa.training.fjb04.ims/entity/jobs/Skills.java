package fa.training.fjb04.ims.entity.jobs;

import fa.training.fjb04.ims.entity.BaseEntity;
import fa.training.fjb04.ims.entity.candidates.Candidates;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Skills extends BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "skill_name")
    private String skillName;

    @ManyToMany(mappedBy = "skillsSet")
    private Set<Jobs> jobsSet=new HashSet<>();

    @ManyToMany(mappedBy = "skillsSet")
    private Set<Candidates> candidatesSet = new HashSet<>();
}
