# Kotlin Coroutines
## intro
* 일부 API는 네트워크 IO, 파일 IO, CPU 또는 GPU 집약적인 작업 등과 같은 장기 실행 작업을 시작하고 호출자가 완료 될 때까지 차단하도록 요청합니다. coroutine은 스레드를 차단하지 않고 더 가볍고 제어 가능한 작업으로 바꾸는 방법을 제공합니다.
* coroutine은 비동기 프로그래밍을 단순화합니다. 프로그램의 로직은 코루틴에서 순차적으로 표현될 수 있으며 기본 라이브러리는 비동기를 파악합니다. 라이브러리에서는 사용자 코드의 관련 부분을 콜백으로 래핑하고, 관련 이벤트를 구독하고, 다른 스레드(또는 다른 시스템)에서 실행을 예약할 수 있으며 코드는 순차적으로 실행되는 것처럼 간단합니다.

## Coroutines
* Coroutine에 대해서 이야기할 때 async/await에 대해서 이야기를 가장 많이 했었습니다. 
* 그래서 코드 가독성과 구현 방법을 우선해서 기존 Java Thread와 AsyncTask를 통해 살펴보며 Coroutines에 대하여 알아봅니다.
* 다음 예시는 아래와 같은 가정하에 작성되었습니다.
    * 버튼을 누른다.(여기서는 제외합니다)
    * 네트워크 처리를 위해서 Progress를 실행
    * loadNetworkSomething()으로 네트워크를 처리
    * progress 숨김
    * UI 갱신

### Java Thread
* Java Thread는 Java에서 기본으로 제공하는 라이브러입니다. 다운로드를 구현해보면 다음과 같습니다.
~~~kotlin
val thread = Thread(Runnable {
    var data = ""
    Handler(Looper.getMainLooper()).post {
        // show Progress on UI Thread
    }
    data = loadNetworkSomething()
    Handler(Looper.getMainLooper()).post {
      // UI data update from UI thread
      // Hide Progress from UI thread
    }
})

thread.start()
~~~

### Android AsyncTask
* 안드로이드에서는 이러한 Thread를 구조화 시켜 AsyncTask를 제공하고 있는데 아래와 같이 구현이 가능합니다. 
* Background, UI Thread을 구분해준 메소드를 통해 이를 활용할 수 있다. Java Thread보다는 높은 가독성을 가지고 있습니다.
* 다만 추가가 쉽지 않고, 취소에 대한 처리를 별도로 해야 합니다.
~~~kotlin
val loadData = object : AsyncTask<Unit, Unit, String>() {
    override fun doInBackground(vararg params: Unit?): String {
        return loadSomethingData()
    }

    override fun onPreExecute() {
        super.onPreExecute()
        // Show progress from UI thread
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        // UI data update from UI thread
        // Hide Progress from UI thread
    }
}

@Test
fun test() {
  loadData.execute(Unit)
}
~~~

### Coroutine
* UI 스레드로 블록을 지정하고, async/await을 이용해 loadSomethingData를 불러옵니다. 위에서 보았던 코드들 보다 더 높은 가독성을 가지고 있습니다.
~~~kotlin
CoroutineScope(Dispatchers.Main).launch {
    // Show progress from UI thread
    var data = ""
    CoroutineScope(Dispatchers.Default).async {
        // background thread
        data = loadNetworkSomething()
    }.await()
    // UI data update from UI thread
    // Hide Progress from UI thread
}
~~~

## Routine
* 백그라운드와 UI를 구분하여 처리해보았습니다. 이런 루틴은 서브루틴과 코루틴으로 나뉩니다.

### Subroutine
* 개발을 하다 보면 중복 코드를 자연스럽게 함수로 만들어 호출해서 사용합니다. 
* 굳이 동일한 코드를 여러 장소에 두어 사용할 필요가 없고, 수정 시 수정 범위를 최소화하기 위해서이기도 합니다. 
* 예를 들면 다음과 같은 Fragment replace 하는 함수를 만들어두고, replace가 필요한 여러 곳에서 활용이 가능합니다.
~~~kotlin
fun AppCompatActivity.replace(@IdRes frameId: Int, fragment: android.support.v4.app.Fragment, tag: String? = null) {
    supportFragmentManager.beginTransaction().replace(frameId, fragment, tag).commit()
}
~~~
* 이런 형태를 Subroutine이라 부릅나다. Wiki에 따르면 언어별로 부르는 용어가 다양한데 Java에서는 function이라 부릅니다. 
* 결과적으로 서브루틴은 함수를 호출하고, 서브루틴의 모든 처리가 완료되어야 다음 줄의 코드를 실행할 수 있습니다.

