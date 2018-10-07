# Anko Commons
## 의존성
* Anko Commons은 안드로이드 애플리케이션을 작성할 때 일반적으로 자주 구현하는 기능을 간편하게 추가할 수 있는 유틸리티 함수를 제공합니다. 
* Anko Commons을 사용하여면 이를 사용할 모듈의 빌드스크립트에 의존성을 추가하면 됩니다.
~~~gradle
// build.gradle

android {
    ...
}

dependencies {

    // Anko Commons 라이브러리를 추가합니다.
    implementation "org.jetbrains.anko:anko-commons:0.10.2"

    ...
}
~~~
* 서포트 라이브러리에 포함된 클래스를 사용하는 경우, 필요에 따라 anko-appcompat-v7-commons 혹은 anko=support-v4-commons을 빌드스크립트 내 의존성을 추가하면 됩니다.
~~~gradle
// build.gradle

android {
    ...
}

dependencies {

    // Anko Commons 라이브러리를 추가합니다.
    implementation "org.jetbrains.anko:anko-commons:0.10.2"

    // appcompat-v7용 Anko Commons 라이브러리를 추가합니다.
    implementation "org.jetbrains.anko:anko-appcompat-v7-commons:0.10.2"

    // support-v4용 Anko Commons 라이브러리를 추가합니다.
    implementation "org.jetbrains.anko:anko-support-v4-commons:0.10.2"
}
~~~
## 토스트 표시하기
* toast() 및 toastLong() 함수를 사용하면 토스트 메시지를 간편하게 표시할 수 있습니다.
* 토스트를 표시하려면 Context 클래스의 인스턴스가 필요하므로, 이 클래스 혹은 이를 상속하는 클래스(액티비티, 프래그먼트)내부에서만 사용할 수 있습니다.
~~~kotlin
// 다음 코드와 동일한 역할을 합니다.
// Toast.makeText(Context, "Hello, Kotlin!", Toast.LENGTH_SHORT).show()
toast("Hello, Kotlin!")

// 다음 코드와 동일한 역할을 합니다.
// Toast.makeText(Context, R.string.hello, Toast.LENGTH_SHORT).show()
toast(R.string.hello)

