# Weather Forecast App

A simple Android application that displays a 3-day weather forecast for the user's current location.

---

## Project Setup Instructions

### Requirements

- Android Studio Hedgehog or newer
- JDK 17 or higher
- Minimum Android SDK: API 21 (Android 5.0)
- Google Play Services installed on device/emulator
- Internet connection for live weather data

### Steps to Run

1. Clone the repository
   git clone https://github.com/sumitpanchal187/WeatherApp

2. Open in Android Studio
   File -> Open -> Select the project folder -> Wait for Gradle sync

3. Add your API Key
   Open local.properties and add:
   WEATHER_API_KEY=d497f49e43966aa8f37958be26e44628


4. Run the app
   Connect a device or start an emulator, then click Run in Android Studio.

### Permissions

The app will ask for location permission on first launch.
If denied, the app will still show the last saved weather data from local storage.

---

## Architecture Explanation

This project follows MVVM (Model-View-ViewModel) architecture as recommended by Google.

Layers:

UI Layer (MainActivity, Adapters)
|
ViewModel (WeatherViewModel)
|
Repository (WeatherRepository)
|
-------+-------
| |
API Database
(Retrofit)    (Room)

UI Layer    : Shows weather data, handles user interactions like refresh and location
ViewModel   : Holds UI state, survives screen rotation, connects UI to Repository
Repository  : Decides whether to load data from API or local database
API Source  : Fetches live weather data from OpenWeatherMap using Retrofit
Database    : Stores fetched data in Room so the app works offline

Offline Support:
When the device has no internet, the app automatically displays the last successfully
fetched weather data from the local Room database.

---

## Libraries Used

Networking

- Retrofit 2.9.0              : API calls to OpenWeatherMap
- Gson Converter 2.9.0        : Parses JSON response into Kotlin data classes
- OkHttp Logging 4.12.0       : Logs API requests for debugging

Location

- Google Play Services 21.2.0 : Gets current GPS coordinates
- Android Geocoder (built-in) : Converts coordinates to city name

Local Storage

- Room 2.6.1                  : Local database to cache weather data for offline use

Architecture Components

- ViewModel 2.7.0             : Manages UI-related data across configuration changes
- LiveData 2.7.0              : Observes and updates UI reactively

UI

- Material Design 1.11.0      : UI components and automatic Dark/Light theme support
- SwipeRefreshLayout 1.1.0    : Pull to refresh gesture
- RecyclerView 1.3.2          : Displays 3-day forecast list

Other

- ViewBinding                 : Type-safe view access, replaces findViewById
- Kotlin Coroutines           : Background threading for API and database operations

---

## Features Implemented

- 3-day weather forecast (date, temperature, condition, icon)
- Current GPS location detection
- Runtime location permission handling
- Offline support with Room database
- Pull to refresh
- Dark mode / Light mode based on system settings
- Loading indicator
- Error handling for no internet and permission denied

---

## APK

APK file is attached with the submission (see apk/ folder or release section on GitHub).