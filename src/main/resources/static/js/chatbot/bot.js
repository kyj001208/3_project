var client;
let key;
let flag = false; // 챗봇이 열려있는 상태를 추적하는 플래그
let isInScenario = false; // 시나리오 모드 플래그
let weatherScenarioStep = 0;// 날씨 시나리오 단계 추적
let selectedLocation = '';// 선택된 위치
let currentCategory = ''; // 추가: 현재 선택된 카테고리 저장
let userLocation = null;

// WebSocket 지원 여부를 출력
function isWebSocketSupported() {
	return 'WebSocket' in window;
}

if (isWebSocketSupported()) {
	console.log("이 브라우저는 WebSocket을 지원합니다.");
} else {
	console.log("이 브라우저는 WebSocket을 지원하지 않습니다.");
}

// 시간 및 날짜 포맷 함수
function formatTime(now) {
	var ampm = (now.getHours() > 11) ? "오후" : "오전";
	var hour = now.getHours() % 12;
	if (hour == 0) hour = 12;
	var minute = now.getMinutes();
	var formattedMinute = String(minute).padStart(2, '0');
	return `${ampm} ${hour}:${formattedMinute}`;
}

function formatDate(now) {
	const year = now.getFullYear();
	const month = now.getMonth() + 1;
	const date = now.getDate();
	const dayOfWeek = now.getDay();
	const days = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
	return `${year}년 ${month}월 ${date}일 ${days[dayOfWeek]}`;
}

// 메시지 표시 및 날짜 표시
function showMessage(tag) {
	var chatContent = document.getElementById("chat-content");
	chatContent.innerHTML += tag;
	chatContent.scrollTop = chatContent.scrollHeight;
}

// 날짜가 오늘인 경우, 로컬 스토리지에 저장된 날짜와 비교하여 중복 표시 방지
function showDateIfNew() {
	var now = new Date();
	var today = formatDate(now);

	var savedDate = localStorage.getItem('lastDisplayedDate');

	if (savedDate !== today) {
		var dateTag = `<div class="flex center date">${today}</div>`;
		var chatContent = document.getElementById("chat-content");
		chatContent.innerHTML = dateTag + chatContent.innerHTML;
		localStorage.setItem('lastDisplayedDate', today);
	}
}

// 환영 메시지 표시 및 저장
function showWelcomeMessage() {
	const now = new Date();
	const today = formatDate(now);

	const welcomeMessage = `<div class="msg bot flex">
        <div class="icon">
            <img src="/images/chatbot_icon.png">
        </div>
        <div class="message">
            <div class="bot-name">음멤</div>
            <div class="part chatbot">
                <p>
                    음멤~<br> 
                    어떤 도움이 필요하신가요?<br>
                </p>
            </div>
        </div>
    </div>`;

	var hasShownWelcomeMessage = localStorage.getItem('hasShownWelcomeMessage');

	if (!hasShownWelcomeMessage) {
		showMessage(welcomeMessage);
		localStorage.setItem('hasShownWelcomeMessage', 'true');
		showQuickReplyButtonsfirst();
	}

	localStorage.setItem('lastOpenedDate', today);
	showDateIfNew();
}

// 빠른 답변 버튼을 표시하는 함수
function showQuickReplyButtonsfirst() {
	const buttonsHTML = `<div class="msg bot flex">
        <div class="icon">
            <img src="/images/bot-img-none.png">
        </div>
        <div class="message">
            <div class="part chatbot">
                <div class="button-con">
                    <button class="notice-button" onclick="startScenario('소모임 추천해주세요!')">소모임 추천</button>
                    <button class="notice-button" onclick="sendQuickReply('소모임 생성하고 싶어요')">소모임 생성</button>
                    <button class="notice-button" onclick="sendQuickReply('오늘의 날씨 알려주세요')">날씨 정보</button>
                    <button class="notice-button" onclick="sendQuickReply('소모임 후기 보고 싶어요')">후기 보기</button>
                </div>
            </div>
        </div>
    </div>`;
	showMessage(buttonsHTML);
}

