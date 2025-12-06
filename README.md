# Motorcycle Parking App

A school project for PROG2007 at NTNU. An Android app for finding and sharing motorcycle parking spots. Users can view nearby parking locations on an interactive map, search for spots, and save favorites.

## Features

- **Interactive Map** - View parking spots using Google Maps with geohash-based location queries
- **Search & History** - Find specific locations with autocomplete search
- **Favorites** - Save frequently used parking spots
- **Authentication** - Google Sign-In and email/password registration

## Running the Project

**Note:** The app requires Google Maps API credentials configured with SHA-1 keys registered in Google Cloud Console. Without proper credentials, the map will not load.

To run:
1. Open project in Android Studio
2. Set up your own Firebase project and place the `google-services.json` file in `/app` directory
3. Configure Google Maps API credentials with your SHA-1 key
4. Sync Gradle dependencies
5. Run on emulator or physical device (API 24+)

## Testing

Run unit tests:
```bash
./gradlew test
```
