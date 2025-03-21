# Kalaha AI Android Game

## Introduction

This project is an Android implementation of the classic board game Kalaha, featuring an AI opponent powered by the Minimax algorithm with Alpha-Beta pruning.  You can play Kalaha against a computer AI opponent on your Android device!

## Requirements

To build and run this application from source, you will need:

*   **Android Studio:**  The official Integrated Development Environment (IDE) for Android development. You can download it for free from [https://developer.android.com/studio](https://developer.android.com/studio).
*   **Android Emulator or Physical Android Device:** To run the application, you will need either:
    *   **Android Emulator:**  Configured within Android Studio.
    *   **Physical Android Device:**  Connected to your computer with USB debugging enabled.

## Building and Running from Source (Using Android Studio)

1.  **Clone the Project:** Download or clone this project repository to your local machine.
2.  **Open in Android Studio:**
    *   Start Android Studio.
    *   Select "Open an Existing Project" and navigate to the directory where you cloned the project.
    *   Select the project folder and click "Open".
3.  **Gradle Sync:** Android Studio will automatically start Gradle syncing. Wait for Gradle to finish building and syncing the project. This might take a few minutes, especially the first time you open the project.
4.  **Build the Application:**
    *   In Android Studio, go to "Build" in the menu bar.
    *   Select "Make Project" or "Rebuild Project".
5.  **Run on Emulator or Device:**
    *   **Emulator:** If you have a configured Android Emulator, select it from the device dropdown menu in the Android Studio toolbar (usually next to the "Run" button).
    *   **Physical Device:**  Connect your Android device to your computer via USB cable. Make sure USB debugging is enabled on your device (usually found in Developer Options in your device's Settings). Android Studio should detect your connected device, and it will appear in the device dropdown menu.
    *   Click the "Run" button (green play icon in the toolbar) to build and run the application on your selected emulator or device.

## Convenience Option: Download and Install APK (Direct Installation)

If you do not wish to set up Android Studio and build from source, you can directly download and install a pre-built APK (Android Package Kit) file of the application.

**[Download APK File Here](https://appdistribution.firebase.dev/i/a0fa562e61ea8ab8)**

[//]: # (**&#40;Replace `YOUR_APK_DOWNLOAD_LINK_HERE` with the actual download link to your APK file. You can use services like Google Drive, Dropbox, or GitHub Releases to host your APK file.&#41;**)

**Installation Instructions (APK):**

1.  **Download the APK file:** Download the `kalaha-ai-game.apk` file (or whatever you named your APK file) to your computer.
2.  **Transfer APK to Android Device:** Transfer the downloaded APK file to your Android device. You can do this via USB cable, cloud storage, or email.
3.  **Enable "Install Unknown Apps":** On your Android device, you may need to enable "Install unknown apps" from your browser or file manager app (depending on your Android version and security settings). This is usually found in Settings > Apps > Special app access > Install unknown apps, or similar.
4.  **Install the APK:** Use a file manager app on your Android device to locate the downloaded APK file. Tap on the APK file to begin the installation process. Follow the on-screen prompts to install the application.
5.  **Run the Kalaha AI Game:** Once installed, you should find the "Kalaha Game" app icon in your app drawer. Tap on the icon to launch and play the game!

**Note:** Installing APKs from unknown sources carries a potential security risk. Ensure you trust the source of the APK file before installing it. This APK is provided for convenience and is built from the source code in this repository.

## Game Features

*   Play Kalaha against a computer AI opponent.
*   AI opponent uses the Minimax algorithm with Alpha-Beta pruning for strategic gameplay.
*   Adjustable AI search depth (in the code - for future enhancement).
*   Simple and intuitive graphical user interface.
*   Randomized first player for fairer gameplay.

## AI Algorithm Details

The AI opponent in this game utilizes:

*   **Minimax Algorithm:** A classic decision-making algorithm for two-player games, exploring possible game states to choose the optimal move.
*   **Alpha-Beta Pruning:** An optimization technique to speed up the Minimax search by pruning irrelevant branches of the game tree.
*   **Heuristic Evaluation Function:** A custom-designed heuristic function (`evaluateBoard` in `KalahaAI.kt`) that estimates the value of a Kalaha board state, considering factors such as:
    *   AI player's Kalaha score
    *   Opponent player's Kalaha score
    *   Capture potential
    *   Free turn potential
    *   End-game score potential
    *   Opponent's chain potential penalty

## Further Development

This project can be further enhanced by:

*   **Improving the Heuristic Function:**  Refining the `evaluateBoard` function to consider more advanced Kalaha strategies and game phases.
*   **Tuning Heuristic Weights:** Experimenting with different weights in the heuristic function to adjust the AI's playing style and strength.
*   **Adjustable AI Search Depth:** Adding a user interface setting to allow players to change the AI's search depth and difficulty level.
*   **Improved UI/Graphics:** Enhancing the user interface with more visually appealing graphics, animations, and user feedback.
*   **Multiplayer Mode:**  Adding a two-player mode to play against another human player (local or online).

---

Enjoy playing Kalaha against the AI!

[//]: # (**&#40;Remember to replace `YOUR_APK_DOWNLOAD_LINK_HERE` with your actual APK download link and specify your project license.&#41;**)