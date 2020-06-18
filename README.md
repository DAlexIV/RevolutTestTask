# RevolutTestTask
Test task for Android Developer job at Revolut

## Task
You should implement one screen with a list of currencies. Link to designs in figma: 
https://www.figma.com/file/cUsxw4zNAvU47ADDCJemBM/Rates
 
The app must download and update rates every 1 second using API 
https://hiring.revolut.codes/api/android/latest?base=EUR 
 
List all currencies you get from the endpoint (one per row). Each row has an input where 
you can enter any amount of money. When you tap on a currency row it should slide to 
the top and it's input becomes the first responder. When you’re changing the amount 
the app must simultaneously update the corresponding value for other currencies. 
 
Use any libraries and languages(java/kotlin) you want. Please, note that the solution 
should be ​production ready. 
 
Video demo: ​https://youtu.be/omcS-6LeKoo  
 
Unfortunately, we do not provide the full list of full currency names and icons, but you 
are welcome to use flags from the Internet or emojis instead. 
 
Please​ upload y​ our​ app ​to ​github/bitbucket/etc. an​d make sure to provide public access 
after you share the link with us for review. 

## Design document
Features:
1. View latest currency rates
2. Change the currency number to convert one amount to all the currencies

## Implementation
#### Important note - I'm designing an application as a standalone app. If we were designing this screen as a feature to Revolut's main application then the design choices probably be little different.

1. View latest currency rates
  * Fetch JSON object with rates using existing REST API periodically
  * Since we expect this single request to be fast we don't need to sync rates while app is in the background.
  * We are already given an API, but if we were designing production application we probably need to use sockets instead of polling the REST API. While being memory heavy, sockets let us to accurately configure update frequency from server to handle spikes in app usage decreasing update frequency. 
  * Since we have just one network request we could even use bare HTTP without dependencies. To save development time I'll use Retrofit and Moshi to implement RatesRepository that will provide a rates subscription to UI. So overall application architecture will be like Clean Architecture, but without Interactor/Usecase layer, since we are not having complex processing logic in the app.
  * For the rates subscription I'll use LiveData. I think using RxJava and Flow will be overkill in that simple application, cause we don't really need heavy processing or scheduling logic. For the background work I also try to use Retrofit features, because referencing there some threading library will be too much for this app.
  * For view architecture I'll pick MVVM with single state (it will be little similar to MVI). I think here we don't need to go with full-fledged MVI with reducers and actions, since we have just two features for our screen. I prefer MVVM to MVP for it's stricter View-Presenter contract, which is a single object (state) and actions in MVVM.
  * To display rates I'll use simple RecyclerView because it has easy to set up animations and transitions. No paging needed.
    
2. Change the currency number to convert one amount to all the currencies
  * For this feature I'll store the currency of changed rate and it's number in the ViewModel field and update and rearrange rates accordingly. I think it doesn't worth to extract this logic to the Interactor or Repository, since it's not complex and I don't think that we should cache that data.
  * Talking about cache I think it's worth to save rates in the Repository to enable some minimal offline mode for the application.

We probably could also create separate gradle modules for data, usecase and presentation layers for this feature
to achieve better build time and code separation, but I'll skip this to save time during development
    
    
## Other things that we definitely need if this app will be in production:
1. Analytics
2. Crash reporting
3. More smart logic for querying the API. We probably want to implement some kind of logarithmic pollingif there is no network

## Things that I clarify with Product Owner / Designer
1. Color of the EditText underline. It's hard to customize that, so could we not do that?
2. We probably want to change the maximum fraction digits in the case, where I convert 1 IDR to other currencies to get values more accurate than 0 or 0.001
3. Ask whether we need an offline mode and latest timestamp. I think yes, so I've implemented them.
4. Ask for the loader design.