# CropDoc

**On-device crop disease diagnosis and soil health analysis for African smallholder farmers.**

Powered by **Gemma 4 E2B** via **LiteRT-LM** — everything runs privately on the farmer's Android phone. No internet connection required for AI analysis. No data ever leaves the device.

[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://android.com)
[![Kaggle](https://img.shields.io/badge/Kaggle-Gemma%204%20Good%20Hackathon-20BEFF.svg?logo=kaggle)](https://www.kaggle.com/competitions/gemma-4-good-hackathon)

---

## The Problem

Rural Zimbabwe has approximately 40% mobile internet penetration, with data costs averaging 3–5% of daily income per session. A cloud-dependent diagnostic tool creates a paywall at exactly the moment a farmer is standing in a field looking at dying crops. CropDoc eliminates that barrier entirely — diagnosis is as fast as the phone's inference speed, not the network.

---

## Features

- **Crop disease detection** — photograph a crop and get an instant AI diagnosis with treatment plan
- **Soil sensor integration** — Bluetooth LE connection to a custom ESP32-based NPK/pH/moisture/temperature sensor
- **Combined multimodal analysis** — Gemma 4 reasons over both image AND live soil data together
- **Health score** — overall crop health 0–100 with colour-coded severity levels
- **Farm Map** — free-form pannable canvas to draw and name farm zones; tap a zone to run a zone-specific analysis
- **Farm Assistant chat** — persistent multi-turn conversation with Gemma 4 about any farming question
- **SMS weather alerts** — daily 6am/6pm weather updates sent via SMS to registered farmers (requires backend, see below)
- **Agentic Mode** — toggle on to let the assistant autonomously monitor farm conditions and surface alerts
- **Analysis history** — all past diagnoses stored locally in Room DB
- **Multi-language** — English, Shona, and Amharic UI support
- **100% offline AI** — no cloud, no API keys, no connectivity required for core features

---

## Tech Stack

| Layer | Technology |
|---|---|
| On-device AI | Gemma 4 E2B via LiteRT-LM (Kotlin API) |
| Vision | LiteRT-LM `visionBackend = Backend.GPU()` |
| Camera | CameraX |
| Bluetooth | Android BLE API |
| Local DB | Room (analysis history) |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| Weather backend | FastAPI + Twilio + OpenWeatherMap |
| Min SDK | Android 8.0 (API 26) |

---

## Project Structure

```
cropdoc/
├── app/
│   └── src/main/java/com/cropdoc/app/
│       ├── MainActivity.kt                   # Compose nav host + permissions
│       ├── data/
│       │   ├── model/
│       │   │   ├── Models.kt                 # SoilReading, AnalysisResult, BleState…
│       │   │   └── CropDocAiEngine.kt        # LiteRT-LM engine wrapper
│       │   └── ble/
│       │       └── SoilSensorBleManager.kt   # BLE scan / connect / parse
│       ├── viewmodel/
│       │   └── CropDocViewModel.kt           # Coordinates AI + BLE + camera
│       └── ui/
│           ├── theme/                        # Green & white Material 3 theme
│           ├── components/                   # Shared Compose components
│           └── screens/
│               ├── HomeScreen.kt             # Dashboard
│               ├── CameraScreen.kt           # Camera capture + streaming analysis
│               ├── SensorScreen.kt           # BLE sensor management
│               ├── HistoryScreen.kt          # Past analyses
│               ├── FarmMapScreen.kt          # Zone map canvas
│               └── ChatScreen.kt             # Farm assistant chat
├── backend/                                  # Weather SMS FastAPI server
│   ├── main.py
│   ├── requirements.txt
│   └── .env.example
├── hardware/                                 # ESP32 soil sensor firmware
│   └── soil_sensor.ino
└── README.md
```

---

## Setup

### 1. Clone the repo

```bash
git clone https://github.com/debroglie99/cropdoc.git
cd cropdoc
```

### 2. Download the Gemma 4 E2B model

The model file (~2.5 GB) must be downloaded separately and placed in:

```
app/src/main/assets/gemma-4-E2B-it.litertlm
```

```bash
# Install huggingface-cli
pip install huggingface_hub

# Download (accept Gemma licence at huggingface.co/google/gemma-4-E2B first)
huggingface-cli download \
  google/gemma-4-E2B-it-litert-lm \
  gemma-4-E2B-it-int4.litertlm \
  --local-dir app/src/main/assets/

# Rename to match the expected filename
mv app/src/main/assets/gemma-4-E4B-it-int4.litertlm \
   app/src/main/assets/gemma-4-E4B-it.litertlm
```

> **Note:** The `assets/` folder is excluded from git via `.gitignore` due to file size.
> For production, use [Play Asset Delivery](https://developer.android.com/guide/playcore/asset-delivery) to deliver the model on first install.

### 3. Build & run the Android app

```bash
./gradlew assembleDebug
# or open in Android Studio and press Run
```

Requires Android Studio Meerkat (2024.3) or later.

---

## Weather SMS Backend

The weather backend is a separate FastAPI service that sends daily SMS weather alerts to registered farmers at 6am and 6pm via Twilio + OpenWeatherMap.

### Run locally

```bash
cd backend
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
uvicorn main:app --reload --port 8000
```

Open `http://127.0.0.1:8000/docs` to see the full API.

### Key endpoints

| Endpoint | Description |
|---|---|
| `POST /register` | Register a farmer (phone number + location) |
| `DELETE /unregister/{phone}` | Remove a farmer |
| `POST /send-weather-now` | Manually trigger SMS to all registered farmers |
| `GET /farmers` | List all registered farmers |

### Environment variables

Copy `.env.example` to `.env` and fill in your keys:

```
TWILIO_ACCOUNT_SID=your_sid
TWILIO_AUTH_TOKEN=your_token
TWILIO_PHONE_NUMBER=+1xxxxxxxxxx
OPENWEATHER_API_KEY=your_key
```

### Testing the SMS alerts

> **Note:** Twilio trial accounts and unregistered numbers are subject to A2P 10DLC restrictions — outbound SMS to arbitrary numbers will be blocked without carrier registration.

**To test locally:**
1. Create a free [Twilio trial account](https://www.twilio.com/try-twilio)
2. In the Twilio console, add your own phone number as a **Verified Caller ID**
3. Register yourself as a farmer via the `/register` endpoint using your verified number
4. Hit `POST /send-weather-now` to trigger an immediate SMS to yourself

This simulates exactly what a registered farmer would receive at 6am/6pm daily.

---

## Soil Sensor Hardware

### Components

| Component | Example Part |
|---|---|
| Microcontroller | ESP32 DevKit v1 |
| Soil NPK sensor | RS485 NPK sensor (e.g. JXCT) |
| pH sensor | Analog pH probe + ADS1115 ADC |
| Moisture sensor | Capacitive soil moisture sensor v1.2 |
| Temperature | DS18B20 waterproof probe |
| RS485 adapter | MAX485 module |
| Power | 3.7V LiPo + TP4056 charger |

> **No hardware?** Enable **Mock Sensor Mode** in the app settings to simulate live soil readings for demo and testing purposes.

### BLE Protocol

The app expects a single BLE service with one notify characteristic:

```
Service UUID:        12345678-1234-5678-1234-56789abcdef0
Characteristic UUID: 12345678-1234-5678-1234-56789abcdef1
Properties:          NOTIFY
Payload:             24 bytes, 6 × float32 little-endian

Byte offset  Field         Unit
0–3          moisture      % (0–100)
4–7          pH            pH (0–14)
8–11         nitrogen      mg/kg
12–15        phosphorus    mg/kg
16–19        potassium     mg/kg
20–23        temperature   °C
```

### ESP32 Firmware (Arduino sketch outline)

```cpp
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>

#define SERVICE_UUID        "12345678-1234-5678-1234-56789abcdef0"
#define CHARACTERISTIC_UUID "12345678-1234-5678-1234-56789abcdef1"

BLECharacteristic *pCharacteristic;

void setup() {
  BLEDevice::init("CropDoc Sensor");
  BLEServer *pServer = BLEDevice::createServer();
  BLEService *pService = pServer->createService(SERVICE_UUID);

  pCharacteristic = pService->createCharacteristic(
    CHARACTERISTIC_UUID,
    BLECharacteristic::PROPERTY_NOTIFY
  );
  pCharacteristic->addDescriptor(new BLE2902());

  pService->start();
  BLEAdvertising *pAdv = BLEDevice::getAdvertising();
  pAdv->addServiceUUID(SERVICE_UUID);
  pAdv->start();
}

void loop() {
  float payload[6] = {
    readMoisture(),     // 0–100 %
    readPh(),           // 0–14
    readNitrogen(),     // mg/kg
    readPhosphorus(),   // mg/kg
    readPotassium(),    // mg/kg
    readTemperature()   // °C
  };
  pCharacteristic->setValue((uint8_t*)payload, 24);
  pCharacteristic->notify();
  delay(2000);
}
```

---

## Permissions

| Permission | Reason |
|---|---|
| `CAMERA` | Photograph crops |
| `BLUETOOTH_SCAN` (API 31+) | Discover nearby BLE sensors |
| `BLUETOOTH_CONNECT` (API 31+) | Connect to sensor |
| `BLUETOOTH` + `ACCESS_FINE_LOCATION` (API < 31) | BLE on older Android |
| `WRITE_EXTERNAL_STORAGE` (API ≤ 28) | Save captured photos |

---

## Production Considerations

- **Model delivery:** Use [Play Asset Delivery](https://developer.android.com/guide/playcore/asset-delivery) (fast-follow pack) to ship the 2.5 GB model file separately from the APK
- **Fine-tuning:** Use [Unsloth](https://github.com/unslothai/unsloth) to fine-tune Gemma 4 E4B on a crop disease dataset (e.g. PlantVillage) for improved accuracy
- **Structured output:** Prompt Gemma 4 to respond in JSON for more reliable rendering
- **SMS registration:** Register for A2P 10DLC through Twilio to enable unrestricted outbound SMS at scale
- **Battery:** Throttle analysis frequency; BLE sensor notifications at 2-second intervals are already efficient
- **Localisation:** Add translations via `res/values-<locale>/strings.xml` — Shona (`sn`) and Amharic (`am`) are already included

---

## Licence

Apache 2.0 — see `LICENSE`.

Gemma 4 model weights are subject to [Google's Gemma Terms of Use](https://ai.google.dev/gemma/terms).

---

*Built for the [Gemma 4 Good Hackathon](https://www.kaggle.com/competitions/gemma-4-good-hackathon) — Kaggle × Google DeepMind, 2026*
