# In-app alerts + Interests badge — patch

Copy each file below into your `companion-android` repo at the **same path**
(paths are relative to the repo root, matching what's in this folder), then
build. No `AndroidManifest.xml` or `build.gradle.kts` changes are needed —
everything here uses libraries already in the project (Material Components,
view binding, Agora Chat SDK types already referenced elsewhere).

## New files (create)
- `app/src/main/java/com/companion/astrodating/base/InAppEventBus.kt`
- `app/src/main/java/com/companion/astrodating/base/InAppAlertManager.kt`
- `app/src/main/res/layout/view_in_app_alert.xml`
- `app/src/main/res/drawable/bg_circle_primary.xml`

## Modified files (overwrite existing)
- `app/src/main/java/com/companion/astrodating/base/NotificationMessagingService.kt`
- `app/src/main/java/com/companion/astrodating/ui/home/ui/HomePageActivity.kt`
- `app/src/main/java/com/companion/astrodating/ui/home/ui/HomeFragment.kt`
- `app/src/main/java/com/companion/astrodating/ui/interests/ui/InterestsFragment.kt`
- `app/src/main/res/layout/activity_home_page.xml`

## What this does
1. **In-app pop-up card, queued** — `InAppAlertManager` shows a card at the
   top of the screen (icon circle, bold title + "Now" timestamp, subtitle,
   chevron arrow). It has **no auto-dismiss timer** — it stays until tapped.
   If a new message/interest arrives while a card is already showing,
   `InAppEventBus` queues it (FIFO) instead of dropping it or replacing the
   card - the next one shows automatically once the current one is tapped.
2. **Tapping a message alert opens that exact chat.** `HomeFragment`
   resolves the sender's nickname via Agora's `userInfoManager()` (the same
   lookup `MessageFragment` uses) and carries the conversation id on the
   event. Tapping the card runs the same "check chat credit → launch
   ChatActivity" flow as `MessageFragment` (via `MessageViewModel`,
   including the 403/402 free-chat/paywall handling), then opens the
   specific sender's `ChatActivity` — not just the Message tab.
3. **Tapping an interest alert opens the actual received/declined list** —
   navigates straight to `interestUserFragment` filtered by type (the same
   destination the old cold-start notification-tap path already used), not
   just the Interests tab's category screen.
4. **Interests received/declined** — `NotificationMessagingService` now
   posts to `InAppEventBus` (in addition to the existing system-tray
   notification) whenever a `received-interest` / `decline-interest` FCM
   push arrives, and increments the Interests badge.
5. **Interests badge** — live badge on the Interests bottom-nav tab, cleared
   automatically when the user opens that tab (`InterestsFragment.onResume()`).
6. **Live message badge + pop-up** — `HomeFragment` polls Agora unread
   counts every 5 seconds while the Home tab is visible, so the badge and
   pop-up update without needing to leave and re-enter the tab.

## Known scope limits (be aware)
- The live "new message" pop-up currently only refreshes while the **Home**
  tab is on screen (it's driven by `HomeFragment`'s poll, matching where the
  existing badge logic already lived). If you want it live across all tabs,
  the unread-count polling (or, better, a real Agora `MessageListener`) needs
  to move up to `HomePageActivity` or the `Application` class instead — I
  didn't attempt that here because I can't verify the exact
  `io.agora.chat.MessageListener` interface signature from the code alone,
  and guessing it wrong would break the build. Verify the interface against
  the installed Agora Chat SDK version before wiring a live listener.
- The polling approach (vs. a push-based listener) means there can be up to
  a ~5s delay before a new message pop-up appears.

## Not included: the online-status bug
Per the diagnosis, `OnlinePinger.kt` is fully commented out/unused, so
"online" status now depends entirely on whether `loginForChatToUpdateUserInfo()`
in `HomeFragment` succeeds for a given account. Recommend checking your
backend/Agora console for chat-login failures tied to the affected account
(`8149505165`), and adding real handling in `onError` there (currently just
logs and does nothing).
