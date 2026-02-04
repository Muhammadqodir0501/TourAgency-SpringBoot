package org.example.touragency.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.touragency.model.base.BaseEntity;


@Entity
@Table(name = "favourite_tours")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FavouriteTour extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

}
