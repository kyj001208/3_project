package com.project.memmem.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.memmem.domain.entity.LocationCoordinate;

@Service
public class LocationServiceProcess {
    private final Map<String, LocationCoordinate> locationCoordinates;
    private final Map<String, String> locationAliases;

    public LocationServiceProcess() {
        locationCoordinates = new HashMap<>();
        locationAliases = new HashMap<>();

        initializeLocations();
    }

    private void initializeLocations() {
        // 주요 도시의 좌표 설정
        addLocation("서울", 60, 127, "서울특별시", "서울시");
        addLocation("부산", 98, 76, "부산광역시");
        addLocation("인천", 55, 124, "인천광역시");
        addLocation("대구", 89, 90, "대구광역시");
        addLocation("광주", 58, 74, "광주광역시");
        addLocation("대전", 67, 100, "대전광역시");
        addLocation("울산", 102, 84, "울산광역시");    
        addLocation("수원", 60, 121, "수원시");
        addLocation("춘천", 73, 134, "춘천시");
        addLocation("청주", 69, 106, "청주시");
        addLocation("전주", 63, 89, "전주시");
        addLocation("목포", 50, 67, "목포시");
        addLocation("포항", 102, 94, "포항시");
        addLocation("창원", 97, 76, "창원시", "마산", "진해");
        addLocation("제주", 52, 38, "제주시", "제주도");
        addLocation("강릉", 92, 131, "강릉시");
        addLocation("속초", 87, 141, "속초시");
        addLocation("원주", 76, 122, "원주시");
        addLocation("김해", 95, 77, "김해시");
        addLocation("여수", 73, 66, "여수시");
        addLocation("충주", 76, 114, "충주시");
        addLocation("세종", 66, 103, "세종특별자치시", "세종시");
        addLocation("용인", 62, 120, "용인시");
        addLocation("고양", 57, 128, "고양시");
        addLocation("성남", 62, 123, "성남시");
        addLocation("구미", 89, 91, "구미시");
        addLocation("통영", 91, 67, "통영시");
        addLocation("독도", 144, 123, "독도섬");
        addLocation("울릉도", 127, 127, "울릉군");
    }
    // 위치와 좌표를 추가하고, 별칭을 설정하는 메소드
    private void addLocation(String name, int nx, int ny, String... aliases) {
    	// 기본 위치를 등록
        locationCoordinates.put(name, new LocationCoordinate(nx, ny));
        // 주어진 별칭에 대해 해당 위치의 이름으로 매핑
        for (String alias : aliases) {
            locationAliases.put(alias, name);
        }
    }
    // 위치가 유효한지 확인하는 메소드
	public boolean isValidLocation(String location) {
		return locationCoordinates.containsKey(normalizeLocation(location));
	}
	// 위치에 대한 좌표를 반환하는 메소드. 기본 좌표(서울)로 대체 가능
    public LocationCoordinate getCoordinateForLocation(String location) {
    	// 위치 문자열에 쉼표가 포함되어 있는지 확인 (위도, 경도 형식인지 여부 확인)
        if (location.contains(",")) {
        	// 쉼표를 기준으로 문자열을 분리하여 배열로 저장
            String[] coords = location.split(",");
            
            // 분리된 배열이 정확히 2개의 값(위도와 경도)으로 나뉘었는지 확인
            if (coords.length == 2) {
                try {
                    double lat = Double.parseDouble(coords[0]);
                    double lon = Double.parseDouble(coords[1]);
                    // 변환된 위도와 경도를 소수점 반올림하여 LocationCoordinate 객체 생성 후 반환
                    return new LocationCoordinate((int)Math.round(lat), (int)Math.round(lon));
                } catch (NumberFormatException e) {
                    // 파싱 실패 시 기본 좌표 반환
                }
            }
        }
        // 쉼표가 없는 경우 또는 좌표 변환 실패 시, location 문자열을 표준화하여 미리 저장된 좌표에서 찾음
        // 만약 해당 위치가 없으면 기본 좌표 (60, 127) 반환
        return locationCoordinates.getOrDefault(normalizeLocation(location), new LocationCoordinate(60, 127));
    }
	// 위치 이름을 정규화하는 메소드. 별칭이 있는 경우 정규화
	public String normalizeLocation(String location) {
		return locationAliases.getOrDefault(location, location);
	}
	
	// 사용자 입력과 매칭되는 위치를 찾는 메소드
	public Optional<String> findMatchingLocation(String input) {
		String normalizedInput = input.toLowerCase();
		return locationCoordinates.keySet().stream().filter(
				loc -> loc.toLowerCase().contains(normalizedInput) || normalizedInput.contains(loc.toLowerCase()))
				.findFirst();
	}
}