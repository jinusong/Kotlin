#Let's Introduce Kotlin
## Kotlin 특징을 알아보자!

### 간결한 문법

* Kotlin은 Java보다 더 간결한 문법을 가졌습니다.
* ;(세미콜론) X
* 객체 생성시 new 키워드 X
* 타입 추론 덕분에 일반적인 타입 작성 X
### 널 안정성
* Kotlin은 객체 타입의 변수에서 널(null) 값의 허용여부를 구분합니다.
* 이 여부를 컴파일 단계에서 검사하여 런타임시의 오류를 대폭 줄일 수 있습니다.

```
// 널 값을 허용하는 문자열 타입(String?)
var foo: String? = null

// 널 값을 허용하지 않는 문자열 타입(String)
var bar: String = "bar"
``` 

### 가변/불변 구분
#### 일반 변수
* Kotlin은 변수 및 변수 내 할당된 불변 여부(최초 할당 후 변경이 가능한가?)를 구분합니다.
* val: Java에서 final을 사용한 변수와 같습니다.
* var: Java에서 final을 사용하지 않은 변수와 같습니다.
```
// String 타입의 값 foo를 선언합니다.
// 자바의 final String foo = "foo";와 동일합니다.
val foo: String = "Foo"

// 컴파일 에러: 값이 한번 할당되면 다른 값을 할당할 수 없습니다.
foo = "foo"

// String 타입의 변수 bar를 선언합니다.
// 자바의 String bar = "Bar";와 동일합니다.
var bar: String = "Bar"

// 성공: var로 선언되었기 때문에 얼마든지 다른 값을 할당할 수 있습니다.
bar = "bar"
```
#### 컬렉션 자료형
* 컬렉션 자료형에 대해서도 가변/불변 여부를 구분합니다.
* 객체에 할당된 값이 아닌 컬렉션 내 포함된 자료들을 추가하거나 삭제할 수 있는 여부를 구분합니다.
* 자료의 가변/불변 여부는 인터페이스로 구분합니다. (불변 인터페이스의 경우 삽입/삭제/수정을 위한 함수가 없습니다.)
```
// 자료를 변경할 수 없는 리스트 생성
val immutable: List<String> = listOf("foo", "bar", "baz")

// 컴파일 에러: add() 함수가 정의되어 있지 않습니다.
immutable.add("Foo")

// 자료를 변경할 수 있는 리스트 생성
val mutable: MutableList<String> = mutableListOf("foo", "bar", "baz")

// 성공: MutableList에는 자료를 수정할 수 있는 함수가 정의되어 있습니다.
mutable.add("Foo")
```

### 람다 표현식 지원
* Android는 개발 환경의 제약으로 람다 표현식을 사용하기 어려웠지만 Kotlin은 기본으로 람다 표현식을 지원합니다.
* 특별한 제약 없이 코드를 더 간소화 할 수 있습니다.
* Java로 작성된 인터페이스에 한해 Single Abstract Method 변환을 지원합니다.
* 그래서 함수의 인자로 전달되는 인터페이스의 인스턴스를 람다식으로 표현이 가능합니다.
```
val view = ...
// SAM 변환을 통해 OnClickListener
// 인터페이스의 인스턴스를 람다식으로 표현합니다.
view.setOnClickListener {
    Toast.makeText(it.context, "Click", Toast.LENGTH_SHORT).show()
}
```

### 스트림 API 지원
* Kotlin에서는 Stream API와 유사한 함수들을 기본 라이브러리에서 지원하기 때문에 컬렉션 내의 자료를 다루기 용이합니다.
(모든 Android version, platform에서 사용 가능)
```
val items = listOf(10, 2, 3, 5, 6)
// 리스트 내 항목 중 짝수의 합을 구합니다.
val sumOfEvens = items.filter { it % 2 == 0 }.sum()
```

### 완벽한 자바 호환성
* 개발을 할 시에 Kotlin을 쓰다 원하는 부분만 Java로 코드 작성이 가능합니다.
* 따라서 기존의 자바 코드를 오랜 시간에 걸쳐 바꾸는 것이 가능합니다.
* 코드 뿐만 아니라 라이브러리와도 모두 호환됩니다. (자바 기반 환경을 그대로 사용가능)
* 대부분 혼용 가능하지만 사용 시 유의해야할 부분이나 살짝 다른 부분들은 존재합니다.