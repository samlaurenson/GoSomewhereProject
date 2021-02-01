# GoSomewhere

This is an app I wrote as part of my Mobile Applications Development module during my 2nd year at university.

The aim of the app is to help those who want to out somewhere but are too indecisive as to where they want to go. The app solves their problem by picking where they will go for them.

App language can be changed by changing phone language. Available languages include:
  - English
  - Spanish

In order to run, the minimum Android OS version should be Android 4.1.

**API key must be created and replaced with the "YOUR_API_KEY_HERE" bits in SearchLocations.java and google_maps_api.xml** \
API's that need to be enabled include:
  - Maps SDK for Android
  - Places API

# On App Start-Up
![App Startup](https://i.gyazo.com/e188868c9b1c3b73ac40d1f8028e57cd.png)
![App Startup 2](https://i.gyazo.com/c2481d70bb1c160bb09585f26f31b29c.png)

User will be presented with a splash screen when starting the app. Afterwards, they will be taken to the map page and the user can navigate around the app using the 'hamburger' icon in the top left.

# Filter Search Fragment
![Search Locations](https://i.gyazo.com/066bdf666c559ab085eed6ec39996220.png)

User will have the option to specify how far they are willing to travel and what types of locations they will be willing to visit. Important to note that if a forest was output as a place to go and a section of it is within the search radius, then there is a chance the marker will appear outside the circle.

# Locations Output
![Results](https://i.gyazo.com/05bb7ad5efcd8ac8cd55a3bfe10db129.png)

Once user presses search then, after a short while, colour coordinated markers will be placed on the map representing each of the searchable types of places.

# Zoom on to a place
![Zoom](https://i.gyazo.com/00a99f705cc8953c51e4501734e2ccfc.png)

The user can select any marker and it will show the name of the marker.


Future developments could see an option for the user to show reviews of selected place when focus is on a marker.
