# LeadShield — Google Play Store Submission Guide
**Date:** May 2, 2026  
**Build:** v1.1.0 (versionCode 2)  
**Status:** ✅ AAB SIGNED & READY — `app/build/outputs/bundle/freeRelease/app-free-release.aab` (35 MB)

---

## 1. PLAY CONSOLE SETUP (One-Time)

### A. Create Developer Account
- Go to: https://play.google.com/console
- Pay **$25 one-time registration fee**
- Use your Google account (keep this account forever — losing it = losing all apps)
- Developer name: **"Chaotically Organized AI"**

### B. Create the App
1. Click **"Create app"**
2. App name: **"LeadShield: AI Auto-Reply for Trades"**
3. Default language: **English (United States)**
4. App or Game: **App**
5. Free or Paid: **Free** (we use in-app subscriptions)
6. Click all checkboxes → **"Create app"**

---

## 2. STORE LISTING COPY (Copy-Paste Ready)

### App Name (30 chars max)
```
LeadShield: AI Reply for Trades
```

### Short Description (80 chars max)
```
AI auto-reply for missed calls. Never lose a contractor lead again.
```

### Full Description (4000 chars max — paste this exactly)
```
Stop losing $850–$4,500 jobs every time you miss a call on the job site.

LeadShield is the AI-powered auto-reply assistant built specifically for contractors, plumbers, electricians, HVAC techs, roofers, and tradespeople. When you're on a ladder, under a sink, or roofing a house — LeadShield answers for you.

━━━━━━━━━━━━━━━━━━━━━━
🔥 THE PROBLEM IT SOLVES
━━━━━━━━━━━━━━━━━━━━━━
Contractors miss 62% of first-time caller calls. That caller doesn't leave a voicemail — they call your competitor. One missed plumber call = $850. One missed roofing quote = $4,500. LeadShield fires an intelligent SMS the moment you miss a call so the lead stays yours.

━━━━━━━━━━━━━━━━━━━━━━
🤖 HOW IT WORKS
━━━━━━━━━━━━━━━━━━━━━━
1. Someone calls while you're working
2. LeadShield detects the missed call instantly
3. An intelligent SMS fires back within seconds
4. The AI holds the conversation if they reply
5. You see the lead scored (HOT / WARM / COLD) when you're free

━━━━━━━━━━━━━━━━━━━━━━
💰 MONEY CAPTURED TRACKER
━━━━━━━━━━━━━━━━━━━━━━
See the real dollar value of leads you've secured. LeadShield calculates your estimated revenue saved based on your trade type — so you always know what the app is worth to your business.

━━━━━━━━━━━━━━━━━━━━━━
📊 AI LEAD SCORING
━━━━━━━━━━━━━━━━━━━━━━
• 🔴 HOT — "emergency," "flooding," "no heat," "burst pipe" = call back NOW
• 🟡 WARM — "quote," "schedule," "how much" = follow up today  
• ⚪ COLD — general inquiry = follow up when you can

━━━━━━━━━━━━━━━━━━━━━━
⚡ ACTIVE HOURS + AFTER-HOURS MODE
━━━━━━━━━━━━━━━━━━━━━━
Set your working hours. After-hours calls get a different message — "We're closed but I got your message. I'll call first thing in the morning."

━━━━━━━━━━━━━━━━━━━━━━
📋 FEATURES BY TIER
━━━━━━━━━━━━━━━━━━━━━━

FREE
✓ Auto-reply to missed calls (up to 10/month)
✓ Custom SMS message
✓ Active hours scheduling
✓ Reply history

PRO — $7.99/mo
✓ Unlimited auto-replies
✓ AI lead scoring (Hot/Warm/Cold)
✓ After-hours split messages
✓ Contact-level overrides
✓ Money Captured tracker
✓ Full analytics + usage history

OPERATOR — $49/mo  
✓ Everything in Pro
✓ AI conversation mode (Gemini holds the chat for you)
✓ Smart contact routing (VIP / known / unknown lanes)
✓ Lead card pipeline with follow-up reminders
✓ Google Review auto-request after job completion
✓ Daily lead summary notifications
✓ Business profile (trade type, business name, hours)
✓ Cloud sync + CRM web dashboard

━━━━━━━━━━━━━━━━━━━━━━
🛡️ PRIVACY FIRST
━━━━━━━━━━━━━━━━━━━━━━
Free and Pro tiers: all data stays 100% on your device. No cloud sync. No data sold. SMS and Phone permissions are used only to detect missed calls and send your auto-replies — nothing else.

━━━━━━━━━━━━━━━━━━━━━━
PERMISSIONS EXPLAINED
━━━━━━━━━━━━━━━━━━━━━━
• Phone State — detect when you miss a call
• SMS — send your auto-reply message
• Contacts — look up caller names for your history
• Notifications — alert you when a HOT lead comes in

Questions? support@coaibakersfield.com
Privacy Policy: https://coaibakersfield.com/privacy
```

