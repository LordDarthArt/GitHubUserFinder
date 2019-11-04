# Github User Finder

GitHubFinderTestApp is an Android OS mobile application that uses GitHub open API to search for users and display some of their public information.

## Architecture

GitHubFinderTestApp was made with using MVVM Architectural Pattern.

## Stack

GitHubFinderTestApp was written on Kotlin, made with using this libs: Android Jetpack, Android Architecture Components, DataBinding, Coroutines, FirebaseAuth, FirebaseCrashlytics, Google Play Services Auth, Retrofit, GSON, OkHttp3 Logging interceptor for logging during Http request, Picasso.

## Known problems

```message API rate limit exceeded for XXX.XX.XXX.XX. (But here's the good news: Authenticated requests get a higher rate limit. Check out the documentation for more details.```

After sending some requests to server, there will be this error, then we're getting error 403, after few seconds pressing on "Try Again" button will fix the problem. The problem is unfixable.

```message Only the first 1000 search results are available```

We can't get more then first 1000 users on each request, after that we are getting 422 error, unfixable

## Tools for building

This project was developed using Android Studio 4.0 - preview1 and was tested on Android SDK 29 (Android 10)