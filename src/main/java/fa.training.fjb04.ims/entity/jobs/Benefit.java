package fa.training.fjb04.ims.entity.jobs;

import fa.training.fjb04.ims.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Benefit extends BaseEntity<Integer> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="benefit_name",nullable = false)
    private String benefitName;

    public Benefit(String benefitName) {
        this.benefitName = benefitName;
    }

    @ManyToMany(mappedBy = "benefitSet")
    private Set<Jobs> jobsSet=new HashSet<>();
}
