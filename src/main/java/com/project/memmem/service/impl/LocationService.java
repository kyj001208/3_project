package com.project.memmem.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.project.memmem.domain.entity.LocationCoordinate;

@Service
public class LocationService {
    private final Map<String, LocationCoordinate> locationCoordinates;
    private final Map<String, String> locationAliases;

    public LocationService() {
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
    }

    private void addLocation(String name, int nx, int ny, String... aliases) {
        locationCoordinates.put(name, new LocationCoordinate(nx, ny));
        for (String alias : aliases) {
            locationAliases.put(alias, name);
        }
    }

	public boolean isValidLocation(String location) {
		return locationCoordinates.containsKey(normalizeLocation(location));
	}

	public LocationCoordinate getCoordinateForLocation(String location) {
		return locationCoordinates.getOrDefault(normalizeLocation(location), new LocationCoordinate(60, 127));
	}

	public String normalizeLocation(String location) {
		return locationAliases.getOrDefault(location, location);
	}

	public Optional<String> findMatchingLocation(String input) {
		String normalizedInput = input.toLowerCase();
		return locationCoordinates.keySet().stream().filter(
				loc -> loc.toLowerCase().contains(normalizedInput) || normalizedInput.contains(loc.toLowerCase()))
				.findFirst();
	}
}