// 시나리오 초기화 함수 수정
function resetScenario() {
	isInScenario = false;
	weatherScenarioStep = 0;
	selectedLocation = '';
	currentCategory = '';
	// 서버에 시나리오 초기화 요청
	if (client) {
		client.send("/message/bot/reset", {}, JSON.stringify({ key: key }));
	}
}

// 시나리오 시작 함수 수정
function startScenario(message) {
	resetScenario(); // 시나리오 시작 시 초기화
	isInScenario = true;
	sendQuickReply(message);
}

// 빠른 답변 메시지를 전송하는 함수
function sendQuickReply(message) {
	document.getElementById("question").value = message;
	btnMsgSendClicked();
}

// WebSocket 연결 및 처리 함수 수정
function connect() {
	// WebSocket 클라이언트를 생성하고, '/bookBot' 엔드포인트로 연결
	client = Stomp.over(new SockJS('/bookBot'));

	// WebSocket 서버에 연결 시도
	client.connect({}, (frame) => {
		console.log("Connected to WebSocket server with frame:", frame);

		// 고유한 세션 키를 생성 (현재 시간의 타임스탬프를 사용)
		key = new Date().getTime();

		// 특정 주제(/topic/bot/{key})에 대해 구독 설정
		client.subscribe(`/topic/bot/${key}`, (response) => {
			console.log("응답완료!!!");

			// 서버로부터 받은 메시지를 JSON 객체로 변환
			var msgObj = JSON.parse(response.body);
			console.log("Received message from server:", msgObj);

			// 현재 시간을 가져와서 지정된 형식으로 포맷
			var now = new Date();
			var time = formatTime(now);

			// 서버에서 받은 응답이 날씨 정보와 관련된 경우
			if (msgObj.answer.startsWith("weather_info:")) {
				// 날씨 정보 처리
				var weatherInfo = JSON.parse(msgObj.answer.substring("weather_info:".length));
			} else if (msgObj.answer.includes("어느 지역의 날씨를 알려드릴까요?")) {
				weatherScenarioStep = 1;
				showMessage(createBotMessage(msgObj.answer, time));
			} else if (msgObj.answer.includes("현재 기온은") && msgObj.answer.includes("습도는")) {
				var weatherInfo = createWeatherInfo(msgObj.answer, time);
				showMessage(weatherInfo);

				// 날씨 정보 제공 후 '다른 답변 찾기' 버튼 표시
				setTimeout(() => {
					var buttonHTML = `<div class="msg bot flex">
                        <div class="icon">
                            <img src="/images/bot-img-none.png">
                        </div>
                        <div class="message">
                            <div class="part chatbot">
                                <p>아래 버튼을 통해 다른 질문도 물어보세요!</p>
                                <div class="button-container">
                                    <button class="faq-button" onclick="showQuickReplyButtons()">질문</button>
                                </div>
                            </div>
                            <div class="time">${formatTime(new Date())}</div>
                        </div>
                    </div>`;
					showMessage(buttonHTML);
				}, 1000);
				weatherScenarioStep = 0;
			} else if (msgObj.answer.includes("죄송합니다. 해당 지역의 날씨 정보를 찾을 수 없습니다.")) {
				showMessage(createBotMessage(msgObj.answer, time));
				weatherScenarioStep = 0;

				// 오류 메시지 후 '다른 답변 찾기' 버튼 표시
				setTimeout(() => {
					var buttonHTML = `<div class="msg bot flex">
                        <div class="icon">
                            <img src="/images/bot-img-none.png">
                        </div>
                        <div class="message">
                            <div class="part chatbot">
                                <p>다른 질문을 해보시겠어요?</p>
                                <div class="button-container">
                                    <button class="faq-button" onclick="showQuickReplyButtons()">질문</button>
                                </div>
                            </div>
                            <div class="time">${formatTime(new Date())}</div>
                        </div>
                    </div>`;
					showMessage(buttonHTML);
				}, 1000);
			} else {
				var tag = createBotMessage(msgObj.answer, time);
				showMessage(tag);
			}

			// 시나리오 모드에서 버튼 표시
			if (msgObj.options && msgObj.options.length > 0) {
				var buttonHTML = `<div class="msg bot flex">
                    <div class="icon">
                        <img src="/images/bot-img-none.png">
                    </div>
                    <div class="message">
                        <div class="part chatbot">
                            <div class="button-con">
                                ${msgObj.options.map(option => `
                                    <button class="notice-button" onclick="sendQuickReply('${option}')">${option}</button>
                                `).join('')}
                            </div>
                        </div>
                    </div>
                </div>`;
				showMessage(buttonHTML);
			}

			// 카테고리 URL이 있는 경우 (시나리오의 마지막 단계)
			if (msgObj.categoryUrl) {
				currentCategory = msgObj.categoryUrl; // 현재 카테고리 저장
				var categoryButtonHTML = `<div class="msg bot flex">
            <div class="icon">
                <img src="/images/bot-img-none.png">
            </div>
            <div class="message">
                <div class="part chatbot">
                    <p>해당 소모임 목록을 보시겠습니까?</p>
                    <div class="button-container">
                        <button class="faq-button" onclick="location.href='${msgObj.categoryUrl}';">소모임 목록 보기</button>
                    </div>
                </div>
            </div>
        </div>`;
				showMessage(categoryButtonHTML);
			}

			if (msgObj.answer.includes("생성")) {
				var buttonHTML = `<div class="msg bot flex">
                    <div class="icon">
                        <img src="/images/bot-img-none.png">
                    </div>
                    <div class="message">
                        <div class="part chatbot">
                            <p>아래 버튼을 통해 그룹 생성 페이지로 이동해주세요!</p>
                            <div class="button-container">
                                <button class="faq-button" onclick="location.href='/create-group';">그룹 생성</button>
                            </div>
                        </div>
   
                    </div>
                </div>`;
				showMessage(buttonHTML);
			}
			if (msgObj.answer.includes("후기")) {
				var buttonHTML = `<div class="msg bot flex">
                    <div class="icon">
                        <img src="/images/bot-img-none.png">
                    </div>
                    <div class="message">
                        <div class="part chatbot">
                            <p>아래 버튼을 통해 후기 페이지로 이동해주세요!</p>
                            <div class="button-container">
                                <button class="faq-button" onclick="location.href='/mem/review';">후기 보기</button>
                            </div>
                        </div>
                    </div>
                </div>`;
				showMessage(buttonHTML);
			}

			// 시나리오 종료 처리 수정
			if (msgObj.endScenario) {
				resetScenario();
				if (msgObj.answer.includes("죄송합니다")) {
					setTimeout(() => {
						showWelcomeMessage();
						showQuickReplyButtons();
					}, 1000);
				} else if (!msgObj.categoryUrl) {
					showQuickReplyButtons();
				}
			}
		});
	});
}

