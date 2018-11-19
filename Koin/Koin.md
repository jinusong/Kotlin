# Koin
## Koin이란?
* 안드로이드 앱을 개발할때 Dagger 와 같은 Dependency Injection(이하 DI)을 사용하는 것을 종종 볼 수 있습니다. 
* 각 컴포넌트간의 의존성을 외부 컨테이너에서 관리하는 방식을 통해 코드 재사용성을 높이고 Unit Test도 편하게 할 수 있게 되는 장점을 가지고 있습니다.
* Koin은 코틀린 개발자를 위한 실용적인 API제공을 하는 경량화된 의존성 주입 프레임워크입니다.
* 코틀린에서는 DSL을 제공하기때문에 어노테이션을 통한 코드 생성 대신 DSL을 활용하여, 의존성을 주입을 하기위한 똑똑하고 실용적인 API를 만들어낼 수 있습니다.
~~~gradle
def koin_version="1.0.0" // 최신버전은 위의 github링크 참조
implementation "org.koin:koin-android:$koin_version"
~~~
## 초기화
* Koin의 초기화는 정말 간단합니다. Application을 상속받은 클래스에 startKoin 함수를 호출하면서 미리 정의한 module들을 인자로 넘깁니다.
~~~kotlin
class Koin: Application(){
    override fun onCreate() {
        super.onCreate()

        startKoin(this, appModules)
    }
}
~~~

## 모듈 정의
* Koin에서는 오직 필요한 모듈들을 정의하고 필요한 곳에 by inject() 키워드를 통해 의존성을 주입하면 됩니다. 
* 추가적인 component 나 subcomponent들은 필요없습니다. Koin은 모듈을 정의할때 kotlin dsl를 사용하여 좀 더 직관적으로 정의가 가능합니다.

* ### Koin의 DSL 키워드
    * module - Koin 모듈을 정의할때 사용
    * factory - Dagger에서의 ActivityScope, FragmentScope와 유사한 기능으로 inject하는 시점에 해당 객체를 생성
    * single - Dagger에서의 Singleton 과 동일하며 앱이 살아있는 동안 전역적으로 사용가능한 객체를 생성
    * bind - 생성할 객체를 다른 타입으로 바인딩하고 싶을때 사용
    * get - 주입할 각 컴포넌트끼리의 의존성을 해결하기 위해 사용합니다.
* 잘이해가 안되더라도 예제를 보면 쉽게 이해할 수 있습니다.
* api를 호출하기 위한 retrofit service 객체를 모듈에 정의하고 위에 startKoin 인자에 넘길 appModules array를 생성해 보겠습니다.
~~~kotlin
val apiModule: Module = module {

    single {
        Retrofit.Builder()
             .client(OkHttpClient.Builder().build())
             .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
             .addConverterFactory(GsonConverterFactory.create())
             .baseUrl(getProperty<String>("BASE_URL"))
             .build()
             .create(Api::class.java)
    }
}

val photoListModule: Module = module {
    factory {
        PhotoPresenterImpl(get()) as PhotoPresenter
    }
}

val appModules = listOf(apiModule, photoListModule)
~~~
* 코틀린은 이런 경우 따로 class 를 만들필요가 없기 때문에 위와 같이 단순히 모듈들을 변수로 정의했습니다. 여기에서 single, factory 키워드를 사용해 객체를 생성했습니다. 
* 이렇게 생성할 경우 Retrofit api 객체는 전역적으로 한개의 객체만 생성이 가능하며 PhotoPresenter 객체는 생성한 액티비티 혹은 프래그먼트로 scope가 제한됩니다.
* 아래는 동일한 기능을 하는 Dagger의 모듈 구현부입니다
~~~kotlin
@Provides
@Singleton
fun provideApi(): Api {
    return Retrofit.Builder()
        .client(OkHttpClient.Builder().build())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseUrl)
        .build()
        .create(Api::class.java)
}
~~~
* 위에 Koin모듈 정의를 보면 get() 키워드를 사용하고 있습니다. 여기서 get()을 호출하면 apiModule 에 정의된 retrofit Api 클래스 객체가 넘어갑니다. 
* Koin은 생성하고자 하는 객체의 인자 타입을 보고 의존성을 판단해 쉽게 컴포넌트간의 의존성을 해결합니다. 실제로 PhotoPresenterImpl의 클래스 정의는 아래와 같습니다.
~~~kotlin
class PhotoPresenterImpl(val api: Api): PhotoPresenter() {
    override fun requestPhoto(id: Long) {
        //구현체
    }
}
~~~
* 좀 더 쉽게 설명하자면 아래와 같은 의존성이 있는 클래스들이 있다면
~~~kotlin
class ComponentA()
class ComponentB(val componentA : ComponentA)
~~~
* 모듈 정의를 아래와 같이 하게 됩니다.
~~~kotlin
val moduleA = module {
    // Singleton ComponentA
    single { ComponentA() }
}

val moduleB = module {
    // Singleton ComponentB with linked instance ComponentA
    single { ComponentB(get()) }
}
~~~
## 의존성 주입
* 이제 사용한 모듈들을 정의했으니 사용하고 싶은 곳에 주입해보겠습니다. Koin에서 주입은 by inject() 키워드를 사용(Kotlin Delegated Properties 방식을 사용)합니다. 
* 이는 Dagger에서의 @Inject 키워드와 유사하며 항상 지연초기화(lazy init)을 사용하게 됩니다. 즉 객체를 사용하는 시점에 생성을 하므로 성능상 이점이 있습니다.
~~~kotlin
class PhotoActivity : AppCompatActivity(), PhotoScene {

    private val presenter: PhotoPresenter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo)

        presenter.scene = this
        presenter.requestPhoto(1)
    }
    ...
}
~~~
* 예제에서는 PhotoPresenter 인터페이스 객체를 주입시키고 있고 실제 객체가 생성되는 시점은 presenter.scene을 호출하는 시점입니다. 
* 모듈을 정의하다보면 가끔 같은 객체를 여러개 생성해 각각 다른 목적으로 사용하고 싶을때가 있습니다.
* Koin 에서는 모듈의 이름을 정의해 이와 같은 conflict 를 해결합니다.
~~~kotlin
val dialogModule: Module = module {
    module("cancelableDialogBuilder"){
        single {
            AlertDialog.Builder(androidContext())
            .setCancelable(true)
        }
    }
    module("dialogBuilder"){
        single {
            AlertDialog.Builder(androidContext())
                .setCancelable(false)
        }
    }
}
~~~
~~~kotlin
// Request dependency from namespace
val cancelableDialog: AlertDialog.Builder by inject("cancelableDialogBuilder")
~~~
* 실제 프로젝트에서는 이렇게 사용하지는 않겠지만 이해를 돕기 위해 AlertDialog 사용해봤습니다.

* 여기까지 모듈을 정의하고 정의된 모듈을 주입하는것까지 해보았습니다. 이 외에 ViewModel 주입을 더 편하게 해주는 
* koin-android-viewmodel 라이브러리도 있고 Scoping을 편하게 해주는 koin-android-scope 라이브러리도 제공을 하고 있습니다. 
* Dagger와 비교했을때 실제 학습비용은 매우 낮지만 장단점이 있습니다. 판단은 각자의 몫이지만 좀더 가벼운 프로젝트를 시작할때 한번쯤 사용해보는것도 좋을 것 같습니다.
