package org.example.touragency.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TourUpdateDto {
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate returnDate;
    private BigDecimal price;
    private String hotel;
    private String city;
    private int seatsTotal;
    private boolean isAvailable = true;
    private float discountPercent;
}
