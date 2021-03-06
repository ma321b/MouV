# MouV
My personal movie recommendation android app. 

## What it does:

The user can search for movies by logging in and then pressing the "Movie Search" button on the landing page. The app then matches the query string with a database of movies and shows the matching movies in the form of a [Card-based layout](https://developer.android.com/guide/topics/ui/layout/cardview), essentially showing the movie title, its IMDB rating and its icon. The user can also add a movie to their "favourites" by clicking a button displayed in the layout. The user can also see their favourite movies by pressing the "Show Favourites" button in the overflow menu of the toolbar. Favourite movies are fetched from the user's records on the Firebase Firestore Cloud database and then shown to the user in the form of a [Card-based layout](https://developer.android.com/guide/topics/ui/layout/cardview). 

The user can also view their recommended movies by clicking "View Recommendations" in the overflow menu toolbar. The recommendations are based on the user's favourite movies. To refresh the recommendations list, simply press the back button and click "View Recommendations" again.


NOTE: The app currently supports Google and Email sign-up/log-in methods. 
## How it works:

The app uses an external REST API to search for movies. User-specific information is stored in Firebase Firestore database and authentication is managed by Firebase Authentication for Android.

## How to run locally:

To run the app locally, please ensure you have Android Studio, Android SDK, ADB as well as the necessary drivers for an Android device that you will be connecting installed. 

When ready, follow the following steps:

1. Clone the repo 
2. Open Android Studio
3. Go to File -> Open and select the repo's location on your system
4. Allow Gradle sync to finish
5. Connect your Android device and wait for Android Studio to recognise it (see it in the top toolbar of Android Studio)
6. Click the Run app icon in Android Studio's toolbar
