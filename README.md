# Teleparty Android Challenge Submission

This repository contains the solution to the Teleparty Android challenge.

# Task 1: DRM Video Player with Quality Selector
- ExoPlayer integration for streaming DRM-protected video.
- User-selectable video quality via a `Spinner`.

# Task 2: Prime Video Metadata Fetcher
- Makes a secure API request to fetch metadata from Amazon Prime Video for a hardcoded `entityId`.
- Authenticated using a valid session token via `OkHttp`. 
- Parsed JSON metadata is shown on screen, including:
  - Title
  - Series Name
  - Season/Episode
  - Rating
    
# How to Run

1. Clone the project
2. Open in Android Studio (latest stable)
3. Run the app on any Android device or emulator with Internet access
4. You'll see two buttons:
   - **Task 1**: Launches ExoPlayer with resolution selector.
   - **Task 2**: Fetches metadata of a Prime Video trailer and displays it.

# Note on Authentication

- The Prime Video API requires a valid `session-token` in the cookie header.
- In this demo, a valid token is hardcoded to demonstrate functionality.
