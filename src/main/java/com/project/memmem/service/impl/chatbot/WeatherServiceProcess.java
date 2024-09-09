package com.project.memmem.service.impl.chatbot;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.memmem.domain.entity.chatbot.LocationCoordinate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherServiceProcess {
	@Value("${weather.api.key}")
	private String apiKey;

	@Value("${weather.api.url}")
	private String apiUrl;

	// HTTP 요청을 보내기 위한 RestTemplate을 주입받습니다.
	private final RestTemplate restTemplate;
	// 위치 정보를 처리하는 LocationService를 주입받습니다.
	private final LocationServiceProcess locationService;

	// 현재 날씨 정보를 가져오는 메서드
	public Map<String, String> getCurrentWeather(String location) {
		Map<String, String> weatherInfo = new HashMap<>();
		
        if (isCoordinates(location)) {
            weatherInfo.put("location", "현재 위치");
        } else {
            weatherInfo.put("location", location);
        }
        if (!locationService.isValidLocation(location) && !isCoordinates(location)) {
            weatherInfo.put("error", "죄송합니다. 해당 지역의 날씨 정보를 찾을 수 없습니다.");
            return weatherInfo;
        }
		// 현재 날짜와 시간을 구합니다.
		LocalDateTime now = LocalDateTime.now();
		String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		String time = now.format(DateTimeFormatter.ofPattern("HHmm"));

		// API 키를 URL 인코딩합니다
		String decodedApiKey = URLDecoder.decode(apiKey, StandardCharsets.UTF_8);
		// 위치에 대한 좌표를 가져옵니다.
		LocationCoordinate coordinate = locationService.getCoordinateForLocation(location);
		// API 요청 URL을 생성합니다.
		String requestUrl = UriComponentsBuilder.fromHttpUrl(apiUrl).queryParam("serviceKey", decodedApiKey)
				.queryParam("pageNo", "1").queryParam("numOfRows", "10").queryParam("dataType", "JSON")
				.queryParam("base_date", date).queryParam("base_time", time).queryParam("nx", coordinate.getNx())
				.queryParam("ny", coordinate.getNy()).build().toUriString();

		try {
			// API에 GET 요청을 보내고 응답을 받아옵니다.
			String response = restTemplate.getForObject(requestUrl, String.class);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode root = mapper.readTree(response);
			// 응답 코드가 "00"인 경우 정상적으로 응답을 받은 것임을 의미합니다
			if (root.path("response").path("header").path("resultCode").asText().equals("00")) {
				JsonNode items = root.path("response").path("body").path("items").path("item");
				// 응답에서 기온(T1H)과 습도(REH)를 추출합니다.
				for (JsonNode item : items) {
					String category = item.path("category").asText();
					String value = item.path("obsrValue").asText();

					if ("T1H".equals(category)) {
						weatherInfo.put("temperature", value);
					} else if ("REH".equals(category)) {
						weatherInfo.put("humidity", value);
					}
				}
			} else {
				weatherInfo.put("error", "죄송합니다. 날씨 정보를 가져오는 데 문제가 발생했습니다.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			weatherInfo.put("error", "죄송합니다. 날씨 정보를 가져오는 데 문제가 발생했습니다.");
		}

		return weatherInfo;
	}
	
	 private boolean isCoordinates(String location) {
	        return location.matches("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?),\\s*[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$");
	    }
}