### Coroutines
* 서브루틴의 확장 개념인 Coroutines은 아래와 같은 장점을 가지고 있습니다.
    * 여러 다른 지점에서 입력과 종료가 일어납니다.
    ![Coroutines](https://thdev.tech/images/posts/2018/10/Kotlin-Coroutines/coroutines-example.png)
    * 실행을 일시 중지하고 호출자 또는 다른 Coroutine으로 이동할 수 있습니다. 호출자는 언제든 다시 Coroutine을 실행시킬 수 있습니다.
* 단순하게는 Thread처럼 보일 수 있지만 Thread보다 훨씬 저렴한 비용으로 동작하게 됩니다. Subroutine처럼 함수 형태로 코드를 작성하여 가독성이 높습니다.

### Coroutine과 Thread
* Java Thread는 하나의 프로세스에 여러 개의 작업을 실행할 수 있지만 OS의 Native Thread에 직접 링크되어 동작하여 많은 시스템 자원을 사용합니다. 
* Thread 간 전환 시에도 CPU의 상태 체크가 필요하므로 그만큼의 비용이 발생합니다.
* 반면 Coroutine은 즉시 실행하는 게 아니며, Thread와 다르게 OS의 영향을 받지 않아 그만큼 비용이 들어가지 않습니다. 
* 그리고 개발자가 직접 루틴을 언제 실행할지, 언제 종료할지 모두 지정이 가능합니다. 이렇게 생성한 루틴은 작업 전환 시에 시스템의 영향을 받지 않아 그에 따른 비용은 발생하지 않습니다. 또한 suspension 포인트를 개발자가 지정하기 때문에 무작위로 종료할 순 없습니다.

## blocking and delay
* 기본적으로 coroutine은 스레드를 차단하지 않고 일시 중단할 수 있습니다. 스레드를 차단하는 것은 상대적으로 적은 수의 스레드만 유지할 수 있으므로 고부하인 경우 특히 비용이 많이 듭니다. 따라서 하나를 차단하면 중요한 작업이 지연됩니다.
* 반면 coroutine은 컨텍스트 스위치 또는 OS의 다른 개입은 필요하지 않습니다. 또한 사용자 라이브러리에 의해 서스펜션을 제어할 수 있습니다. 라이브러리 작성자는 서스펜션시 발생할 일을 결정하고 필요에 따라 최적화 / 로그 / 인터셉트를 결정할 수 있습니다.

## Coroutine 언제 사용하면 좋을까요?
* Coroutine은 결국 대용량 처리, 복잡한 계산, 게임 등에서 유용하게 사용할 수 있습니다. 
* 보통 유니티에서 많이 사용하고 있습니다. 코틀린에서도 이를 제공하여 Android 개발에서도 Coroutine 사용이 가능합니다.
* 다만 ReactiveX처럼 강력한 라이브러리 형태를 제공하지는 못합니다. Github을 통해 그래도 괜찮은 라이브러리를 몇 개 찾을 수 있긴 하지만 ReactiveX 만큼의 편의성은 아직입니다. 필요시 만들어 쓰는 방법과 그냥 RxJava 활용을 하는 편이 좋습니다.

## Kotlin Coroutine
* 1.2 버전에서는 별도의 라이브러리를 통해 이를 제공하고 있는데, 1.3 버전부터는 코틀린에 포함되어 제공하고 있습니다.
* kotlin coroutine의 경우 다른 언어에서 제공하는 비동기 처리에 대해서도 추가로 제공하고 있습니다. 
* C#과 ECMAScript에서 제공하는 async/await. Go에서 제공하는 channels, select. C#과 Python에서 제공하는 generators/yield도 사용할 수 있습니다.



## 안드로이드에 Coroutine 적용하기
~~~kotlin
dependencies {
  implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:0.30.1'
}

kotlin {
    experimental {
        coroutines "enable"
    }
}
~~~

## Android Coroutine
* Android Coroutine을 추가하면 UI 스레드의 사용이 가능합니다. coroutens.android에서 제공하는 샘플 코드는 아래와 같습니다.
* 아래와 같이 job을 생성하고, 이를 cancel 처리해줄 수 있습니다.
~~~kotlin
fun setup() {
    val job = GlobalScope.launch(Dispatchers.Main) { // launch coroutine in the main thread
        delay(1000)
        for (i in 10 downTo 1) { // countdown from 10 to 1
            tv_message.text = "Countdown $i ..." // update text
            delay(500) // wait half a second
        }
        tv_message.text = "Done!"
    }
    fab.setOnClickListener {
        job.cancel() // cancel coroutine on click
    }
}
~~~
* 위 코드는 launch {} 부분이 별도의 Main thread에서 동작하고, 나머지는 cancel을 할 수 있도록 setOnClickListener을 등록합니다. 생성한 job을 가지고 cancel() 할 수 있습니다.


# Onclick 해보기
## Cowntdown 코드 살펴보기
* 다음은 아까 했던 Countdown 코드입니다.
~~~kotlin
fun setup() {
    val job = GlobalScope.launch(Dispatchers.Main) { // launch coroutine in the main thread
        for (i in 10 downTo 1) { // countdown from 10 to 1
            tv_message.text = "Countdown $i ..." // update text
            delay(500) // wait half a second
        }
        tv_message.text = "Done!"
    }
    fab.setOnClickListener {
        job.cancel() // cancel coroutine on click
    }
}
~~~
* 이 코드에서 알아볼 것
    * 코루틴을 실행하는 블록
    * 코루틴의 스레드 형태를 어떻게 가져갈지 정의((Dispatchers.Main, Dispatchers.Default)

## 코루틴을 실행하는 블록
* 코투린을 실행하는 블록은 두 가지를 제공하고 있습니다. 위의 샘플 코드에서는 GlobalScope을 사용하였습니다.

### GlobalScope
* GlobalScope는 싱글톤으로 전역 범위의 스레드에서 유용하게 사용할 수 있는데, 일반적인 어플리케이션이 종료되기 전까지 동작이 필요한 경우 유용합니다.
* GlobalScope의 원래 코드는 다음과 같이 기본 EmptyCoroutineContext를 사용하고, isActive는 항상 true를 리턴하고 있습니다. 
* 문서상 GlobalScope는 launch 또는 async와 함께 사용하는 것은 좋은 방법이 아니며, 일반적인 코드에서는 매번 새로 생성하는 CoroutineScope을 사용하라고 합니다.
~~~kotlin
object GlobalScope : CoroutineScope {
    /**
     * @suppress **Deprecated**: Deprecated in favor of top-level extension property
     */
    @Deprecated(level = DeprecationLevel.HIDDEN, message = "Deprecated in favor of top-level extension property")
    override val isActive: Boolean
        get() = true

    /**
     * Returns [EmptyCoroutineContext].
     */
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext
}
~~~
* 이런 GlobalScope의 좋은 예는 아래와 같습니다.
~~~kotlin
fun ReceiveChannel<Int>.sqrt(): ReceiveChannel<Double> = GlobalScope.produce(Dispatchers.Unconfined) {
  for (number in this) {
    send(Math.sqrt(number))
  }
}
~~~

### CoroutineScope
* CoroutineScope는 모든 하위 Scope의 확장 형태를 가집니다. 위의 GlobalScope 역시 CoroutineScope을 상속받아 구현하고 있습니다. 
* CoroutineScope의 coroutineContext 상속에 따라 컨텍스트와 취소 동작을 사용할지 지정합니다.
* 모든 Coroutine builder(like launch, async, etc)와 모든 하위 scope(like coroutineScope, withContext, etc)에서 메인 Job(CoroutineScope의 Job)에 의해 동작을 처리할 수 있습니다. 
* CoroutineScope 내부에서 정의한 Coroutine은 모든 동작이 완료될 때까지 기다렸다가 다음을 처리하도록 동작합니다.
* CoroutineScope을 정의하였는데, wait -> Done으로 넘어가려면 1초를 대기하고 넘어갑니다. 이러한 형태를 CoroutineScope()의 정의에 따라서 동작하게 됩니다.

~~~kotlin
CoroutineScope(Dispatchers.Main).launch {
    tv_message.text = "wait"
    delay(1000)
    tv_message.text = "Done!"
}
~~~

* 안드로이드에서 CoroutineScope을 잘 사용하려면 Coroutine 라이프 사이클과 함께 사용하는 게 좋습니다. 
* 이러한 샘플 코드는 문서에 잘 정의되어 있는데 다음과 같은 Activity를 만들어두고, job을 활용할 수 있도록 정의하고 사용하도록 안내하고 있습니다.

~~~kotlin
class MyActivity : AppCompatActivity(), CoroutineScope {

  // Job을 등록할 수 있도록 초기화
  lateinit var job: Job

  // 기본 Main Thread 정의와 job을 함께 초기화
  override val coroutineContext: CoroutineContext
      get() = Dispatchers.Main + job

  override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      job = Job()
  }

  // 작업 중이던 모든 job을 종 children을 종료처리
  override fun onDestroy() {
      super.onDestroy()
      job.cancel() // Cancel job on activity destroy. After destroy all children jobs will be cancelled automatically
  }

  /*
   * Note how coroutine builders are scoped: if activity is destroyed or any of the launched coroutines
   * in this method throws an exception, then all nested coroutines are cancelled.
   */
    fun loadDataFromUI() = launch { // <- extension on current activity, launched in the main thread
       val ioData = async(Dispatchers.IO) { // <- extension on launch scope, launched in IO dispatcher
           // blocking I/O operation
       }
       // do something else concurrently with I/O
       val data = ioData.await() // wait for result of I/O
       draw(data) // can draw in the main thread
    }
}
~~~

### scope 선택
* GlobalScope와 CoroutineScope의 용도에 적절하게 사용하는 게 좋습니다.
* CoroutineScope에서는 꼭 Job을 별도로 만들어서 이를 활용함이 좋은데 저렇게 미리 만들어두는 게 좋습니다. 
* 아래쪽에서 설명할 것이지만, 별도로 만드는 scope의 경우는 Android AAC Lifecycle을 함께 활용하여 처리하는 방법도 가능합니다.

## 코루틴의 스레드 형태를 어떻게 가져갈지 정의
* 코루틴도 RxJava와 같이 스레드 형태를 여러 개 제공하고 있습니다. 
* 안드로이드에서는 Dispatchers.Main과 Dispatchers.Default을 활용하게 될 것입니다.

### Dispathcers.Main
* Dispatchers.Main은 안드로이드 용으로 제공하는 thread 입니다. Java Handler가 기본으로 초기화되어 사용합니다.

### Dispathcers.Default
* Dispatchers.Default은 모든 launch, async, etc에서 사용하는데, ContinuationInterceptor을 지정하지 않을 경우 기본으로 사용하는 CoroutineDispatcher입니다.
* 이 Dispatches는 JVM에서 공유된 스레드 풀에 의해 동작합니다.
* Dispatches.Default에서 사용하는 최대 스레드 수는 CPU 코어 수와 같은데 최소 두 개를 사용하고 있습니다.

## Android OnClick에 따른 적절한 coroutine 처리 알아보기
* onClick
~~~kotlin
private var count = 0
btn_start.setOnClickListener {
    count++
    CoroutineScope(Dispatchers.Main).launch {
        for (i in 10 downTo 1) { // countdown from 10 to 1
            tv_message.text = "Now Click $count Countdown $i ..." // update text
            delay(500) // wait half a second
        }
        tv_message.text = "Done!"
    }
}
~~~
* 이제 앱을 실행하고, start 버튼을 N 번 눌러봅니다. 아래 그림처럼 계속적인 버튼을 눌렀다면 무슨 일이 일어날까요?
~~~
Now Click 1 Countdown 5 ...
Now Click 2 Countdown 5 ...
Now Click 1 Countdown 4 ...
Now Click 3 Countdown 5 ...
...
Now Click 1 Countdown 1 ...
Done!
Now Click 5 Countdown 5 ...
Now Click 4 Countdown 4 ...
Now Click 3 Countdown 1 ...
Done!
~~~
* 사용자 액션에 따라 무작위로 동작해야 하는 경우도 있지만, 보통 그렇지 않습니다. 
* 이전 동작이 모두 끝나고 나면 다음 동작을 해야 하는 경우도 있고, 중간중간 사용자의 액션을 처리해야 하는 경우도 있습니다.

## actor의 이용
* 만약 네트워크를 통해 데이터를 받아오고, 이를 노출한다고 해봅니다.

* 이때 위와 같이 N 번 클릭을 무작위로 받는 코드를 작성한다면? 결과는 당연히 네트워크도 N 번 동작하게 됩니다. 기존 이벤트를 취소하는 것도 적절한 방법은 아닙니다.

* 그래서 actor을 이용할 수 있는데, 아래와 같이 코드를 수정해보았습니다.

~~~kotlin
private var count = 0
override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContentView(R.layout.activity_main)

  btn_start.onClick {
    count++
    for (i in 10 downTo 1) { // countdown from 10 to 1
        tv_message.text = "Now Click $count Countdown $i ..." // update text
        delay(500) // wait half a second
    }
    tv_message.text = "Done!"
  }
}

private fun View.onClick(action: suspend (View) -> Unit) {
  // launch one actor
  val event = GlobalScope.actor<View>(Dispatchers.Main) {
    for (event in channel) action(event)
  }

  setOnClickListener {
    event.offer(it)
  }
}
~~~
* 위 코드는 여러 번 누르더라도 기존 action을 모두 처리하기 전에는 다음으로 넘어가지 않습니다.

## 끝
* 코루틴 좋아요!
* 참고 블로그 태환님의 안드로이드 블로그