---

## 3. APP CATEGORY & TAGS

| Field | Value |
|---|---|
| **Category** | Business |
| **Tags** | Contractor, Auto Reply, SMS, Missed Call, Trades, Lead Management |
| **Content Rating** | Everyone (complete Play Console questionnaire) |
| **Target Age** | Adults (18+) |

---

## 4. REQUIRED GRAPHIC ASSETS

| Asset | Size | Notes |
|---|---|---|
| App icon | 512×512 PNG | Already in res/mipmap — export high-res |
| Feature graphic | 1024×500 PNG | Banner shown at top of listing |
| Phone screenshots | Min 2, max 8 | 1080×1920 or 1080×2340 PNG |
| 7-inch tablet (optional) | 1200×1920 | — |

### Screenshot Strategy (8 screens):
1. **Home Screen** — LeadShield active, Money Captured counter showing "$1,700–$8,500 secured"
2. **Missed Call Firing** — Visual of the AI reply going out
3. **Lead Cards** — HOT / WARM / COLD pipeline
4. **AI Conversation** — Gemini holding the back-and-forth
5. **After-Hours Mode** — Schedule screen showing work hours
6. **Money Captured Detail** — Revenue math by trade type
7. **Usage Analytics** — Replies sent, leads scored
8. **Subscription Screen** — Free → Pro → Operator tiers

**How to capture:** Build `masterDebug` APK, install on phone, screenshot each screen. Or use Android Studio's built-in screenshot tool with an emulator.

---

## 5. IN-APP SUBSCRIPTION PRODUCTS (Critical)

Go to: Play Console → Your App → **Monetize → Subscriptions**

Create these 4 products **exactly**:

### Product 1: Pro
| Field | Value |
|---|---|
| Product ID | `mctb_pro_monthly` |
| Name | LeadShield Pro |
| Description | Unlimited auto-replies, AI lead scoring, Money Captured tracker |
| Billing period | Monthly |
| Price | **$7.99** |
| Free trial | 7 days |
| Grace period | 3 days |

### Product 2: Operator
| Field | Value |
|---|---|
| Product ID | `mctb_operator_monthly` |
| Name | LeadShield Operator |
| Description | AI conversations, lead pipeline, Google Review automation, CRM sync |
| Billing period | Monthly |
| Price | **$49.00** |
| Free trial | 7 days |
| Grace period | 3 days |

### Product 3: Voice (Waitlist)
| Field | Value |
|---|---|
| Product ID | `mctb_voice_monthly` |
| Name | LeadShield Voice |
| Description | AI voice answering — coming soon. Join waitlist. |
| Billing period | Monthly |
| Price | **$99.00** |
| Free trial | none |
| Grace period | 3 days |

### Product 4: Team (Waitlist)
| Field | Value |
|---|---|
| Product ID | `mctb_team_monthly` |
| Name | LeadShield Team |
| Description | Multi-number, team dashboard — coming soon. Join waitlist. |
| Billing period | Monthly |
| Price | **$129.00** |
| Free trial | none |
| Grace period | 3 days |

> ⚠️ **IMPORTANT:** Product IDs must match exactly what's in `BillingManager.kt`. If you change them here, change them in the code too.

---

## 6. PRIVACY POLICY HOSTING

Your privacy policy is at: `PRIVACY_POLICY.md`

**You need to host it at a URL.** Options:

### Option A — GitHub Pages (Free, 5 min setup)
1. Create repo: `leadshield-privacy`
2. Add `index.html` with the policy content (or a rendered MD file)
3. Enable GitHub Pages
4. URL: `https://[yourusername].github.io/leadshield-privacy`

### Option B — Vercel (Already have account)
1. Create a simple Next.js page at `/privacy`
2. Deploy to `leadshield-crm.vercel.app/privacy`
3. Already live at: https://leadshield-crm.vercel.app

### Option C — Your website
Host at: `https://coaibakersfield.com/privacy`  
This is already referenced in the privacy policy document.

**In Play Console:** Store listing → App content → Privacy Policy → paste URL

---

## 7. DATA SAFETY FORM

Play Console → App content → Data safety

### Data collected:
| Data type | Collected | Shared | Optional | Purpose |
|---|---|---|---|---|
| Phone number | Yes | No | No | Core functionality |
| Contact names | Yes | No | Yes | Display caller names |
| SMS messages (outgoing only) | Yes | No | No | Send auto-replies |
| Call logs | No (read momentarily) | No | No | Detect missed calls |
| In-app messages/texts | Yes (local) | No | No | Conversation history |

### Data encrypted in transit: Yes  
### Users can request deletion: Yes (email privacy@coaibakersfield.com)

---

