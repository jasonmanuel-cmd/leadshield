# MCTB Subscription Implementation - TODO List

## Phase 1: Data Layer
- [ ] 1.1 Update AppPreferences.kt - Add subscription tier enum
- [ ] 1.2 Update AppPreferences.kt - Add weekly reset logic
- [ ] 1.3 Create SubscriptionStatus.kt - Data class
- [ ] 1.4 Create SubscriptionManager.kt - Subscription logic
- [ ] 1.5 Create TradeTemplate.kt - Trade template data
- [ ] 1.6 Create ContactMessage.kt - Contact messages

## Phase 2: Business Logic
- [ ] 2.1-2.3 Update SmsHandler.kt - Tier limits, contact lookup, templates

## Phase 3: Billing Integration
- [ ] 3.1 Add Play Billing Library dependency
- [ ] 3.2-3.4 Create BillingManager.kt - Purchase flow

## Phase 4: UI Updates
- [ ] 4.1-4.8 Subscription screens, AdMob, navigation

## Phase 5: Google Play Config
- [ ] 5.1-5.3 Billing permission, privacy policy

## Phase 6: Testing
- [ ] 6.1-6.3 Build verification, compliance check