// '질문' 버튼을 클릭했을 때 빠른 답변 버튼을 표시하는 함수
function showQuickReplyButtons() {
	const buttonsHTML = `<div class="msg bot flex">
        <div class="icon">
            <img src="/images/bot-img-none.png">
        </div>
        <div class="message">
            <div class="part chatbot">
                <p>어떤 정보가 더 필요하신가요?</p>
                <div class="button-con">
                    <button class="notice-button" onclick="startScenario('소모임 추천해주세요!')">소모임 추천</button>
                    <button class="notice-button" onclick="sendQuickReply('소모임 생성하고 싶어요')">소모임 생성</button>
                    <button class="notice-button" onclick="sendQuickReply('오늘의 날씨 알려주세요')">날씨 정보</button>
                    <button class="notice-button" onclick="sendQuickReply('소모임 후기 보고 싶어요')">후기 보기</button>
                </div>
            </div>
        </div>
    </div>`;
	showMessage(buttonsHTML);
}

// 챗봇 메시지를 생성하는 함수
function createBotMessage(message, time) {
	return `<div class="msg bot flex">
        <div class="icon">
            <img src="/images/chatbot_icon.png">
        </div>
        <div class="message">
            <div class="bot-name">음멤</div>
            <div class="part chatbot">
                <p>${message}</p>
            </div>
            <div class="time">${time}</div>
        </div>
    </div>`;
}

