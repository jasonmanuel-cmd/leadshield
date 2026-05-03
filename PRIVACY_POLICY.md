# LeadShield Privacy Policy

**Effective Date:** April 30, 2026
**Last Updated:** April 30, 2026
**App Name:** LeadShield
**Developer:** Chaotically Organized AI
**Website:** https://coaibakersfield.com
**Contact:** privacy@coaibakersfield.com

---

## 1. Overview

LeadShield is an Android application that automatically sends a customizable SMS reply when you miss a phone call. This Privacy Policy explains exactly what data the App accesses, how it is used, where it is stored, and your rights over it.

We collect the minimum data required to make the App work. We do not sell your data. We do not share your data with advertisers. We do not use your data for any purpose beyond providing the features described in this policy.

---

## 2. Data We Access and Why

### 2.1 Phone State (READ_PHONE_STATE)

**What it is:** The ability to detect when your phone is ringing, being answered, or returning to idle.

**Why we need it:** This is the core function of the App. Without monitoring call state, the App cannot detect missed calls.

**What we do with it:** We track call state transitions in real time, entirely on your device. This data is never transmitted off your device.

---

### 2.2 Call Log (READ_CALL_LOG)

**What it is:** The ability to read your phone's call history.

**Why we need it:** To confirm that an incoming call was genuinely missed (versus answered) before sending an auto-reply. This prevents false triggers.

**What we do with it:** We check individual call events momentarily to determine outcome. We do not copy, store, or upload your full call log. We maintain our own separate reply log (see Section 2.6) that records only calls where an auto-reply was sent.

---

### 2.3 Send SMS (SEND_SMS)

**What it is:** The ability to send text messages from your phone number.

**Why we need it:** This is the App's primary function — sending your customized auto-reply message to callers you missed.

**What we do with it:** We send exactly one SMS per missed call, to the number that called you, containing the message you configured. We never send messages to anyone other than your missed callers, and only during hours you set.

**Your control:** You can disable auto-reply at any time with the main toggle. You can configure active hours so replies only go out during your work day. You can pause the service for one hour with a single tap.

---

### 2.4 Contacts (READ_CONTACTS)

**What it is:** The ability to look up a caller's name from your device contacts.

**Why we need it:** To display caller names in your reply history, and to support the optional "Reply to Contacts Only" Spam Shield feature.

**What we do with it:** We perform a single lookup per call to match a phone number to a name. We do not copy, upload, or store your contacts in bulk. Only the name associated with a specific call is saved to your local reply log.

**Your control:** Contacts access is optional. The "Reply to Contacts Only" filter is opt-in and off by default.

---

### 2.5 Notifications (POST_NOTIFICATIONS)

**What it is:** The ability to display a persistent notification while the monitoring service runs.

**Why we need it:** Android requires foreground services to show a notification. This lets you see that LeadShield is actively monitoring and gives you quick access to the App.

**What we do with it:** We show one low-priority persistent notification while the service is active. You can manage or silence this in your device's notification settings.

---

### 2.6 Reply History Log (Local Storage Only)

**What it contains:** Caller phone number, caller contact name (if found), message text sent, and timestamp. Maximum 20 entries. Stored only on your device.

**What we do with it:** Displayed in the History screen so you can track who received an auto-reply. Never transmitted off your device on Free, Pro, or Expert tiers.

**Your control:** You can clear your history at any time from the History screen.

---

### 2.7 Analytics Log (Local Storage)

**What it contains:** Call event results (SUCCESS / SKIPPED / FAILURE), reason for any skip, response delay, and your subscription tier at the time of the event.

**What we do with it:** Displayed in the Analytics screen to show your App's performance. On Free, Pro, and Expert tiers, this data never leaves your device.

**Master Tier only — Cloud Sync:** Master tier subscribers may optionally enable cloud synchronization of anonymized analytics to a secure database (Neon Postgres, hosted on AWS) for remote monitoring. This is opt-in, can be disabled at any time, and uses encrypted HTTPS transmission. Phone numbers in synced records are not included.

---

### 2.8 Subscription Status

Your subscription tier (Free, Pro, Expert, or Master) is stored locally on your device to enforce feature limits. All payment processing is handled by Google Play. We never see or store your payment card information.

---

## 3. Data We Do NOT Collect

- We do **not** record call audio or content
- We do **not** access your microphone
- We do **not** read incoming SMS messages
- We do **not** access your camera, location, photos, or files
- We do **not** track your activity across other apps
- We do **not** use advertising SDKs
- We do **not** sell or share your data with third parties for advertising
- We do **not** transmit your call log, contact list, or message history to any external server (except as described in Section 2.7 for optional Master tier cloud sync)

---

## 4. Data Storage and Security

**On-device:** All personal data is stored in Android DataStore and a local SQLite database protected by Android's application sandbox. No other app can access it.

**Cloud (Master tier sync only):** Data is transmitted over HTTPS (TLS 1.2 minimum) and stored in an encrypted database. Access is restricted to the developer.

**Backups:** App settings (your reply message, schedule) may be included in Android's standard Google account backup. Reply history and caller phone numbers are excluded from backup.

---

## 5. Data Retention

| Data | Where Stored | Retention |
|---|---|---|
| Reply history | Local device | Maximum 20 entries, auto-rotated |
| Analytics log | Local device | Until app uninstall or manual clear |
| App settings | Local device | Until app uninstall |
| Cloud sync data | Neon Postgres (Master only) | 90 days, then auto-purged |

---

## 6. Third-Party Services

**Google Play Billing:** All subscription payments are processed by Google. We receive only a purchase confirmation token. See: https://policies.google.com/privacy

**Google Gemini AI (Master Tier — Sovereign Diary feature only):** If you use the Sovereign Diary journaling feature on the Master tier, your journal entries are sent to Google's Gemini AI API. This feature is opt-in, available only on Master tier, and governed by Google's AI terms: https://ai.google.dev/terms. Do not enter sensitive personal or financial information in journal entries.

**WorkManager:** Used for reliable SMS scheduling. Operates entirely on-device. No data leaves your phone via this service.

---

## 7. Children's Privacy

LeadShield is a business productivity tool intended for users 18 years and older. We do not knowingly collect data from minors. If you believe a child under 18 has used the App, contact privacy@coaibakersfield.com and we will take immediate action.

---

## 8. Your Rights

You have the right to:

- **See your data:** Everything the App stores about you is visible within the App itself
- **Delete your data:** Clear history and analytics from within the App at any time
- **Opt out of sync:** Disable cloud sync (Master tier) in Settings at any time
- **Delete your account data:** Email privacy@coaibakersfield.com with "Data Deletion Request" — we will process within 30 days
- **Uninstall:** Removing the App deletes all locally stored data from your device

---

## 9. California Residents (CCPA)

California residents have the right to know what personal information is collected, whether it is sold (it is not), request deletion, and not be discriminated against for exercising privacy rights. To make any CCPA request, contact privacy@coaibakersfield.com. We do not sell personal information.

---

## 10. Changes to This Policy

We will update this policy when the App changes or law requires it. The "Last Updated" date at the top will reflect any changes. Continued use of the App after an update constitutes acceptance of the revised policy.

---

## 11. Contact

**Chaotically Organized AI**
https://coaibakersfield.com
privacy@coaibakersfield.com

We respond to all privacy inquiries within 5 business days.
