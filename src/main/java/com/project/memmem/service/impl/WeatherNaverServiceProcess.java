package com.project.memmem.service.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import com.project.memmem.domain.dto.user.WeatherInfoDTO;
import com.project.memmem.service.WeatherNaverService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherNaverServiceProcess implements WeatherNaverService{

	@Override
	public WeatherInfoDTO getWeatherInfo(String city) throws IOException {
	    String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
	    String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=" + encodedCity + "+날씨";
	    Document doc = Jsoup.connect(url)
	        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
	        .get();

	    // HTML 요소들 가져오기
	    Element temperatureElement = doc.selectFirst(".temperature_text"); // 온도 정보 선택자 (예시)
	    Element statusElement = doc.selectFirst(".weather_main");

	    // 온도와 상태가 null인지 확인
	    if (temperatureElement == null || statusElement == null) {
	        throw new IOException("날씨 정보를 가져오는데 실패했습니다. 요소를 찾을 수 없습니다.");
	    }

	    // 텍스트 추출
	    String temperature = temperatureElement.text().trim(); // 온도 정보 추출
	    String status = statusElement.text().trim(); // 날씨 상태 정보 추출

	    // 습도 정보가 없을 때 처리 로직 추가 (필요에 따라 수정)
	    String humidity = "정보 없음";

	    return new WeatherInfoDTO(temperature, status, humidity);
	}

}
