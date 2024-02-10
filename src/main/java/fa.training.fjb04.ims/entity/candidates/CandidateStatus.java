package fa.training.fjb04.ims.entity.candidates;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
public class CandidateStatus extends BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "status_id")
    private Integer id;

    @Column(name = "status_name")
    private String statusName;

    public CandidateStatus(Integer id, String statusName) {
        this.id = id;
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return "CandidateStatus{" +
                "statusName='" + statusName + '\'' +
                "} " + super.toString();
    }

    @OneToMany(mappedBy = "candidateStatus")
    @JsonIgnore
    private Set<Candidates> candidatesSet = new HashSet<>();


}
