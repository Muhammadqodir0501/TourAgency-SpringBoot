package org.example.touragency.dto.request;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TourAddDto {
    private String title;
    private String description;
    private LocalDate startDate;
    private LocalDate returnDate;
    private BigDecimal price;
    private String hotel;
    private String city;
    private int seatsTotal;
}