## 8. CONTENT RATING QUESTIONNAIRE

Expected rating: **Everyone** (ESRB: E / PEGI: 3)

Answers:
- Violence: No
- Sexual content: No
- Profanity: No
- Controlled substances: No
- User-generated content: No (users only see their own data)
- Location sharing: No
- Financial transactions: Yes (subscription billing via Google Play)

---

## 9. DECEPTIVE BEHAVIOR / PERMISSIONS DECLARATION

Because LeadShield uses **SEND_SMS** and **READ_PHONE_STATE**, Google will ask you to declare intended use. Required statement:

> "LeadShield uses SMS permission exclusively to send auto-reply text messages to phone numbers that called the device user when the call was not answered. The app monitors phone state solely to detect missed calls. No SMS messages are sent without the user configuring and enabling the service. No call data is transmitted off the device."

You will also need to submit a **Permissions Declaration Form** for:
- `READ_CALL_LOG` — Required for missed call detection
- `READ_PHONE_STATE` — Required for missed call detection  
- `SEND_SMS` — Core app function

Play Console will prompt you to fill these in at: **App content → Sensitive app permissions**

---

## 10. RELEASE TRACKS (Recommended Order)

### Step 1: Internal Testing (Day 1)
- Upload `app-free-release.aab`
- Add up to 100 tester email addresses
- Test billing with Google Play test cards
- Test: reply fires on missed call, AI conversation, subscription upgrade

### Step 2: Closed Testing (Week 1–2)
- Invite 20–50 real contractors to test
- Collect feedback on reply timing, AI quality, UI clarity
- Fix any show-stopping bugs

### Step 3: Open Testing (Week 2–3)
- Open to anyone who finds the listing
- Final pre-production bug catching

### Step 4: Production (Week 3–4)
- Submit for Google review (~3–7 days with SMS/Phone permissions)
- Add a rollout percentage: start at 10%, then 50%, then 100%

---

## 11. UPLOAD CHECKLIST

Before submitting to production, verify:

- [ ] `app-free-release.aab` signed with `mctb-release-key.jks` ✅ (already done)
- [ ] versionCode = 2, versionName = "1.1.0" ✅
- [ ] Package name: `com.mctb.autoreply` ✅
- [ ] 4 in-app subscription products created in Play Console
- [ ] Privacy policy hosted and URL entered in Play Console
- [ ] Feature graphic (1024×500) uploaded
- [ ] At least 2 phone screenshots uploaded
- [ ] Short description filled (80 chars)
- [ ] Full description filled (4000 chars)
- [ ] App category: Business
- [ ] Data safety form completed
- [ ] Content rating questionnaire completed
- [ ] Permissions declaration submitted (SMS, Phone State, Call Log)
- [ ] Target SDK 35 ✅
- [ ] Supports API 26+ ✅

---

## 12. EXPECTED REVIEW TIMELINE

| Milestone | Timeline |
|---|---|
| Internal testing live | Same day as upload |
| Closed/open testing live | Same day as upload |
| Production — initial review | 3–7 business days |
| Production — if flagged for SMS/Phone permissions | Up to 14 days |
| Expedited review (if appealing) | 3 business days |

> 💡 **Tip:** SMS + Phone State permissions often trigger manual review. Write a clear description in the permissions declaration. Google will check that your app actually uses these for their stated purpose (missed call detection) and nothing else.

---

## 13. COMMON REJECTION REASONS (And How We Avoid Them)

| Risk | Our Mitigation |
|---|---|
| SMS permission misuse | Clearly declared: replies only to missed callers, only when user enables |
| Missing privacy policy | Hosted at coaibakersfield.com/privacy |
| TCPA / spam concerns | Users control the message, can disable anytime, opt-out in reply history |
| In-app purchase not working | Test billing in internal track before production |
| Crash on launch | Already built and tested, lint passes |

---

## 14. POST-LAUNCH (Week 1–4)

1. **Monitor ANR/crash reports** in Play Console → Android Vitals
2. **Reply to first reviews** within 24 hours (shows engagement)
3. **Track Day-1/Day-7/Day-30 retention** in Play Console → Statistics
4. **Update app description** with "Now available on Google Play!" social proof
5. **Push first update** (v1.1.1) within 2 weeks addressing any review feedback

---

## FILES READY TO UPLOAD

```
✅ app/build/outputs/bundle/freeRelease/app-free-release.aab  (35 MB)
✅ app/build/outputs/apk/free/release/app-free-release.apk    (23 MB — for direct install testing)
✅ mctb-release-key.jks                                        (KEEP THIS SAFE FOREVER)
✅ keystore.properties                                          (DO NOT COMMIT TO GIT)
✅ PRIVACY_POLICY.md                                           (needs hosting)
```

---

*LeadShield — Built by Chaotically Organized AI | support@coaibakersfield.com*
