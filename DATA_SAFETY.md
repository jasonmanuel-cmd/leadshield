# Google Play Data Safety Form Guide

Use the information below to fill out the **Data Safety** section in the Google Play Console. This is required for all apps that handle user data.

## 1. Data Collection and Security
- Does your app collect or share any of the required user data types? **Yes**
- Is all of the user data collected by your app encrypted in transit? **Yes** (Even if not transmitted, select Yes as modern Android handles this for system APIs).
- Do you provide a way for users to request that their data is deleted? **Yes** (Uninstalling the app deletes all local data).

## 2. Data Types Collected & Shared
- **Location:** No
- **Personal Info:** No (The app accesses phone numbers, but only for functional purposes and does not "collect" them in the sense of storing them on a server).
- **Financial Info:** No
- **Health and Fitness:** No
- **Messages:**
    - **SMS Messages:** **Yes** (Used only for auto-replying. Mention that this data is processed only locally on the device).
- **Photos and Videos:** No
- **Audio Files:** No
- **Files and Docs:** No
- **Calendar:** No
- **Contacts:** **Yes** (Only used to filter auto-replies to known contacts if the user enables that feature).
- **App Activity:** **Yes** (If using Firebase Analytics for performance monitoring).
- **Web Browsing:** No
- **App Info and Performance:** **Yes** (Crashlytics for stability monitoring).
- **Device or Other IDs:** **Yes** (Android ID/Advertising ID used for analytics).

## 3. Data Usage & Handling (For each type selected above)
For **SMS Messages** and **Contacts**:
- **Collected:** Yes (Processed locally)
- **Shared:** No
- **Ephemeral:** No (Stored in local database for user history)
- **Required?** No (Optional user-enabled feature)
- **Why?** App Functionality

For **App Info and Performance** (Crashlytics):
- **Collected:** Yes
- **Shared:** Yes (Shared with Google/Firebase)
- **Ephemeral:** No
- **Required?** Yes (To ensure app stability)
- **Why?** Analytics, Developer Communications

## 4. Privacy Policy URL
You must provide a URL to your hosted privacy policy (e.g., the content in `PRIVACY_POLICY.md` hosted on GitHub Pages, Google Sites, or your personal website).
**URL Example:** `https://chaoticallyorganizedai.com/privacy-policy` (Ensure this URL matches what is in `SettingsScreen.kt`).
