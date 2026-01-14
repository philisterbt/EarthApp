# EarthApp


EarthApp is a decentralized, offline-first Android application designed to facilitate communication during disasters and emergencies without complying on cellular networks or internet connections.

The application creates a mesh network using Wi-Fi Direct and Bluetooth Low Energy (BLE) technologies, allowing users to transmit data through other devices. This ensures that basic communication and location sharing remain possible even when traditional infrastructure fails.

##  Features

*   **Decentralized Communication (Mesh Network):** Establishes a messaging network via nearby devices without needing the internet or GSM operators.
*   **Emergency Buttons:** One-touch status notifications for "Help Needed" or "I Am Safe".
*   **Location Sharing:** Automatically appends GPS coordinates to sent messages, allowing rescue teams or other users to see the location.
*   **Continuous Connectivity:** The `MeshService` foreground service ensures that network scanning and data transmission continue even when the app is in the background.
*   **Local Data Storage:** Message history and user information are securely stored on the device using Room Database.

##  Technologies Used

*   **Platform:** Android (Min SDK: 24, Target SDK: 36)
*   **Language:** Java
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Database:** Room Database
*   **Network Protocols:**
    *   Wi-Fi P2P (Wi-Fi Direct)
    *   Bluetooth Low Energy (BLE)
*   **Build System:** Gradle (Kotlin DSL)

##  Installation

1.  Clone or download this repository.
2.  Open the project in **Android Studio**.
3.  Wait for the Gradle synchronization to complete.
4.  Install the application on a physical Android device (Real devices are recommended over emulators to properly test Mesh features).
5.  Grant the requested **Location**, **Bluetooth**, and **Wi-Fi** permissions when the app launches.

> **Note:** To test the Mesh network, the application must be installed on at least two different devices, and they must be within range of each other.

##  Permissions

The following permissions are required for the application to function fully:

*   `ACCESS_FINE_LOCATION`: For device discovery and location sharing.
*   `NEARBY_WIFI_DEVICES`: To establish connections via Wi-Fi Direct (Android 13+).
*   `BLUETOOTH_SCAN`, `BLUETOOTH_ADVERTISE`, `BLUETOOTH_CONNECT`: To find and connect to devices via BLE.
*   `FOREGROUND_SERVICE`: To ensure the connection remains active in the background.



