# WeatherDemoApp : This application is developed as an assignment gievn by Intelegencia 

Some of the Android architecture and design patterns that are used in the Weather app development  

Architecture of this app is based on MVVM. ViewModel class contains LiveData and that data is observed in View class i.e. on WeatherHomeActivity class. The purpose of this architecture is that, if the data changes in background then it will be observed by the observer and will display the updated data on respective views.
WorkManager is used to schedule a periodic work request.

Retrofit is used to get data from api.openweathermap.org . GsonConverterfactory is used to convert the json response to model class object. 

Created singleton classes like CacheManager to store api response in shared preferences and Util class to provide basic functions which are required in application.

Created LoadingListener interface to show and hide loading. Lottie animation is used to show loading animation.

Implemented runtime permission for FINE_LOCATION and GPS enable setting to enable gps.


About Future Release Plan

First plan will be to get a design suggestion from the Product guy. It will help me to make it  more attractive and more user friendly.

I would like to add more features to it. Some of them are as below

Option to add more places in a list and whenever the user selects that location it will show its weather report (Location should be stored in cache manager for further use).

I would like to add more animations respective to its current weather description

More features like to get the next 7 days forecast report. Also if possible would like to add a per hour weather report for the day.

