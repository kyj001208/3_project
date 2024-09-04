package com.project.memmem.domain.dto.naver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyForecastDTO {
	
	private String day;
    private String date;
    private String morningWeather;  // 아이콘 대신 날씨 상태 문자열 (예: "맑음", "흐림", "비")
    private String afternoonWeather;
    private String morningTemp;
    private String afternoonTemp;
    private String rainProbability;
    
    private String lowestTemp;  // 추가된 필드: 최저 기온
    private String highestTemp; // 추가된 필드: 최고 기온
}
