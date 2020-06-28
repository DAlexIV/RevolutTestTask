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

## Solution
You can download apk from the releases page to try it out.
![Output sample](https://github.com/DAlexIV/RevolutTestTask/raw/master/demo.gif)

## Design document
Features:
1. View latest currency rates
2. Change the currency number to convert one amount to all the currencies

## Implementation design decisions
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

I've written blackbox robolectric tests to cover most application code with minimal efforts.
They are flaky since they query real network, so in the real world, we probably want to use
mock web server for these kind of tests. Also, it makes sense to have UI and unit tests here, but
I've skipped this part due to not complex UI or business logic.
    
    
## Other things that we definitely need if this app will be in production:
1. Analytics
2. Crash reporting
3. More smart logic for querying the API. We probably want to implement some kind of logarithmic polling if there is no network

## Things that I clarify with Product Manager / Designer
1. Color of the EditText underline. It's hard to customize that, so could we not do that?
2. We probably want to change the maximum fraction digits in the case when I convert 1 IDR to other currencies to get values more accurate than 0 or 0.001
3. Ask whether we need an offline mode and latest timestamp. I think yes, so I've implemented them.
4. Ask for the loader design.
5. Ask how we should handle errors

## Feedback and how I addressed that
> 1. Some SOLID principles are violated


I think here you are implying that these two principles are violated:


Single-responsibility principle: A class should only have a single responsibility, that is, only changes to one part
    of the software's specification should be able to affect the specification of the class.
    
    
Here you could argue that CurrencyRateRepoImpl has two responsibilities - merge the data from the
local and network data sources and add descriptions and icons for this data.
I think here we violating that principle but we gaining in the lower code complexity. We could
probably move some of this logic to the usecase, but I think it will make code just harder to read
without adding some pros.


Dependency inversion principle "One should depend upon abstractions, [not] concretions."


Here you could say that we are referencing RatesViewModel directly without a proxy interface which
contradicts this principle. I think that since we don't have tests that stub viewmodel and
we also don't have different viewmodels for the same UI then the value of that interface is not
clear to me.
So my point is that I think that we need to follow SOLID, but we also need to keep in mind other
principles, like KISS for example.


> 2. Rates are not updated every second


Sorry, I've understood the task wrong. I thought that we should freeze rates while we are in the
editing mode. I've watched Youtube video again and found that we shouldn't do that, so I've removed
this feature from the code.


> 3. We find it to be not the best decision to use a handler in the data layer. For posting live data value on a Ui thread you can use MutableLiveData.postValue()


Replaced handler at the data layer with MutableLiveData.postValue()
Handler at the UI layer still left, but I think it's fine since Handler is a UI tool itself.


> 4. You use context in the data layer to get an android resource and set it to the data entity. We are not sure that data entity should know something about UI layer requirement (icon)


Actually, I think that we can treat iconId as a data and since it's not an Android class itself, it's
just a reference to a resource. But I agree with you that CurrencyRateRepo shouldn't interact with
the whole Context while it needs to do just a couple of operations. I've removed CurrencyRateRepo context
dependency by the creation of IconDataSource and passing tickers and descriptions as parameters.


> 5. Amount is Double


I also thought that maybe it's better to use BigDecimal for it's better precision,
but since we are not doing some mission-critical calculations then I've got away with Double.
After your comment, I've moved to BigDecimal since it's cheap to implement and
who knows how this app might evolve :)


> 6. No Ui tests


I was also lazy to write them for the test application. I've added them now.
