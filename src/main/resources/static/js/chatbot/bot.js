var client;
let key;
let flag = false; // 챗봇이 열려있는 상태를 추적하는 플래그
let isInScenario = false; // 시나리오 모드 플래그
let weatherScenarioStep = 0;
let selectedLocation = '';

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
            <div class="bot-name">멤멤</div>
            <div class="part chatbot">
                <p>
                    안녕하세요. <br> 
                    안내봇입니다.<br>
                    무엇을 도와드릴까요?
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
                    <button class="notice-button" onclick="sendQuickReply('소모임 참가하고 싶어요')">소모임 참가</button>
                    <button class="notice-button" onclick="sendQuickReply('오늘의 날씨 알려주세요')">날씨 정보</button>
                    <button class="notice-button" onclick="sendQuickReply('소모임 후기 보고 싶어요')">후기 보기</button>
                </div>
            </div>
        </div>
    </div>`;
    showMessage(buttonsHTML);
}

// 시나리오 시작 함수
function startScenario(message) {
    isInScenario = true;
    sendQuickReply(message);
}

// 빠른 답변 메시지를 전송하는 함수
function sendQuickReply(message) {
    document.getElementById("question").value = message;
    btnMsgSendClicked();
}

// WebSocket 연결 및 처리
function connect() {
    client = Stomp.over(new SockJS('/bookBot'));
    client.connect({}, (frame) => {
        console.log("Connected to WebSocket server with frame:", frame);

        key = new Date().getTime();
        client.subscribe(`/topic/bot/${key}`, (response) => {
            console.log("응답완료!!!");
            var msgObj = JSON.parse(response.body);
            console.log("Received message from server:", msgObj);

            var now = new Date();
            var time = formatTime(now);

            if (msgObj.answer.includes("어느 지역의 날씨를 알려드릴까요?")) {
                weatherScenarioStep = 1;
                showMessage(createBotMessage(msgObj.answer, time));
            } else if (msgObj.answer.includes("현재 기온은") && msgObj.answer.includes("습도는")) {
                var weatherInfo = createBotMessage(msgObj.answer, time);
                showMessage(weatherInfo);
                
                // 날씨 정보 제공 후 '다른 답변 찾기' 버튼 표시
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
                        <div class="time">${time}</div>
                    </div>
                </div>`;
                showMessage(buttonHTML);
                weatherScenarioStep = 0;
            } else {
                var tag = createBotMessage(msgObj.answer, time);
                showMessage(tag);
            }

            // 시나리오 모드에서 버튼 표시 (한 번만 실행)
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

            if (msgObj.answer.includes("참가")) {
                var buttonHTML = `<div class="msg bot flex">
                    <div class="icon">
                        <img src="/images/bot-img-none.png">
                    </div>
                    <div class="message">
                        <div class="part chatbot">
                            <p>아래 버튼을 통해 참가 신청 페이지로 이동해주세요!</p>
                            <div class="button-container">
                                <button class="faq-button" onclick="location.href='/create-group';">참가 신청</button>
                            </div>
                        </div>
                        <div class="time">${time}</div>
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
                        <div class="time">${time}</div>
                    </div>
                </div>`;
                showMessage(buttonHTML);
            }

            // 시나리오 종료 처리
            if (msgObj.endScenario) {
                isInScenario = false;
                if (msgObj.answer.includes("죄송합니다")) {
                    // 잠시 대기 후 웰컴 메시지와 빠른 답변 버튼 표시
                    setTimeout(() => {
                        showWelcomeMessage();
                        showQuickReplyButtons();
                    }, 1000); // 1초 후 실행
                } else {
                    showQuickReplyButtons();
                }
            } else {
                isInScenario = true;
            }
        });
    });
}

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
                    <button class="notice-button" onclick="sendQuickReply('소모임 참가하고 싶어요')">소모임 참가</button>
                    <button class="notice-button" onclick="sendQuickReply('오늘의 날씨 알려주세요')">날씨 정보</button>
                    <button class="notice-button" onclick="sendQuickReply('소모임 후기 보고 싶어요')">후기 보기</button>
                </div>
            </div>
        </div>
    </div>`;
    showMessage(buttonsHTML);
}

function createBotMessage(message, time) {
    return `<div class="msg bot flex">
        <div class="icon">
            <img src="/images/chatbot_icon.png">
        </div>
        <div class="message">
            <div class="bot-name">멤멤</div>
            <div class="part chatbot">
                <p>${message}</p>
            </div>
            <div class="time">${time}</div>
        </div>
    </div>`;
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

// 버튼 클릭 이벤트 핸들러
function btnCloseClicked() {
    const botContainer = document.getElementById("bot-container");
    botContainer.classList.remove('open');
    saveBotState();
    disconnect();
    flag = false;
    isInScenario = false;
    weatherScenarioStep = 0;
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

function btnMsgSendClicked() {
    if (!client) {
        console.error("WebSocket client is not initialized.");
        return;
    }

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
        selectedLocation = question;
        weatherScenarioStep = 2;
    }

    var data = {
        key: key,
        content: question,
        inScenario: isInScenario || weatherScenarioStep > 0,
        weatherStep: weatherScenarioStep,
        selectedLocation: selectedLocation
    };
    client.send(`/message/bot/question`, {}, JSON.stringify(data));
    clearQuestion();
}

function clearQuestion() {
    document.getElementById("question").value = "";
}

// 페이지 로드 시 초기화
document.addEventListener('DOMContentLoaded', (event) => {
    btnCloseClicked();
    loadBotState();

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