// 다음 코드와 동일한 역할을 합니다.
// Toast.makeText(Context, "Hello, Kotlin!", Toast.LENGTH_LONG).show()
longToast("Hello, Kotlin!)
~~~
## 다이얼로그 생성 및 표시하기
* alert() 함수를 사용하면 AlertDialog를 생성할 수 있습니다. 
* 토스트와 마찬가지로 Context 클래스 혹은 이를 상속하는 클래스(액티비티, 프래그먼트) 내부에서만 사용할 수 있습니다.
~~~kotlin
// 다이얼로그의 제목과 본문을 지정합니다.
alert(title = "Message", message = "Let's learn Kotlin!") {

    // AlertDialog.Builder.setPositiveButton()에 대응합니다.
    positiveButton("Yes") {
        // 버튼을 클릭했을 때 수행할 동작을 구현합니다.
        toast("Yay!")
    }

    // AlertDialog.Builder.setNegativeButton()에 대응합니다.
    negativeButton("No") {
        // 버튼을 클릭했을 때 수행할 동작을 구현합니다.
        longToast("No way...")
    }
}.show()
~~~
* 프레임워크에서 제공하는 다이얼로그가 아닌 서포트 라이브러리에서 제공하는 다이얼로그(android.support.v7.app.AlertDialog)를 생성하려면,
* anko-appcompat-v7-commons을 의존성에 추가한 후 다음과 같이 Appcompat을 함께 인자에 추가하면 됩니다.
~~~kotlin
// import 문이 추가됩니다.
import org.jetbrains.anko.appcompat.v7.Appcompat

// Appcompat을 인자에 추가합니다.
alert(Appcompat, title = "Message", message = "Let's learn Kotlin!") {
    ...
}.show()
~~~
* 여러 항목 중 하나를 선택하도록 할 떄 사용하는 리스트 다이얼로그는 selector() 함수를 사용하여 생성할 수 있습니다.
~~~kotlin
// 다이얼로그에 표시할 목록을 생성합니다.
val cities = listOf("Seoul", "Tokyo", "Mountain View", "Singapore")

// 리스트 다이얼로그를 생성하고 표시합니다.
selector(title = "Select City", items = cities) { dig, selection -> 
    
    // 항목을 선택했을 때 수행할 동작을 구현합니다.
    toast("You selected ${cities[selection]}!")
}
~~~
* 작업의 진행 상태를 표시할 때 사용하는 프로그레스 다이얼로그는progressDialog()와 indeterminateProgressDialog() 함수를 사용하여 생성할 수 있습니다.
* progressDialog() 함수는 파일 다운로드 상태와 같이 진행률을 표시해야 하는 다이얼로그를 생성할 때 사용합니다. 
* indeterminateProgressDialog() 함수는 진행률을 표시하지 않는 다이얼로그를 생성할 때 사용합니다.
~~~kotlin
// 진행률을 표시하는 다이얼로그를 생성합니다.
val pd = progressDialog(title = "File Download", message = "Downloading...")

// 다이얼로그를 표시합니다.
pd.show()

// 진행률을 50으로 조정합니다.
pd.progress = 50

// 진행률을 표시하지 않는 다이얼로그를 생성하고 표시합니다.
indenterminateProgressDialog(message = "Please wait...").show()
~~~
## 인텐트 생성 및 사용하기
* 인텐트는 컴포넌트 간에 데이터를 전달할 때에도 사용하지만 주로 액티비티나 서비스를 실행하는 용도로 사용합니다.
* 다른 컴포넌트를 실행하기 위해 인텐트를 사용하는 경우, 이 인텐트는 대상 컴포넌트에 대한 정보와 기타 부가 정보를 포함합니다.
~~~kotlin
// DetailActivity 액티비티를 대상 컴포넌트로 지정하는 인텐트
val intent = Intent(this, DetailActivity::class.java)

// DetailActivity를 실행합니다.
startActivity(intent)
~~~
* 이 인텐트에 부가 정보를 추가하거나 플래그를 설정하는 경우 인텐트를 생성하는 코드는 다음과 같습니다.
~~~kotlin
val intent = Intent(this, DetailActivity::class.java)

// 인텐트에 부가정보를 추가합니다.
intent.putExtra("id", 150L)
intent.putExtra("title", "Awesome item")

// 인텐트에 플래그를 생성합니다.
intent.setFlag(Intent.FLAG_ACTIVITY_NO_HISTORY)
~~~
* intentFor() 함수를 사용하면 훨씬 간소한 현태로 동일한 역할을 하는 인텐트를 생성할 수 있습니다.
~~~kotlin
val intent = intentFor<DetailActivity>(
    // 부가 정보를 Pair 형태로 추가합니다.
    "id" to 150L, "title" to "Awesome item")

    // 인텐트 플래그를 설정합니다.
    .noHistory()
~~~
* 인텐트에 플래그를 지정하지 않는다면, startActivity() 함수나 startService() 함수를 사용하여 인텐트 생성과 컴포넌트 호출을 동시에 수행할 수 있습니다.
* 이들 함수는 모두 Context 클래스를 필요로 하므로, 이 클래스 혹은 이를 상속하는 클래스(액티비티, 프래그먼트) 내부에서만 사용할 수 있습니다.
~~~kotlin
// 부가정보 없이 DetailActivity를 실행합니다.
startActivity<DetailActivity>()

// 부가정보를 포함하여 DetailActivity를 실행합니다.
startActivity<DetailActivity>("id" to 150L, "title" to "Awesome item")

// 부가정보 없이 DataSyncService를 실행합니다.
startService<DataSyncService>()

// 부가정보를 포함하여 DataSyncService를 실행합니다.
startService<DataSyncService>("id" to 1000L)
~~~
* 이 외에도, 자주 사용하는 특정 작업을 바로 수행할 수 있는 함수들을 제공합니다.
~~~kotlin
// 전화를 거는 인텐트를 실행합니다.
makeCall(number = "01012345678")

// 문자메시지를 발송하는 인텐트를 실행합니다.
sendSMS(number = "01012345678", text = "Hello, Kotlin!")

// 웹 페이지를 여는 인텐트를 실행합니다.
browse(url = "https://google.com")

// 이메일을 발송하는 인텐트를 실행합니다.
email(email = "jyte82@gmail.com", subject = "Hello, Taeho Kim", text = "How are you?")
~~~
## 로그 메시지 기록하기
* 안드로이드 애플리케이션에서 로그메시지를 기록하려면 android.util.Log 클래스에서 제공하는 메서드를 사용해야 합니다. 
* 하지만 로그를 기록하는 함수를 호출할 때마다 매번 태그를 함께 입력해야 하므로 다소 불편합니다.
* Anko 라이브러리에서 제공하는 AnkoLogger를 사용하면 훨씬 편리하게 로그 메시지를 기록할 수 있습니다.
* AnkoLogger에서는 다음과 같이 android.util.Log 클래스의 로그 기록 메서드에 대응하는 함수를 제공합니다.

|android.util.Log|AnkoLogger|
|----------------|----------|
|v()|verbose()|
|d()|debug()|
|i()|info()|
|w()|warn()|
|e()|error()|
|wtf()|wtf()|
* AnkoLogger를 사용하려면 이를 사용할 클래스에서 AnkoLogger 인터페이스를 구현하면 됩니다. 
* AnkoLogger 인터페이스를 구현한 액티비티에서의 사용 예는 다음 코드에서 확인할 수 있습니다.
* 출력할 메시지의 타입으로 String만 허용하는 android.util.Log 클래스와 달리 모든 타입을 허용하는 모습을 확인할 수 있습니다.
~~~kotlin
// AnkoLogger 인터패이스를 구현합니다.
class MainActivity: AppCompatActivity(), AnkoLogger {

    fun doSomething() {
        // Log.INFO 레벨로 로그 메시지를 기록합니다.
        info("doSomething() called")
    }

    fun doSomethingWithParameter(number: Int) {
        // Log.DEBUG 레벨로 로그 메시지를 기록합니다.
        // String 타입이 아닌 인자는 해당 인자의 toString() 함수 변환값을 기록합니다.
        debug(number)
    }
    ...
}
~~~
* AnkoLogger에서 제공하는 함수를 사용하여 로그 메시지를 기록하는 경우, 로그 태그로 해당 함수가 호출되는 클래스의 이름을 사용합니다.
* 따라서 앞의 예제에서는 "MainActivity"를 로그 태그로 사용합니다.
* 로그 태그를 바꾸고 싶다면 loggerTag 프로퍼티를 오버라이드하면 됩니다.
~~~kotlin
class MainActivity: AppCompatActivity(), AnkoLogger {
    // 이 클래스 내에서 출력되는 로그 태그를 "Main"으로 지정합니다.
    override val loggerTag: String
        get() = "Main"

    ...
}
~~~
## 단위 변환하기
* 안드로이드는 다양한 기기를 지원하기 위해 픽셀(px) 단위 대신 dip(혹은 dp; device independent pixels)나 sp(scale independent pixels)를 사용합니다.
* dp나 sp 단위는 각 단말기의 화면 크기나 밀도에 따라 화면에 표시되는 크기를 일정 비율로 조정하므로, 다양한 화면 크기나 밀도를 가진 단말기에 대응하는 UI를 작성할 때 유용합니다.
* 커스텀 뷰 내뷰와 같이 뷰에 표시되는 요소의 크기를 픽셀 단위로 다루는 경우 dp나 sp단위를 픽셀 단위로 변환하기 위해 복잡한 과정을 거쳐야 합니다.
~~~kotlin
class MainActivity : AppCompatActivity() {
    fun doSomething() {
        // 100dp를 픽셀 단위로 변환합니다.
        val dpInPixel = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 100f, resources.displayMetrics)

        // 16sp를 픽셀 단위로 변환합니다.
        val spInPixel = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 16f, resources.displayMetrics)
    }
    ...
}
~~~
* Anko에서 제공하는 dip() 및 sp() 함수를 사용하면 이러한 단위를 매우 간단히 변환할 수 있습니다.
* 단위를 변환하기 위해 단말기의 화면 정보를 담고 있는 DisplayMetrics 객체가 필요하므로, 이 함수들은 단말기 화면 정보에 접근할 수 있는 클래스인 Context를 상속한 클래스 혹은 커스텀 뷰 클래스 내에서 사용할 수 있습니다.
* dip() 함수 및 sp() 함수를 사용하여 앞의 코드를 간단하게 표현한 모습입니다.
* TypedValue.applyDimension() 메서드는 Float 형 인자만 지원했지만, dip() 및 sp() 함수는 Int 형 인자도 지원합니다.
~~~kotlin
// 100dp를 픽셀 단위로 변환합니다.
val dpInPixel = dip(100)

