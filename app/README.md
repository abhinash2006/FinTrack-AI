# 💰 FinTrack AI: Smart Financial Assistant

FinTrack AI is an industry-ready Android application that leverages Artificial Intelligence to simplify personal finance management. Built with **Jetpack Compose** and **Kotlin**, it provides a seamless, multimodal experience for tracking expenses.

## 🚀 Key Features

* **🤖 Multimodal AI Input**: Scan physical receipts using OCR or add expenses via Voice Commands.
* **📊 Smart Analytics**: Interactive Donut Charts and predictive spending logic to forecast end-of-month totals.
* **⚠️ Intelligent Budgeting**: Set monthly limits and receive visual warnings (UI turns Red) when you exceed them.
* **🔒 Secure Auth**: Integrated Google OAuth 2.0 for professional and secure user authentication.
* **📄 Data Export**: Generate and share professional CSV reports of all your transactions.

## 🛠️ Tech Stack

- **UI**: Jetpack Compose (Material 3)
- **Database**: Room Persistence Library
- **Architecture**: MVVM with Clean Architecture
- **Dependency Injection**: Dagger Hilt
- **AI/ML**: Google ML Kit & Android Speech-to-Text

## 📂 Project Structure

- `data/`: Room Database entities, DAOs, and Repositories.
- `di/`: Hilt Modules for dependency injection.
- `ui/`: Compose Screens, ViewModels, and custom UI components.

## ⚙️ Setup Instructions

1. **Clone the project**: `git clone https://github.com/your-username/fintrack-ai.git`
2. **Google Cloud Console**:
    - Add your SHA-1 fingerprint to the Android Client ID.
    - Copy the Web Client ID into `AuthRepository.kt`.
3. **Build**: Sync Gradle and run on an Android emulator or physical device.

---
*Developed as a professional portfolio project demonstrating AI integration in mobile apps.*