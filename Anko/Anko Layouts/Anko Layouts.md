# Anko Layouts
* 안드로이드 애플리케이션을 작성할 때, 대부분 XML 레이아웃을 사용하여 화면을 구성합니다.
* 소스 코드(Java 혹은 Kotlin)를 사용하여 화면을 구성하는 것도 가능하지만 XML 레이아웃에 비해 복잡하고 까다라로워 대다수의 사람들이 선호하지 않습니다.
* 하지만 XML로 작성된 레이아웃을 사용하려면 이 파일에 정의된 뷰를 파싱하는 작업을 먼저 수행해야 합니다.
* 때문에 소스 코드를 사용하여 화면을 구성한 경우에 비해 애플리케이션의 성능이 저하되고, 파싱 과정에서 자원이 더 필요한 만큼 배터리도 더 많이 소모합니다.
* Anko Layouts는 소스 코드로 화면을 구성할 때 유용하게 사용할 수 있는 여러 함수들을 제공하며, 아룰 사용하면 XML 레이아웃을 작성하는 것처럼 편리하게 소스코드로도 화면을 구성할 수 있습니다.
* Anko Layouts을 사용하려면 이를 사용할 모듈의 빌드스크립트에 의존성을 추가하면 되며, 애플리케이션의 minSdkVersion에 따라 사용하는 라이브러리가 달라집니다.
* 애플리케이션의 minSdkVersion에 대응하는 Anko Layouts 라이브러리는 다음과 같습니다.

|minSdkVersion|Anko Layouts 라이브러리|
|---------|----------|
|15이상 19미만|anko-sdk15|
|19이상 21미만|anko-sdk19|
|21이상 23미만|anko-sdk21|
|23이상 25미만|anko-sdk23|
|25이상|anko-sdk25|
~~~gradle
android {
    defaultConfig {
        // minSdkVersion이 15로 설정되어 있습니다.
        minSdkVersion 15
        targetSdkVersion 27
        ...
    }
    ...
}

dependencies {
    // minSdkVersion에 맞추어 Anko Layouts 라이브러리를 추가합니다.
    compile "org.jetbrains.anko:anko-sdk15:0.10.2"
}
~~~
* 서프트 라이브러리에 포함된 뷰를 사용하는 경우,  사용하는 뷰가 포함된 라이브러리에 대응하는 Anko Layouts 라이브러리를 의존성에 추가하면 됩니다.
* 각 서프트 라이브러리에 대응하는 Anko Layouts 라이브러리는 다음과 같습니다.
|서포트 라이브러리|Anko Layouts 라이브러리|
|---------|----------|
|appcompat-v7|anko-appcompat-v7|
|cardview|anko-cardview-v7|
|design|anko-design|
|gridlayout|anko-gridlayout-v7|
|recyclerview-v7|anko-recyclerview-v7|
|support-v4|anko-support-v4|
* 다음은 서포트 라이브러리와 이에 대응하는 Anko Layouts 라이브러리를 의존성으로 추가한 예입니다.
~~~gradle
android {
    ...
}

dependencies {
    // appcompat-v7 서포트 라이브러리 추가
    implementation "com.android.support:appcompat-v7:27.0.1"

    // appcompat-v7용 Anko Layouts 라이브러리를 추가합니다.
    implementation "org.jetbrains.anko:anko-appcompat-v7:0.10.2"
}
~~~
## DSL로 화면 구성하기
* Anko Layouts을 사용하면 소스 코드에서 화면을 DSL(Domain Specific Language) 형태로 정의할 수 있습니다.
* 다음은 DSL을 사용하여 화면을 구성하는 간단한 예를 보여줍니다. XML 레이아웃으로 정의할 때보다 더 간단하게 화면을 구성할 수 있는 것을 확인할 수 있습니다.
~~~kotlin
verticalLayout {
    padding = dip(12)

    textView("Enter Login Credentials")

    editText {
        hint = "E-mail"
    }

    editText {
        hint = "Password"
    }

    button("Submit")
}
~~~
* 앞의 코드에서 사용한 verticalLayout(), textView(), editText(), button()은 Anko Layout에서 제공하는 함수로, 뷰 혹은 다른 뷰를 포함할 수 있는 레이아웃을 생성하는 역할을 합니다.
* 다음은 여기에서 제공하는 함수 중 자주 사용하는 함수 몇 개의 목록입니다.

