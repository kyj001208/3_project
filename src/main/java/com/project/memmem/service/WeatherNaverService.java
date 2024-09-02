package com.project.memmem.service;

import java.io.IOException;

import com.project.memmem.domain.dto.user.WeatherInfoDTO;

public interface WeatherNaverService {
	
	WeatherInfoDTO getWeatherInfo(String city) throws IOException;

}
