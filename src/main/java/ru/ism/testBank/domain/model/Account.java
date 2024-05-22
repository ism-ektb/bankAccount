package ru.ism.testBank.domain.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Check;


@Entity
@Table(name = "accounts")
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode
@Check(constraints = "balance > 0 AND count < 16")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    User user;
    private long balance;
    @Builder.Default
    private long count = 0;


}
