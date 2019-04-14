# Functional Programing

* 함수형 프로그래밍은 하나의 패러다임입니다.
* 본질적으로 표현식으로 데이터를 변환하는 것입니다.

* 함수형 프로그래밍 스타일은 다음과 같은 이점을 제공합니다.
    * 코드는 읽기 쉽고 테스트하기 쉽습니다. 외부 가변 상태에 의존하지 않는 함수는 더 쉽게 접근할 수 있고, 더 쉽게 증명할 수 있습니다.
    * 상태와 부수 효과가 주의 깊게 계획됩니다. 상태 관리를 코드에 개별적으로 특정 위치로 제한하는 것은 유지와 리팩토링을 쉽게 만듭니다.
    * 동시성이 좀 더 안전해지며 더 자연스러워집니다. 가변 상태가 없다는 것은 동시성 코드가 코드 주변에서 잠금이 적거나 필요 없다는 것을 뜻합니다.

## 기본 개념
### 일급 함수 및 고차함수
* 함수형 프로그래밍의 가장 기본적인 컴셉은 일급함수입니다.
* 일급함수를 지원하는 프로그래밍 언어는 함수를 다른 타입으로 취급합니다.
* 함수를 변수, 파라미터, 반환, 일반화 ㅏ입 등으로 사용할 수 있게 합니다.
* 파라미터와 반환에 대해 말하자면 다른 함수를 사용하거나 반환하는 함수가 고차함수입니다.

* 한번에 이해해봅시다
~~~kotlin
val capitalize = { str: String -> str.capitalize() }

fun main(args: Array<String>) {
    println(capitalize("헬로 월드!"))
}
~~~
* 이해 되죠?
* capitalize 람다 함수는 타입이 (String) -> String입니다.
* 즉, capitalize는 String을 받아 다른 String을 반환합니다.
* 이제 일반화를 적용해볼까요?

~~~kotlin
fun transform(str:String, fn: (String) -> String): String {
    return fn(str)
}
~~~
* transform(String, (String) -> String) 함수는 하나의 String을 받아 람다 함수를 적용합니다.
* 결과적으로 변환을 일반화할 수 있습니다.

~~~kotlin
fun <T> transform(t: T, fn: (T) -> T): T {
    return fn(t)
}
~~~
* 함수 호출을 해볼까요?

~~~kotlin
fun main(args: Array<String>) {
    println(transform("kotlin", capitalize))
}
~~~
* 좀 더 다양한 방법은?
* 일단 다음과 같은 함수가 있습니다.

~~~kotlin
fun reverse(str: String): String {
    return str.reversed()
}

fun main(args: Array<String>) {
    println(transform("kotlin", ::reverse))
}
~~~
* reverse는 함수입니다. 다음과 같이 더블 콜론을 사용해 참조를 전달할 수 있습니다.
* 또한 인스턴스나 컴패니언 오브젝트 메소드에 참조를 전달할 수도 있습니다.
* 그러나 가장 일반적인 경우는 람다를 직접 전달하는 것입니다.

* 그리고 함수가 마지막 파라미터로 람다를 받으면 람다는 괄호밖으로 전달될 수 있습니다.
~~~kotlin
fun main(args: Array<String>) {
    println(transform(("kotlin") {str -> str.substring(0..1)})
}
~~~

* 이 기능은 코틀린으로 도메인 특화 언어(DSL)를 생성할 수 있는 가능성을 열어줍니다.
* 한번 체험해볼까요? if의 반대인 unless를 만들어봅시다.
~~~kotlin
fun unless(condition: Boolean, block:() -> Unit) {
    if(!condition) block()
}

fun main(args: Array<String>) {
    val securitryCheck = false
    unless(securityCheck) {
        println("이 웹 사이트에 접근할 수 없습니다.")
    }
}
~~~

* 이제 타입 앨리어스는 함수와 혼합해 간단한 인터페이스를 대체하는 데 사용할 수 있습니다.
~~~kotlin
interface Machine<T> {
    fun process(product: T)
}

fun <T> useMachine(t: T, machine: Machine<T>) {
    machine.process(t)
}

class PrintMachine<T>: Machine<T> {
    override fun process(t: T) {
        println(t)
    }
}

fun main(args: Array<String>) {
    useMachine(5, PrintMachine())
    sueMachine(5, object: Machine<Int> {
        override fun process(t: Int) {
            println(t)
        }
    })
}
~~~

* 이것은 타입 앨리어스로 태체될 수 있으며, 모든 함수의 구문적 특징과 함께 사용될 수 있습니다.
~~~kotlin
typealias Machine<T> = (T) -> Unit

fun <T> useMachine(t: T, machine: Machine<T>) {
    machine(t)
}

class PrintMachine<T>: Machine<T> {
    override fun invoke(p1: T) {
        println(p1)
    }
}

fun main(args: Array<String>) {
    useMachine(5, PrintMachine())
    
    useMachine(5, ::println)
    useMachine(5) { i -> 
        println(i)
    }
}
~~~

### 순수 함수
* 순수 함수에는 부수 효과나 메모리, I/O가 없습니다.
* 순수 함수는 참조 투명도, 캐싱, 기타 다양한 속성을 가집니다.
* 코틀린에서 순수 함수를 작성하는 것은 가능하지만, 순수 함수형 프로그래밍을 실행하지 않습니다.
* 원한다면 순수한 함수형 스타일로 작성할 수 있는 기능을 포함한 대단한 유연성을 제공합니다.

### 재귀 함수
* 재귀함수는 실행을 멈추는 조건과 함께 스스로를 호출하는 함수입니다.
* 코틀린에서 재귀 함수는 스택을 유지하지만 tailrec 수정자를 통해 최적화할 수 있습니다.

~~~kotlin
fun tailrecFib(n: Long): Long {
    tailrec fun go(n: Long, prev: Long, cur: Long): Long {
        return if(n == 0L) {
            prev
        } else {
            go(n - 1, cur, prev + cur)
        }
    }

    return go(n, 0, 1)
}
~~~

## 함수적 컬렉션
* 함수적 컬렉션은 코차 함수를 통해 요소와 상호작용할 수 있는 방법을 제공하는 컬렉션입니다.
* 함수적 컬렉션은 필터, 맵, 폴드와 같은 이름과 함께 공통된 작업이 있습니다.
* 순수 함수형 언어에서 구현된 데이터 구조인 순수 함수적 데이터 구조와 혼동하지 말아야 합니다.
* 순수 함수적 데이터 구조는 변경 불가능하며, 느긋한 계산법과 다른 기능 테크닉을 사용합니다.

* 함수적 컬렉션은 순수 함수적 데이터 구조가 될 수는 있지만 반드시 그래야만 하는 것은 아닙니다.

