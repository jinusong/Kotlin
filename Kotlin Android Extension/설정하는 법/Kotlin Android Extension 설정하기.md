# Kotlin Android Extension 설정하기
## kotlin-android-extensions 플러그인 적용
* Kotlin Android Extension을 사용하려면 이를 사용할 모듈의 빌드스크립트에 kotlin-android-extensions 플러그인을 적용해야합니다.
* 플러그인을 적용하기 위해 kotlin-android 플러그인도 함께 적용합니다.
~~~gradle
apply plugin: 'com.android.application'
apply plugin: 'kotlin-android'

// 코틀린 안드로이드 익스텐션을 이 모듈에 적용합니다.
apply plugin: 'kotlin-android-extensions'

android {
    ...
}

dependencies {
    ...
    implementation "org.jetbrains.kotlin:kotlin-stdlib:${kotlinVersion}"
}
~~~
## 사용 범위
* Kotlin Android Extension은 현재 액티비티, 프래그먼트, 리사이클러뷰에서 사용할 수 있습니다.
* 컴포넌트에서 사용하는 레이아웃에 포함된 뷰를 프로퍼티처럼 사용할 수 있도록 지원하며, 각 뷰의 ID를 프로퍼티 이름으로 사용합니다.

## import 추가
* 레이아웃 내 선언된 뷰를 프로퍼티처럼 사용하기 위해, 코틀린 안드로이드 익스텐션을 사용하는 컴포넌트에서는 특별한 import문을 추가 해야합니다.
~~~kotlin
import kotlinx,.android.synthetic.{sourceSet}.{layout}.*
~~~
* src/main/res/layout/activity_main.xml 레이아웃을 사용하는 경우를 예로 들어 봅시다.
* 이 파일은 main 소스 셋(Source Set)에 포함되어 있고 activity_main이라는 이름을 가지고 있습니다.
* 따라서 Kotlin Android Extension을 통해 이 레이아웃 내 뷰를 사용하는 컴포넌트에서는 import 문을 추가해야 합니다.
~~~kotlin
import kotlinx.android.synthetic.main.activity_main.*
~~~
* import 문이 다소 복잡해 보이지만 Kotlin Android Extension 또한 자동완성을 지원합니다.
* 일반적인 경우 다음과 같이 뷰의 이름을 입력하기 시작하면 사용할 수 있는 뷰의 목록이 표시되며 이들 중 하나를 선택하면 자동으로 import 문이 추가됩니다.
