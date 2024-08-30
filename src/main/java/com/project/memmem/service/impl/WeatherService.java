package com.project.memmem.service.impl;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.memmem.domain.entity.LocationCoordinate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherService {
	 @Value("${weather.api.key}")
	    private String apiKey;

	    @Value("${weather.api.url}")
	    private String apiUrl;

	    private final RestTemplate restTemplate;
	    private final LocationService locationService;

	    public String getCurrentWeather(String location) {
	        LocalDateTime now = LocalDateTime.now();
	        String date = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
	        String time = now.format(DateTimeFormatter.ofPattern("HHmm"));

	        String decodedApiKey = URLDecoder.decode(apiKey, StandardCharsets.UTF_8);

	        LocationCoordinate coordinate = locationService.getCoordinateForLocation(location);

	        String requestUrl = UriComponentsBuilder.fromHttpUrl(apiUrl)
	                .queryParam("serviceKey", decodedApiKey)
	                .queryParam("pageNo", "1")
	                .queryParam("numOfRows", "10")
	                .queryParam("dataType", "JSON")
	                .queryParam("base_date", date)
	                .queryParam("base_time", time)
	                .queryParam("nx", coordinate.getNx())
	                .queryParam("ny", coordinate.getNy())
	                .build()
	                .toUriString();

	        try {
	            String response = restTemplate.getForObject(requestUrl, String.class);
	            ObjectMapper mapper = new ObjectMapper();
	            JsonNode root = mapper.readTree(response);

	            if (root.path("response").path("header").path("resultCode").asText().equals("00")) {
	                JsonNode items = root.path("response").path("body").path("items").path("item");

	                String temperature = "알 수 없음";
	                String humidity = "알 수 없음";

	                for (JsonNode item : items) {
	                    String category = item.path("category").asText();
	                    if ("T1H".equals(category)) {
	                        temperature = item.path("obsrValue").asText();
	                    } else if ("REH".equals(category)) {
	                        humidity = item.path("obsrValue").asText();
	                    }
	                }

	                return String.format("%s의 현재 기온은 %s°C이고, 습도는 %s%%입니다.", location, temperature, humidity);
	            } else {
	                return "죄송합니다. 날씨 정보를 가져오는 데 문제가 발생했습니다.";
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            return "죄송합니다. 날씨 정보를 가져오는 데 문제가 발생했습니다.";
	        }
	    }
	}