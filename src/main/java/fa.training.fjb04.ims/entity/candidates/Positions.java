package fa.training.fjb04.ims.entity.candidates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fa.training.fjb04.ims.entity.BaseEntity;
import fa.training.fjb04.ims.entity.offer.Offer;
import fa.training.fjb04.ims.entity.user.User;
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
public class Positions extends BaseEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "position_id")
    private Integer id;

    @Column(name = "position_name")
    private String positionName;

    @OneToMany(mappedBy = "position")
    @JsonIgnore
    private Set<Candidates> candidatesSet = new HashSet<>();

    @OneToMany(mappedBy = "position")
    @JsonIgnore
    private Set<User> userSet = new HashSet<>();


    @OneToMany(mappedBy = "position")
    @JsonIgnore
    private Set<Offer> offersSet = new HashSet<>();
}
