package org.example.touragency.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import org.example.touragency.model.base.BaseEntity;

import java.util.UUID;

@Entity
@Table(name = "ratings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating extends BaseEntity {

    @Column(name = "tour_id", nullable = false)
    private UUID tourId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private float rate;
}