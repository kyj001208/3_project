package com.project.memmem.service.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.project.memmem.domain.dto.naver.WeatherInfoDTO;
import com.project.memmem.domain.dto.naver.WeeklyForecastDTO;
import com.project.memmem.service.WeatherNaverService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class WeatherNaverServiceProcess implements WeatherNaverService {

    /**
     * 도시의 현재 날씨 정보를 가져오는 메서드
     * 
     * @param city - 날씨 정보를 가져올 도시 이름
     * @return WeatherInfoDTO - 현재 날씨 정보를 담은 객체
     * @throws IOException - HTML 요소를 찾지 못하거나 네트워크 연결에 문제가 있을 경우 발생
     */
    @Override
    public WeatherInfoDTO getWeatherInfo(String city) throws IOException {
        // 도시 이름을 URL 인코딩
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        // 네이버 날씨 검색 URL 생성
        String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=" + encodedCity + "+날씨";

        // Jsoup을 사용하여 HTML 문서 가져오기
        Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            .get();

        // HTML 요소들 선택 (온도, 날씨 상태)
        Element temperatureElement = doc.selectFirst(".temperature_text"); // 온도 정보
        Element statusElement = doc.selectFirst(".weather_main"); // 날씨 상태 정보

        // 요소가 존재하는지 확인하고 없을 경우 예외 발생
        if (temperatureElement == null || statusElement == null) {
            throw new IOException("날씨 정보를 가져오는데 실패했습니다. 요소를 찾을 수 없습니다.");
        }

        // 텍스트 추출 및 정리
        String temperature = temperatureElement.text().trim(); // 온도 텍스트 추출 및 트림
        String status = statusElement.text().trim(); // 날씨 상태 텍스트 추출 및 트림

        // 습도 정보가 없을 때 기본값 설정
        String humidity = "정보 없음";

        // 결과 DTO 객체 생성 및 반환
        return new WeatherInfoDTO(temperature, status, humidity);
    }

    /**
     * 도시의 주간 날씨 예보 정보를 가져오는 메서드
     * 
     * @param city - 주간 예보를 가져올 도시 이름
     * @return List<WeeklyForecastDTO> - 주간 예보 정보를 담은 리스트
     * @throws IOException - HTML 요소를 찾지 못하거나 네트워크 연결에 문제가 있을 경우 발생
     */
    @Override
    public List<WeeklyForecastDTO> getWeeklyForecast(String city) throws IOException {
        // 도시 이름을 URL 인코딩
        String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
        // 네이버 날씨 검색 URL 생성
        String url = "https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=1&ie=utf8&query=" + encodedCity + "+날씨";

        // Jsoup을 사용하여 HTML 문서 가져오기
        Document doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            .get();

        List<WeeklyForecastDTO> weeklyForecasts = new ArrayList<>(); // 주간 예보 정보를 저장할 리스트
        Elements weekItems = doc.select(".weekly_forecast_area .week_item"); // 주간 예보 항목 선택

        // 각 주간 예보 요소를 순회하며 데이터 추출
        for (Element weekItem : weekItems) {
            String day = weekItem.select(".day").text(); // 요일 정보 추출
            String date = weekItem.select(".date").text(); // 날짜 정보 추출

            // 오전과 오후의 날씨 정보를 추출
            Element morningWeather = weekItem.selectFirst(".weather_inner:nth-child(1)");
            Element afternoonWeather = weekItem.selectFirst(".weather_inner:nth-child(2)");

            // 오전, 오후의 날씨 상태 변환 (아이콘 클래스 -> 상태 텍스트)
            String morningWeatherStatus = convertWeatherIconToStatus(morningWeather.select(".weather_left .weather_inner i").attr("class"));
            String afternoonWeatherStatus = convertWeatherIconToStatus(afternoonWeather.select(".weather_left .weather_inner i").attr("class"));

            // 오전, 오후의 온도 정보 추출 및 기호 제거
            String morningTemp = morningWeather.select(".temperature").text().replace("°", "");
            String afternoonTemp = afternoonWeather.select(".temperature").text().replace("°", "");

            // 강수 확률 정보 추출
            String rainProbability = weekItem.select(".rainfall").text();

            // 주간 예보 DTO 객체 생성 및 리스트에 추가
            WeeklyForecastDTO forecast = new WeeklyForecastDTO(
                day, date, 
                morningWeatherStatus, afternoonWeatherStatus, 
                morningTemp, afternoonTemp, 
                rainProbability
            );
            weeklyForecasts.add(forecast);
        }

        // 예보 데이터가 없을 경우 예외 발생
        if (weeklyForecasts.isEmpty()) {
            throw new IOException("주간 예보 정보를 가져오는데 실패했습니다. 요소를 찾을 수 없습니다.");
        }

        // 주간 예보 리스트 반환
        return weeklyForecasts;
    }

    /**
     * 네이버 날씨 아이콘 클래스를 실제 날씨 상태 텍스트로 변환하는 메서드
     * 
     * @param iconClass - 날씨 아이콘의 CSS 클래스
     * @return String - 날씨 상태 텍스트 (맑음, 흐림, 비, 눈 등)
     */
    private String convertWeatherIconToStatus(String iconClass) {
        if (iconClass.contains("ico_sun")) return "맑음"; // 맑은 날씨
        if (iconClass.contains("ico_cloud")) return "흐림"; // 흐린 날씨
        if (iconClass.contains("ico_rain")) return "비"; // 비 오는 날씨
        if (iconClass.contains("ico_snow")) return "눈"; // 눈 오는 날씨
        return "기타"; // 기타 상태 (기본값)
    }

    @Override
    public Map<String, WeatherInfoDTO> getMultipleWeatherInfo(List<String> cities) throws IOException {
        Map<String, WeatherInfoDTO> weatherMap = new HashMap<>();
        for (String city : cities) {
            try {
                WeatherInfoDTO weatherInfo = getWeatherInfo(city);
                weatherMap.put(city, weatherInfo);
            } catch (IOException e) {
                // 에러 로깅 또는 처리
                weatherMap.put(city, new WeatherInfoDTO("N/A", "정보 없음", "정보 없음"));
            }
        }
        return weatherMap;
    }
}