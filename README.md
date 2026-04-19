# 🌱 CropDoc

**On-device crop disease diagnosis and soil health analysis for farmers.**

Powered by **Gemma 4 E4B** via **LiteRT-LM** — everything runs privately on the farmer's Android phone. No internet connection required. No data ever leaves the device.

---

## Features

- 📸 **Camera-based crop disease detection** — photograph a crop and get an instant AI diagnosis
- 🌡️ **Soil sensor integration** — Bluetooth LE connection to an ESP32-based NPK/pH/moisture sensor
- 🤖 **Combined analysis** — Gemma 4 reasons over both the image AND soil data together
- 📊 **Health score** — overall crop health 0–100 with colour-coded severity
- 📋 **Analysis history** — all past diagnoses stored locally on-device
- 🔒 **100% offline** — no cloud, no API keys, no connectivity required

---

## Tech Stack

| Layer | Technology |
|---|---|
| On-device AI | Gemma 4 E4B via LiteRT-LM (Kotlin API) |
| Vision | LiteRT-LM `visionBackend = Backend.GPU()` |
| Camera | CameraX |
| Bluetooth | Android BLE API |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM (ViewModel + StateFlow) |
| Min SDK | Android 8.0 (API 26) |

---

## Project Structure

```
app/src/main/java/com/cropdoc/app/
├── MainActivity.kt                   # Compose nav host + permissions
├── data/
│   ├── model/
│   │   ├── Models.kt                 # SoilReading, AnalysisResult, BleState…
│   │   └── CropDocAiEngine.kt        # LiteRT-LM engine wrapper
│   └── ble/
│       └── SoilSensorBleManager.kt   # BLE scan / connect / parse
├── viewmodel/
│   └── CropDocViewModel.kt           # Coordinates AI + BLE + camera
└── ui/
    ├── theme/                        # Green & white Material 3 theme
    ├── components/                   # Shared Compose components
    └── screens/
        ├── HomeScreen.kt             # Dashboard
        ├── CameraScreen.kt           # Camera capture + streaming analysis
        ├── SensorScreen.kt           # BLE sensor management
        └── HistoryScreen.kt          # Past analyses
```

---

## Setup

### 1. Clone the repo

```bash
git clone https://github.com/your-org/cropdoc.git
cd cropdoc
```

### 2. Download the Gemma 4 E4B model

The model file (~2.5 GB) must be downloaded separately and placed in:

```
app/src/main/assets/gemma-4-E4B-it.litertlm
```

**Download from HuggingFace:**

```bash
# Install huggingface-cli
pip install huggingface_hub

# Download (accept Gemma licence at huggingface.co/google/gemma-4-E4B first)
huggingface-cli download \
  google/gemma-4-E4B-it-litert-lm \
  gemma-4-E4B-it-int4.litertlm \
  --local-dir app/src/main/assets/

# Rename to match the expected filename
mv app/src/main/assets/gemma-4-E4B-it-int4.litertlm \
   app/src/main/assets/gemma-4-E4B-it.litertlm
```

> **Note:** The `assets/` folder is excluded from git via `.gitignore` due to file size.
> For production, consider using [Play Asset Delivery](https://developer.android.com/guide/playcore/asset-delivery)
> to deliver the model on first install rather than bundling it in the APK.

### 3. Build & run

```bash
./gradlew assembleDebug
# or open in Android Studio and press Run
```

Requires Android Studio Meerkat (2024.3) or later.

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
  // Read sensors
  float moisture    = readMoisture();     // 0–100 %
  float ph          = readPh();           // 0–14
  float nitrogen    = readNitrogen();     // mg/kg
  float phosphorus  = readPhosphorus();   // mg/kg
  float potassium   = readPotassium();    // mg/kg
  float temperature = readTemperature();  // °C

  // Pack as 24-byte little-endian float array
  float payload[6] = { moisture, ph, nitrogen, phosphorus, potassium, temperature };
  pCharacteristic->setValue((uint8_t*)payload, 24);
  pCharacteristic->notify();

  delay(2000); // Send reading every 2 seconds
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
- **Fine-tuning:** Use [Unsloth](https://github.com/unslothai/unsloth) to fine-tune Gemma 4 E4B on a crop disease dataset (e.g. PlantVillage) before deployment — this will significantly improve accuracy
- **Structured output:** For production, prompt Gemma 4 to respond in JSON and parse it for more reliable structured results
- **Battery:** Consider throttling analysis to avoid draining the battery; BLE sensor notifications at 2-second intervals are already efficient
- **Localisation:** Add translations via `res/values-<locale>/strings.xml` for local languages

---

## Licence

Apache 2.0 — see `LICENSE`.

Gemma 4 model weights are subject to [Google's Gemma Terms of Use](https://ai.google.dev/gemma/terms).