// 날씨 상태에 따른 아이콘을 선택하는 함수
function getWeatherIcon(temperature, humidity) {
	let icon = "";

	// 온도에 따른 기본 아이콘 설정
	if (temperature > 35) icon = "🔥"; // 폭염
	else if (temperature > 30) icon = "☀️"; // 매우 더움
	else if (temperature > 25) icon = "🌤️"; // 맑고 더움
	else if (temperature > 20) icon = "😎"; // 따뜻함
	else if (temperature > 15) icon = "🌻"; // 온화함
	else if (temperature > 10) icon = "🍃"; // 선선함
	else if (temperature > 5) icon = "🍂"; // 쌀쌀함
	else if (temperature > 0) icon = "❄️"; // 추움
	else if (temperature > -10) icon = "🥶"; // 매우 추움
	else icon = "☃️"; // 극한의 추위

	// 습도에 따른 아이콘 수정
	if (humidity > 90) {
		if (temperature > 0) icon = "🌧️"; // 폭우
		else icon = "🌨️"; // 폭설
	} else if (humidity > 80) {
		if (temperature > 0) icon = "🌦️"; // 비
		else icon = "🌨️"; // 눈
	} else if (humidity > 70) {
		icon += "💨";
	} else if (humidity < 30) {
		icon += "🏜️";
	}

	return icon;
}

// 날씨 정보를 표시하는 함수
function createWeatherInfo(message, time) {
	const regex = /(.+)의 현재 기온은 (.+)°C이고, 습도는 (.+)%입니다./;
	const match = message.match(regex);

	if (match) {
		const [, location, temperature, humidity] = match;
		const weatherIcon = getWeatherIcon(parseFloat(temperature), parseFloat(humidity));
		return `<div class="msg bot flex">
            <div class="icon">
                <img src="/images/chatbot_icon.png">
            </div>
            <div class="message">
                <div class="bot-name">음멤</div>
                <div class="part chatbot weather-info">
                    <h3>${location} 날씨 정보</h3>
                    <div class="weather-details">
                        <div class="weather-icon">${weatherIcon}</div>
                        <div class="weather-text">
                            <p>기온: ${temperature}°C</p>
                            <p>습도: ${humidity}%</p>
                        </div>
                    </div>
                </div>
                <div class="time">${time}</div>
            </div>
        </div>`;
	}
	return createBotMessage(message, time);
}

// Geolocation API를 사용
function requestUserLocation() {
	// Geolocation API를 사용할 수 있는지 확인 (브라우저가 위치 정보를 지원하는지 확인)
	if ("geolocation" in navigator) {
		// 사용자의 현재 위치를 비동기로 요청
		navigator.geolocation.getCurrentPosition(function(position) {
			// 위치 정보를 성공적으로 가져왔을 때 호출되는 콜백 함수
			userLocation = {
				// 사용자의 위치 정보를 객체로 저장 (위도와 경도)
				latitude: position.coords.latitude,
				longitude: position.coords.longitude
			};
			console.log("User location:", userLocation);
		}, function(error) {
			console.error("Error getting location:", error);
		});
	} else {
		console.log("Geolocation is not supported by this browser.");
	}
}


// WebSocket 연결 종료
function disconnect() {
	if (client) {
		client.disconnect(() => {
			console.log("Disconnected...");
		});
	}
}

// 상태 저장 및 복원
function saveBotState() {
	var isVisible = document.getElementById("bot-container").classList.contains('open');
	localStorage.setItem('botState', isVisible ? 'open' : 'closed');
}

