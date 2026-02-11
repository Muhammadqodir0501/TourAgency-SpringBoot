package org.example.touragency.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourCreatedEvent implements Serializable {
    private String tourTitle;
    private String city;
    private BigDecimal price;
    private LocalDate startDate;
    private String authorEmail;
}
