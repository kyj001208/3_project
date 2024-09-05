package com.project.memmem.domain.dto.naver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@AllArgsConstructor
public class WeatherInfoDTO {
    private String temperature;
    private String status;
    private String humidity;

}
