package org.example.touragency.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.touragency.model.base.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tours")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tour extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agency_id", nullable = false)
    private User agency;

    @Column(nullable = false)
    private String title;
    private String description;
    private int nights;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "return_date")
    private LocalDate returnDate;

    @Column(nullable = false)
    private BigDecimal price;
    private BigDecimal priceWithDiscount;

    @Column(nullable = false)
    private String hotel;

    @Column(nullable = false)
    private String city;

    @Column(name = "seats_total")
    private int seatsTotal;

    @Column(name = "seats_available")
    private int seatsAvailable;
    private Long views = 0L;
    private float rating = 0f;

    @Column(name = "is_available")
    private boolean isAvailable = true;

    @Column(name = "discount_percent")
    private float discountPercent = 0;


}