// 챗봇 상태 로드 함수
function loadBotState() {
	var botState = localStorage.getItem('botState');
	const botContainer = document.getElementById("bot-container");

	if (botState === 'open') {
		botContainer.classList.add('open');
		flag = true;
		connect();
	} else {
		botContainer.classList.remove('open');
		flag = false;
		disconnect();
	}

	var hasShownWelcomeMessage = localStorage.getItem('hasShownWelcomeMessage');
	var wasChatReset = localStorage.getItem('chatReset');

	if (!hasShownWelcomeMessage || wasChatReset) {
		if (botState === 'open') {
			showWelcomeMessage();
			localStorage.removeItem('chatReset');
		}
	}
}

// 페이지를 떠날 때 챗봇 상태를 저장
window.addEventListener('beforeunload', function() {
	saveBotState();
	localStorage.removeItem('chatContent');
});

// 챗봇 종료 함수 수정
function btnCloseClicked() {
	const botContainer = document.getElementById("bot-container");
	botContainer.classList.remove('open');
	saveBotState();
	disconnect();
	flag = false;
	resetScenario(); // 시나리오 초기화 추가
	document.getElementById("chat-content").innerHTML = "";
	localStorage.removeItem('chatContent');
	localStorage.setItem('chatReset', 'true');
	localStorage.removeItem('hasShownWelcomeMessage');
}

function btnBotClicked() {
	if (flag) return;

	const botContainer = document.getElementById("bot-container");
	botContainer.classList.add('open');
	connect();
	flag = true;

	var hasShownWelcomeMessage = localStorage.getItem('hasShownWelcomeMessage');
	var wasChatReset = localStorage.getItem('chatReset');

	if (!hasShownWelcomeMessage || wasChatReset) {
		showWelcomeMessage();
		localStorage.removeItem('chatReset');
	}

	saveBotState();
}

// 메시지 전송 버튼 클릭 이벤트 핸들러
function btnMsgSendClicked() {
	// WebSocket 클라이언트가 초기화되었는지 확인
	if (!client) {
		console.error("WebSocket client is not initialized.");
		return;
	}
	// 사용자가 입력한 질문을 가져오고 공백을 제거한 후 저장
	var question = document.getElementById("question").value.trim();
	if (question.length < 2) {
		alert("질문은 최소 2글자 이상으로 부탁드립니다.");
		return;
	}
	var now = new Date();
	var time = formatTime(now);

	var tag = `<div class="msg user flex">
        <div class="message">
            <div class="part guest">
                <p>${question}</p>
            </div>
            <div class="time">${time}</div>
        </div>
    </div>`;

	showDateIfNew();
	showMessage(tag);

	if (weatherScenarioStep === 1) {
		if (question.toLowerCase() === "현재 내 위치" && userLocation) {
			// 사용자 위치가 있을 경우, 위도와 경도를 선택된 위치로 설정
			selectedLocation = `${userLocation.latitude},${userLocation.longitude}`;
			question = "현재 위치"; // 서버로 보낼 질문을 "현재 위치"로 수정
		} else {
			selectedLocation = question;
		}
		weatherScenarioStep = 2; // 날씨 시나리오 단계 진행
	}
	// 서버로 보낼 데이터 객체 생성
	var data = {
		key: key,
		content: question,
		inScenario: isInScenario || weatherScenarioStep > 0, // 시나리오 진행 여부
		weatherStep: weatherScenarioStep, 
		selectedLocation: selectedLocation
	};
	client.send(`/message/bot/question`, {}, JSON.stringify(data));
	clearQuestion();
}

// 입력창 클리어 함수 수정
function clearQuestion() {
	var questionInput = document.getElementById("question");
	questionInput.value = "";
	questionInput.focus(); // 옵션: 입력창에 포커스 유지
}
// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', (event) => {
	btnCloseClicked();
	loadBotState();
	requestUserLocation();

	document.getElementById("chat-icon").addEventListener('click', btnBotClicked);
	document.getElementById("close-button").addEventListener('click', btnCloseClicked);
	document.getElementById("send-button").addEventListener('click', btnMsgSendClicked);

	document.getElementById("question").addEventListener('keydown', function(event) {
		if (event.key === 'Enter') {
			event.preventDefault();
			btnMsgSendClicked();
		}
	});
});