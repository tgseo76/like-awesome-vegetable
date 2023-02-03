package com.i5e2.likeawesomevegetable.domain.point.entity;

import com.i5e2.likeawesomevegetable.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "t_user_point")
public class UserPoint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_point")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "point_total_balance")
    private Long pointTotalBalance;

    public UserPoint(User user, Long pointTotalBalance) {
        this.user = user;
        this.pointTotalBalance = pointTotalBalance;
    }

    public void updatePointTotalBalance(Long pointTotalBalance) {
        this.pointTotalBalance = pointTotalBalance;
    }
}
