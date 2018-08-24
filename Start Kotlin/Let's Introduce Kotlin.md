#Let's Introduce Kotlin
## 특징
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
