#include <ESP8266WiFi.h>
#include <Firebase_ESP_Client.h>
#include <addons/RTDBHelper.h>
#include <Esp.h>

#include <OneWire.h>
#include <DallasTemperature.h>

#define ONE_WIRE_BUS D2  // 온도 센서는 D4에 꽂을 것 (왜지?)

OneWire ourWire(ONE_WIRE_BUS);
DallasTemperature sensors(&ourWire);
 
#define FIREBASE_HOST "dodamdodam-d52ba-default-rtdb.firebaseio.com" // http달린거 빼고 적어야 됩니다.
#define FIREBASE_AUTH "KJAXKMCKA8AFjwp5STWEPczSzzSXSK6ejCtUHcqI" // 데이터베이스 비밀번호
#define WIFI_SSID "CS" // 연결 가능한 wifi의 ssid
#define WIFI_PASSWORD "12345678" // wifi 비밀번호

FirebaseData firebaseData;
FirebaseJson json;
FirebaseData fbdo;  // Firebase 데이터 객체
FirebaseAuth auth;  // Firebase 인증용 객체
FirebaseConfig config;  // Firebase 설정용 객체

void printResult(FirebaseData &data);

int i = 0;

int pin = D7;

void setup() {
  Serial.begin(115200);
  sensors.begin();
  pinMode(pin, INPUT);

  Serial.println("Booting...");

  WiFi.mode(WIFI_STA);
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  WiFi.setHostname("TestESP8266");

  while(WiFi.status() != WL_CONNECTED){
    delay(500);
    Serial.print("");
  }

  Serial.println("WiFi Connected");
  Serial.println(WiFi.localIP());

  config.database_url = FIREBASE_HOST;                   // RTDB의 URL 적용
  config.signer.tokens.legacy_token = FIREBASE_AUTH;  // RTDB의 비밀번호 적용
  Firebase.begin(&config, &auth);
  Firebase.reconnectWiFi(true);

  String path = "/Test";

  Serial.println("Connected...");
  delay(500);
}

void loop() {
  i++;
  delay(500);

  sensors.requestTemperatures();
  float temperature = sensors.getTempCByIndex(0);  // 온도 값
  float turbidity = analogRead(pin);  // 탁도 값
  float waterLevel = analogRead(A0);  // 수위 값

  Serial.print("turbidity : "); Serial.println(turbidity * (5.0 / 1024.0));
  Serial.print("temp : "); Serial.println(temperature);
  Serial.print("turb : "); Serial.println(turbidity);
  Serial.print("water : "); Serial.println(waterLevel);

  if(Firebase.RTDB.setInt(&fbdo, "/sensor/temperature", temperature) == true){
    Serial.println("Saving temperature!");
  } else{
    Serial.println("Saving temperature Failed...");
    Serial.println(fbdo.errorReason().c_str());
  }

  if(Firebase.RTDB.setInt(&fbdo, "/sensor/turbidity", turbidity) == true){
    Serial.println("Saving turbidity!");
  } else{
    Serial.println("Saving turbidity Failed...");
    Serial.println(fbdo.errorReason().c_str());
  }

  if(Firebase.RTDB.setInt(&fbdo, "/sensor/waterLevel", waterLevel) == true){
    Serial.println("Saving waterLevel!");
  } else{
    Serial.println("Saving waterLevel Failed...");
    Serial.println(fbdo.errorReason().c_str());
  }


  if(Firebase.RTDB.setInt(&fbdo, "/test", i) == true){

  } else{
    Serial.println("Failed...");
    Serial.println(fbdo.errorReason().c_str());
  }

  delay(1000);
}
