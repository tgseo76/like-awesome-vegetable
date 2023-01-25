package com.i5e2.likeawesomevegetable.domain.market;


import com.i5e2.likeawesomevegetable.domain.user.User;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "t_standby")
public class Standby {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "standby_id")
    private Long id;

    @ManyToOne//입잘자id bidder
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne //게시글 id
    @JoinColumn(name = "auction_id")
    private Auction auction;

    @Column(name = "bidding_price")
    private Long biddingPrice;
    @Column(name = "bidding_time")
    private Long biddingTime;


}