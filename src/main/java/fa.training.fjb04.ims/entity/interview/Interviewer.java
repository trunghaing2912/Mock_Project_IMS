package fa.training.fjb04.ims.entity.interview;

import fa.training.fjb04.ims.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Interviewer extends BaseEntity<Integer> {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String interviewerName;

    @Column(nullable = false,unique = true)
    @Email(message = "{ME009}", regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")
    private String email;

    @ManyToMany(mappedBy = "interviewers")
    private Set<Interview> interviews = new HashSet<>();


}
