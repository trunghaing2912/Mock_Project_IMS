package fa.training.fjb04.ims.entity.jobs;

import fa.training.fjb04.ims.entity.BaseEntity;
import fa.training.fjb04.ims.entity.jobs.Jobs;
import fa.training.fjb04.ims.entity.offer.Offer;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class Levels extends BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="level_name")
    private String levelName;


    @ManyToMany(mappedBy = "levelsSet")
    private Set<Jobs> jobsSet=new HashSet<>();

    @OneToMany(mappedBy = "level")
    private Set<Offer> offersSet =new HashSet<>();
}