// 16sp를 픽셀 단위로 변환합니다.
val spInPixel = sp(16)
~~~
* 반대로, 픽셀 단위를 dp나 sp 단위로 변환하는 함수도 제공합니다. 각각 px2dip(), px2sp() 함수를 사용합니다.
~~~kotlin
// 300px를 dp 단위로 변환합니다.
val pxInDip = px2dip(300)

// 80px를 sp 단위로 변환합니다.
val pxInSp = px2sp(80)
~~~
## 기타
* 여러 단말기 환경을 지원하는 애플리케이션은, 단말기 환경에 따라 다른 형태의 UI를 보여주도록 구현하는 경우가 많습니다.
* 이러한 경우, configuration() 함수를 사용하면 특정 단말기 환경일 때만 실행할 코드를 간단하게 구현할 수 있습니다.

|매개변수 이름|단말기 환경 종류|
|---------|----------|
|density|화면 밀도|
|language|시스템 언어|
|long|화면 길이|
|nightMode|야간모드 여부|
|orientation|화면 방향|
|rightToLeft|RTL(Right-to-Left)레이아웃 여부
|screenSize|화면 크기|
|smallestWidth|화면의 가장 작은 변의 길이|
|uiMode|UI 모드(일반, TV, 차량, 시계, VR 등)|
* configuration() 또한 단말기 환경에 접근해야 하므로 이 정보에 접근할 수 있는 Context 클래스 혹은 이를 상속한 클래스(액티비티, 프래그먼트)에서만 사용할 수 있습니다.
~~~kotlin
class MainActivity : AppCompatActivity() {
    fun doSomething() {
        configuration(orientation = Orientation.PORTRAIT) {
            // 단말기가 세로 방량일 때 수행할 코드를 작성합니다.
            ...
        }
        configuration(orientation = Orientation.LANDSCAPE, language = "ko") {
            // 단말기가 가로 방향이면서 시스템 언어가 한국어로 설정되어 있을 때
            // 수행할 코드를 작성합니다.
            ...
        }
    }
    ...
}
~~~
* 단순히 단말기의 OS 버전에 따라 분기를 수행하는 경우 doFromSdk()와 doIfSdk()를 사용할 수 있습니다.
~~~kotlin
doFromSdk(Build.VERSION_CODES.0) {
    // 안드로이드 8.0 이상 기기에서 수행할 코드를 작성합니다.
    ...
}

doIfSdk(Build.VERSION_CODES.N) {
    // 안드로이드 7.0 기기에서만 수행할 코드를 작성합니다.
    ...
}
~~~