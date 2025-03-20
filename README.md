# Wheel of Fortune Puzzle Solver (Android)

An Android application that helps solve Wheel of Fortune puzzles by analyzing photos of the puzzle board and suggesting solutions based on visible letters and puzzle categories.

## Features

- Take photos of Wheel of Fortune puzzle boards using device camera
- Upload existing photos from gallery
- Extract visible letters using ML Kit text recognition
- Solve puzzles based on extracted text and provided clue/category
- Suggest letters to guess next based on frequency analysis
- AdMob integration for monetization

## Technology Stack

- Kotlin for Android development
- CameraX for camera integration
- ML Kit for text recognition
- Custom puzzle solving algorithm
- Google AdMob integration

## Project Structure

- `app/src/main/java/com/djtaylor333/wheeloffortune/activities` - Activity classes
- `app/src/main/java/com/djtaylor333/wheeloffortune/utils` - Utility classes for image processing and puzzle solving
- `app/src/main/java/com/djtaylor333/wheeloffortune/models` - Data models
- `app/src/main/res/layout` - XML layout files

## Building the Project

1. Clone the repository
```
git clone https://github.com/djtaylor333/wheel-of-fortune-android.git
cd wheel-of-fortune-android
```

2. Open the project in Android Studio

3. Build and run the application on a device or emulator

## Usage

1. Launch the application
2. Enter the puzzle clue/category (optional)
3. Either:
   - Enter visible letters manually (use underscores for blanks)
   - Take a photo of the puzzle board
   - Upload an existing photo from gallery
4. View the suggested solution and recommended letters to guess

## AdMob Integration

The application includes AdMob integration with the publisher ID `pub-0555936907173117`. To use your own AdMob account:

1. Open `app/src/main/java/com/djtaylor333/wheeloffortune/utils/AdManager.kt`
2. Replace the publisher ID with your own
3. Update the ad unit IDs as needed
4. Update the AdMob App ID in `AndroidManifest.xml`

## Permissions

The application requires the following permissions:
- Camera
- Internet
- Read External Storage

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Google ML Kit for text recognition capabilities
- Android Jetpack libraries
- CameraX for camera integration