|함수|생성하는 뷰|비고|
|---|--------|----|
|button()|android.widget.Button||
|checkBox()|android.widget.CheckBox||
|editText()|android-widget.EditText||
|frameLayout()|android-widget.FrameLayout||
|imageView()|android-widget-ImageView||
|linearLayout()|android.widget.LinearLayout||
|radioButton()|android.widget.RadioButton||
|relativeLayout()|android-widget.RelativeLayout||
|switch()|android-widget.Switch|서포트 라이브러리에서 제공하는 뷰는 switchCompat() 사용|
|verticalLayout()|android-widget-LinearLayout|orientation 값으로 LinearLayout.VERTICAL을 갖는 LinearLayout|
|webView()|android-webkit.WebView||
* XML 레이아웃 파일에 XML로 구성한 레이아웃을 저장하듯이, DSL로 구성한 뷰는 AnkoComponent 클래스를 컨테이너로 사용합니다.
* AnkoComponent에는 정의되어 있는 화면을 표시할 대상 컴포넌트의 정보를 포함합니다.
* 다음은 MainActivity 액티비티에 표시할 뷰의 정보를 가지는 AnkoComponent의 코드 예시를 보여줍니다.
~~~kotlin
class MainActivityUI : AnkoComponent<MainActivity> {

    override fun createView(ui: AnkoContext<MainActivity>) = ui.apply {
        vertivalLayout {
            // LinearLayout의 padding을 12dp로 설정합니다.
            padding = dip(12)

            // TextView를 추가합니다.
            textView("Enter Login Credentials")

            // EditText를 추가하고, 힌트 문자열을 설정합니다.
            editText {
                hint = "E-mail"
            }

            editText {
                hint = "password"
            }

            // 버튼을 추가합니다.
            button("Submit")
        }
    }.view
}
~~~
* 추가로, 액티비티에서는 AnkoComponent 없이 직접 액티비티 내에서 DSL을 사용하여 화면을 구성할 수 있습니다.
* 다음은 앞의 코드와 동일한 레이아웃을 AnkoComponent 없이 구성하는 예입니다. 이 방식으로 화면을 구성하는 경우 setContentView()를 호출하지 않아도 됩니다.
~~~kotlin
class MainActivity: AppcompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContentView()가 없어도 됩니다.
        verticalLayout {
            padding = dip(12)

            textView("Enter Login Credentials")

            editText {
                hint = "E-mail"
            }

            editText {
                hint = "Password"
            }

            button("Submit")
        }
    }
}
~~~
## 프래그먼트에서 사용하기
* 프래그먼트에서 Anko Layouts을 사용하려면 프래그먼트를 위한 AnkoComponent를 만들고, onCreateView()에서 createView()를 직접 호출하여 프래그먼트의 화면으로 사용할 뷰를 반환하면 됩니다.
* createView()를 직접 호출하려면 AnkoContext 객체를 직접 만들어 인자로 전달하면 됩니다.
~~~kotlin
class MainFragment : Fragment()  {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?{
        // AnkoComponent.createView() 함수를 호출하여 뷰를 반환합니다.
        return  MainFragmentUI().createView(AnkoContext.create(context, this))
    }
}

// 프래그먼트를 위한 AnkoComponent를 만듭니다.
class MainFragmentUI : AnkoComponent<MainFragment>  {
    override fun createView(ui: AnkoContext<MainFragment>)  = ui.apply  {
        verticalLayout {

            textView("Enter Login Credentials")

            editText {
                hint = "E-mail"
            }

            editText {
                hint = "Password"
            }

            button("Submit")
        }
    }.view
}
~~~
## Anko Support Plugin
* Anko Support Plugin은 Anko와 같이 사용할 수 있는 부가 기능을 제공하는 IDE 플러그인입니다. 
* 플러그인을 설치하려면 코틀린 IDE 플러그인을 설치하는 과정과 동일하게 진행하면 되며, 플러그인  검색 다이얼로그에서 다음과 같이 'Anko Support'를 선택하여 설치하면 됩니다.

* Anko Support Plugin에서는 AnkoComponent로 작성한 화면이 어떻게 표시되는지 미리 확인할 수 있는 레이아웃 프리뷰 기능을 제공합니다.
* 레이아웃 프리뷰를 사용하려면, 먼저 프리뷰 기능으로 확인하고 싶은 AnkoComponent가 구혀되어 있는 파일을 연 후 AnkoComponent의 구현부 내부 아무곳에 커서를 둡니다.
* 그 다음, [View > Tools Windows > Anko Layout Preview]를  선택하여 레이이웃 프리뷰 창을 띄웁니다.
* 레이아웃 프리뷰 창은 XML 레이아웃 프리뷰 창과 거의 유사한 형태로 구성되어 있습니다.
* 앞에서 선택한 AnkoComponent의 레이아웃 프리뷰를 보여주며, 화면이 표시되지 않거나 바뀐 내용이 반영되지 않았다면 프로젝트를 다시 빌드하면 됩니다.