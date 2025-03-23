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
    *   Select **"Open an Existing Project"** and navigate to the directory where you cloned the project.
    *   Select the project folder and click **"Open"**.
3.  **Gradle Sync:** Android Studio will automatically start Gradle syncing. Wait for Gradle to finish building and syncing the project. This might take a few minutes, especially the first time you open the project.
4.  **Build the Application:**
    *   In Android Studio, go to **"Build"** in the menu bar.
    *   Select **"Make Project"** or **"Rebuild Project"**.
5.  **Run on Emulator or Device:**
    *   **Emulator:** If you have a configured Android Emulator, select it from the device dropdown menu in the Android Studio toolbar (usually next to the "Run" button).
    *   **Physical Device:**  Connect your Android device to your computer via USB cable. Make sure USB debugging is enabled on your device (usually found in **Developer Options** in your device's Settings). Android Studio should detect your connected device, and it will appear in the device dropdown menu.
    *   Click the **"Run"** button (green play icon in the toolbar) to build and run the application on your selected emulator or device.

## Convenience Option: Install via Firebase App Distribution (Easiest Way!)

Want to play Kalaha right away without using Android Studio? Follow these super simple steps to install the app directly on your Android device using Firebase App Distribution:

1.  **Click the Invitation Link:**  Find the invitation link for the Kalaha app. **[Tap on this link](https://appdistribution.firebase.dev/i/a0fa562e61ea8ab8)** on your computer or directly on your Android device.
2.  **Check Your Email Inbox:**  After clicking the link, you will receive an email from Firebase App Distribution in your email inbox.
3.  **Open Email on Your Android Phone:** **Important:** Open this email **using the email app on your Android phone** (not on your computer).  The email contains the link that will guide the installation on your device.
4.  **Follow Email Instructions:**  Inside the email, you'll see clear instructions on how to download and install the "Kalaha King" app.  **Just follow the steps described in the email.**  It will likely involve tapping a button to download and then tapping to install.
5.  **Start Playing!** Once the installation is complete, you'll find the "Kalaha King" app on your phone or app list. **Tap the app icon to launch the game and start playing Kalaha against the AI!**

**Note:** Firebase App Distribution is a safe and convenient method for installing test versions of Android apps. You can be assured that the app is coming from a trusted source via Firebase.

## Game Features

*   Play Kalaha against a computer AI opponent.
*   AI opponent uses the **Minimax algorithm with Alpha-Beta pruning** for strategic gameplay.
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
