package fa.training.fjb04.ims.entity.candidates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fa.training.fjb04.ims.entity.BaseEntity;
import fa.training.fjb04.ims.entity.interview.Interview;
import fa.training.fjb04.ims.entity.offer.Offer;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Recruiters extends BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruiter_id")
    private Integer id;

    @Column(name = "recruiter_user_name",unique = true,nullable = false)
    private String recruiterUserName;

    @Column(name = "recruiter_name")
    private String recruiterName;

    @OneToMany(mappedBy = "recruiter")
    @JsonIgnore
    private Set<Candidates> candidatesSet = new HashSet<>();

    @OneToMany(mappedBy = "recruiters")
    private List<Interview> interviewList = new ArrayList<>();

    @OneToMany(mappedBy = "recruiterOwner")
    private Set<Offer> offerSet = new HashSet<>();
}
