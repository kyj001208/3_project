package com.project.memmem.domain.dto.naver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HourlyWeatherDTO {
	
    private String time;
    private String temperature;
    private String weatherIcon;
    private String rainProbability;
}
