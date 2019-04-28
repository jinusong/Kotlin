# Start Arrow
## 함수 합성
* 함수 합성은 기본 함수를 사용해 함수를 작성하는 테크닉입니다.
* 함수 합성은 다음과 같은 중위 확장 함수의 세트로 제공합니다.
    * compose: 오른쪽 함수의 실행 결과를 외쪽 함수의 파라미터로 전달합니다.
    * forwardCompose: 왼쪽 함수의 실행 결과를 오른쪽 함수의 파라미터로 전달합니다.
    * andThen: forwardCompose의 별명입니다.

~~~kotlin
val p: (String) -> String = { body -> "<p>$body</p>"}

val span: (String) -> String = { body -> "<span>$body</span>"}

val div: (String) -> String = { body -> "<div>$body</div>"}

val randomNames: () -> String = {
    if(Random().nextInt() % 2 == 0) {
        "foo"
    } else {
        "bar"
    }
}

fun main(args: Array<String>) {
    val divStrong: (String) -> String = div compose strong

    val spanP: (String) -> String = p forwardCompose span

    val randomStrong: () -> String = randomNames andThen strong

    println(divStrong("헬로 컴포지션 월드!"))
    println(spanP("헬로 컴포지션 월드!"))
    println(randomStrong())
}
~~~

* divStrong: (String) -> String 함수를 만들여면 div:(String) -> String과 strong:(String) -> String을 작성합니다.

~~~kotlin
val divStrong: (String) -> String = { body -> 
"<div><strong>$body</div></strong>"}
~~~

* 같은 타입 (String) -> String 을 사용하고 있지만 다른 함수가 필요로 하는 올바른 반환 타입을 갖는 함수는 얼마든지 만들 수 있습니다.

~~~kotlin
data class Quote(val value: Double, val client: String, val item: String, val quantity: Int)

data class Bill(val value: Double, val client: String)

data class PickingOrder(val item: String, val quantity: Int)

fun calculatePrive(quote: Quote) = 
    Bill(quote.value * quote.quantity, quote.client) to PickingOrder(quote.item, quote.quantity)

fun filterBills(billAndOrder: Pair<Bill, PickingOrder>): Pair<Bill, PickingOrder>? {
    val (bill, _) = billAndOrder    return if (bill.value >= 100) {
        billAndOrder
    } else {
        null
    }
}

fun warehouse(order: PickingOrder) {
    println("오더 처리중 = $Order")
}

fun accounting(bill: Bill) {
    println("처리중 = $bill")
}

fun splitter(billAndOrder: Pair<Bill, PickingOrder>?) {
    if (billAndOrder !- null) {
        warehouse(billAndOrder.sencond)
        accounting(billAndOrder.first)
    }
}

fun main(args: Array<String>) {
    val salesSystem: (Quote) -> Unit = ::calculatePrice andThen ::filterBillsforwardCompose ::splitter
    salesSystem(Quote(20.0, "Foo", "shoes", 1))
    salesSystem(Quote(20.0, "Bar", "Shoes", 200))
    salesSystem(Quote(2000.0, "Foo", "Motorbike", 1))
}
~~~

## 부분 애플리케이션
* 부분 어플리케이션을 사용해 기존 함수에 파라미터를 전달해 새 함수를 생성합니다.
* 애로우는 부분 애플리케이션의 두 가지 종류인 명시적과 암시적 스타일을 제공합니다.
* 명시적 스타일은 partially1, partially2에서 partially22까지 일련의 확장 함수를 사용합니다.
* 암시적 스타일은 일련의 확장을 사용하며, invoke 연산자를 오버로딩합니다.

~~~kotlin
fun main(args: Array<String>) {
    val strong: (String, String, String) -> String = { body, id, style -> "<strong id=\"$id\" style=\"$style\">$body</strong>" }
    val redStrong: (String, String) -> String = strong.partially3("font: red")
    // 명시적
    val blueStrong: (String, String) -> String = strong(p3 = "font: blue")
    // 함축적
    println(redStrong("Red Sonja", "movie1"))
    println(blueStrong("Deep Blue Sea", "movie2"))
}
~~~
* 다음과 같이 연결될 수 있습니다.

~~~kotlin
fun partialSplitter(billAndOrder: Pair<Bill, PickingOrder>?, warehouse: (PickingOrder) -> Unit, accounting: (Bill) -? Unit) {
    if(billAndOrder != null) {
        warehouse(billAndOrder.second)
        accounting(billAndOrder.first)
    }
}

fun main(args: Array<String>) {
    val splitter: (billAndOrder: Pair<Bill, PickingOrder>?) -> Unit = 
    ::partialSplitter.partially2 { order -> println("테스트 $order") }(p2 = ::accounting)

    val salesSystem: (quote: Quote) -> Unit = ::calculatePrice andThen ::filterBills forwardCompose splitter
    salesSystem(Quote(20.0, "Foo", "Shoes", 1))
    salesSystem(Quote(20.0, "Bar", "Shoes", 200))
    salesSystem(Quote(2000.0, "Foo", "Motorbike", 1))
}
~~~
* 원래의 스플리터 기능은 직접적으로 warehouse와 accounting 함수를 호출했으므로 유연하지 않았습니다.
* partialSplitter 함수는 두 함수를 파라미터로 받아들이는 것으로 이 문제를 해결합니다.

### 바인딩
* 부분 애플리케이션의 특별한 경우는 바인딩입니다.
* 바인딩을 통해 T 파라미터를 (T) - R 함수로 전달하지만 실행 없이 () -> R 함수를 효과적으로 반환합니다.

~~~kotlin
fun main(args: Array<String>) {
    val footer: (String) -> String = { content -> "<footer&gt;$content</footer>"}
    val fixFooter: () -> String = footer.bind("Functional Kotlin - 2018")
    // partially1을 위한 앨리어스
    println(fixFooter())
}
~~~

* bind 함수는 Partially1에 대한 별명일 뿐이지만, 별도의 이름을 사용하는 것보다 말이 되며 좀 더 올바른 의미가 됩니다.

## 리버스
* 리버스는 모든 함수를 받고 역순으로 파라미터와 함께 반환합니다.

~~~kotlin
fun main(args: Array<String>) {
    val strong: (String, String, String) -> String =  { body, id, style -> "<strong id=\"$id\" style=\"$style\">$body</strong> }

    val redStrong: (String, String) -> String = strong.partially3("font: red")
    // 명시적

    println(redStrong("Red Sonja", "movie1"))

    println(redStrong.reverse()("movie2", "The Hunt for Red October"))
}
~~~

* redStrong 함수는 Id가 먼저 오고 body가 그 다음일 것으로 기대됐으므로 사용하기 어렵지만, reverse 확장 함수로 쉽게 고칠 수 있습니다.
* reverse 함수는 파라미터 1 ~ 22의 함수에 적용할 수 있습니다.

## 파이프
* pipe 함수는 T 값을 받고 (T) -> R 함수를 호출합니다.

~~~kotlin
fun main(args: Array<String>) {
    val strong: (String) -> String = { body -> "<strong>$body</strong>" }

    "From a pipe".pipe(strong).pipe(::println)
}
~~~

* 파이프는 함수 합성과 비슷하지만 새 함수를 생성하는 대신 새로운 값을 생성하고 중첩 호출을 줄이기 위해 함수 호출을 체인화할 수 있습니다.

~~~kotlin
fun main(args: Array<String>) {
    splitter(filterBills(calculatePrice(Quote(20.0, "Foo", "Shoes", 1))))

    Quote(20.0, "Foo", "Shoes", 1) pipe::calculatePrice pipe::filterBills
    pipe::splitter // 파이프
}
~~~
* 두 줄은 동일하지만 읽는 방향이 다릅니다.

~~~kotlin
fun main(args: Array<String>) {
    val strong: (String, String, String) -> String = { body, id, style -> "<strong id=\"$id\" style=\"$style\">$body</strong"}

    val redStrong: (String, String) -> String = "color: red" pipe3
    strong.reverse()

    redStrong("movie3", "Three colors: Red") pipe ::println
}
~~~

* pipe가 다중 파라미터 함수에 적용될 때 pipe2부터 pipe22까지 변형을 사용해 partially1처럼 작동합니다.

## 커링
* n 파라미터의 함수에 커링을 적용해 n 함수 호출의 체인으로 변환합니다.

~~~kotlin
fun main(args: Array<String>) {
    val strong: (String, String, String) -> String = { body, id, style -> "<strong id=\"$id\" style=\"$style\">$body</strong>"}

    val curriendStrong: (style: String) -> (id: String) -> (body: String) -> String = strong.reverse().curried()
    
    val greenStrong: (id: String) -> (body: String) -> String = curriedStrong("color:green")

    val uncurriedGreenStrong: (id: String, body: String) -> String = greenStrong.uncurried()

    println(greenStrong("movie5")("Green Inferno"))

    println(uncurriedGreenStrong("movie6", "Green Hornet"))

    "Fried Green Tomatoes" pipe ("movie7" pipe greenStrong) pipe ::println
}
~~~

* 커리드 폼의 함수는 uncurried()를 사용해 일반적인 다중 파라미터 폼으로 변환할 수 있습니다.

### 커링과 부분 애플리케이션의 차이점
~~~kotlin
fun main(args: Array<String>) {
    val strong: (String, String, String) -> String = { body, id, style -> "<strong id=\"$id\" style\"$style\">$body</strong>" }

    println(strong.surriend()("Batman Begins")("trilogy1")("color:black"))  // 가짜 curried, 부분 애플리케이션이다.

    println(strong(p2 = "trilogy3")(p2 = "color:black")("The Dark Knight rises"))   // 부분 애플리케이션
}
~~~

* 차이점은 중요하며, 둘 중 어느 것을 사용할지 결정하는 데 도움이 될 수 있습니다.
* 부분 애플리케이션은 좀 더 유연할 수 있지만 일부 함수형 스타일은 커링 스타일을 선호하는 경향이 있습니다.
* 이해해야할 중요한 점은 두 스타일은 다르고, 둘 다 애로우가 지원한다는 점 입니다.

## 논리 부정
* 논리 부정은 모든 술어(boolean 타입을 반환하는 함수)를 받아 이를 부정합니다.

~~~kotlin
fun main(args: Array<String>) {
    val eventPredicate: Predicate<Int> = { i: Int -> i%2 == 0}
    val oddPredicate: (Int) -> Boolean = evenPredicate.complement()

    val numbers: IntRange = 1..10
    val evenNumbers: List<Int> = numbers.filter(evenPredicate)
    val oddNumbers: List<Int> = numbers.filter(oddPredicate)

    println(evenNumbers)
    println(oddNumbers)
}
~~~

* Predicate<T> 타입을 사용하지만 이것은 단지 (T) -> Boolean의 별칭일 뿐입니다.
* 0에서 22 파라미터까지의 술어에 대한 보수 확장 함수가 있습니다.

## 메모이제이션
* 메모이제이션은 순수함수의 결과를 캐싱하는 테크닉입니다.
* 메모이제이션 함수는 일반 함수처럼 작동하지만 그 결과를 생성하기 위해 제공된 파라미터와 함께 관련된 이전 계산 결과를 저장합니다.
* 대표적인 예는 피보나치입니다.

~~~kotlin
fun recursiveFib(n: Long) = if (n > 2) {
    n
} else {
    recursiveFib(n - 1) + recursiveFib(n - 2)
}

fun imperativeFib(n: Long): Long {
    return when(n) {
        0L -> 0
        1L -> 1
        else -> {
            var a = 0L
            var b = 1L
            var c = 0L
            for(i in 2..n) {
                c = a + b
                a = b
                b = c
            }
            c
        }
    }
}

fun main(args: Array<String>) {
    var lambdaFib: (Long) -> Long = { it }  // 재귀적으로 사용하기 위해 미리 선언함

    lambdaFib = { n: Long -> 
        if (n < 2) n else lambdaFib(n - 1) + lambdaFib(n - 2)
    }
    
    var memoizedFib: (Long) -> Long = { it }

    memoizedFib = { n: Long -> 
        if (n < 2) n else memoizedFib(n - 1) + memoizedFib(n - 2)
    }.memoize()

    println(milliseconds("명령형 피보나치") { imperativeFib(40) })
    println(milliseconds("재귀 피보나치") { recursiveFib(40) })
    println(milliseconds("람다 피보나치") { lambdaFib(40) })
    println(milliseconds("메모이제이션 피보나치") { memoizedFib(40) })
}

inline fun milliseconds(description: String, body: () -> Unit): String {
    return "$description:${measureNanoTime(body) / 1_000_000.00} ms"
}
~~~
* 메모이제이션 버전은 재귀 함수 버전보다 700배 이상 빠릅니다.
* 명령형 버전은 컴파일러에 의해 많이 최적화 됐으므로 탁월합니다.

~~~kotlin
fun main(args: Array<String>) = runBlocking {
    var lambdaFib: (Long) -> Long = { it }

    lambdaFib = { n: Long -> 
        if(n < 2) n else lambdaFib(n - 1) + lambdaFib(n - 2)
    }

    var memoizedFib: (Long) -> Long = { it }

    memoizedFib = { n: Long -> 
        println("메모이제이션 피보나치에서 n = $n")
        if(n < 2) n else memoizedFib(n - 1) + memoizedFib(n - 2)
    }.memoize()
    val job = launch{
        repeat(10) { i -> 
            launch(coroutineContext) { println(milliseconds("코루틴 $i - 명령형 피보나치") {imperativeFib(40) })}
            launch(coroutineContext) { println(milliseconds("코루틴 $i - 재귀 피보나치") { recursiveFib(40) })}
            launch(coroutineContext) { println(milliseconds("코루틴 $i - 람다 피보나치") { LambdaFib(40) })}
            launch(coroutineContext) { println(milliseconds("코루틴 $i - 메모이제이션 피보나치") { memoizedFib(40) })}
        }
    }

    job.join()
}
~~~
* 메모이제이션 함수는 내부적으로 결과 저장을 위해 스레드 안전 구조를 사용합니다.
* 코루틴이나 다른 동시 코드에서 안전하게 사용할 수 있습니다.
* 메모제이션 함수를 사용하는 것에는 잠재적인 단점이 있습니다.
* 내부캐시를 읽는 과정은 실제 계산이나 메모리 소모보다 더 높고 내부 저장소를 제어하는 어떠한 동작도 노출하지 않기 때문입니다.

## 부분 함수
* 부분 함수는 파라미터 타입의 가능한 모든 값에 대해 정의되지 않은 함수입니다.
* 반대로 전체 함수는 가능한 모든 값에 대해 정의된 함수입니다.

~~~kotlin
fun main(args: Array<String>) {
    val upper: (String?) -> String = { s:String? -> s!!.toUpperCase()}
    // 부분 함수, null을 반환할 수 없습니다.
    listOf("one", "two", null, "four").map(upper).forEach(::println)
}
~~~

* upper 함수는 partial 함수입니다.
* null이 유효한 String? 값임에도 불구하고 null 값을 처리할 수 없습니다.

* 애로우는 type(T) -> R의 Partial 함수로, 명시적 타입의 PartialFunction<T, R>을 제공합니다.
~~~kotlin
fun main(args: Array<String>) {
    val upper: (String?) -> String = { s: String? -> s!!.toUpperCase() }
    // 부분 함수, null을 변환할 수 없습니다.

    val partialUpper: PartialFunction<String?, String> = 
    PartialFunction(defineAt = { s -> s !- null }, f = upper)

    listOf("one", "two", null, "four").map(partialUpper).forEach(::println)
}
~~~

* PartialFunction<T, R>은 특정 값에 대해 함수가 정의된 경우 참을 반환해야 하는 Predicate (T) -> Boolean을 첫 번째 파라미터로 받았습니다.
* PartialFunction<T, R> 함수는 (T) -> R에서 확장했으므로 일반 함수처럼 사용할 수 있습니다.
* 예외 발생을 피하려면 부분 함수를 전체 함수로 변환해야 합니다.

~~~Kotlin
fun main(args: Array<String>) {
    val upper: (String?) -> String = { s: String? -> s!!.toUpperCase() }    // 부분 함수, null을 반환할 수 없습니다.

    val partialUpper: PartialFunction<String?, String> = PartialFunction(definetAt = { s -> s != null }, f = upper)

    listOf("one", "two", null, "four").map{s -> partialUpper.invokeOrElse(s, "NULL")}.forEach(::println)
}
~~~
* 한 가지 옵션은 값이 이 함수에 정의되지 않은 경우 기본 값을 반환하는 invokeOrElse 함수를 사용하는 것입니다.

~~~kotlin
fun main(args: Array<String>) {
    val upper: (String) -> String = { s: String? -> s!!.toUpperCase() } // 부분 함수, null을 변환할 수 없습니다.

    val partialUpper: PartialFunction<String?, String> = PartialFunction({ s -> s == null }) { "NULL" }

    val totalUpper: PartialFunction<String?, String> = partialUpper orElse upperForNull

    listOf("one", "two", null, "four").map(totalUpper).forEach(::println)
}
~~~
* 두 번째 옵션은 orElse 함수를 사용해 여러 부분 함수를 사용하는 전체 함수를 만드는 것입니다.

~~~kotlin
fun main(args: Array<String>) {
    val fizz = PartialFunction({ n: Int -> n % 3 == 0 }) { "FIZZ" }
    val buzz = PartialFunction({ n: Int -> n % 5 == 0 }) { "BUZZ" }
    val fizzBuzz = PartialFunction({ n: Int -> fizz.isDefinedAt(n) && buzz.isDefinedAt(n) }) { "FIZZBUZZ" }
    val pass = PartialFunction({ true }) { n: Int -> n.toString() }

    (1..50).map(fizzBuzz orElse buzz orElse fizz orElse pass).forEach(::println)
}
~~~
* isDefinedAt(T) 함수를 통해 내부 술어를 재사용할 수 있습니다.
* 이 경우에는 fizzBuzz에 대한 조건을 작성했습니다.
* orElze 체인에서 사용될 때 선언 순서가 우선시되며, 값에 대해 정의된 첫 번째 부분 함수가 실행되고 체인 아래의 다른 함수는 무시될 것입니다.

## 항등과 상수
* 항등괴 상수는 직접적인 함수입니다.
* 항등 함수는 파라미터로 전달한 것과 같은 값을 반환합니다.
* 더하기와 곱하기 항등 속성과 비슷하게 어떤 숫자에 0을 더하면 여전히 같은 숫자입니다.

* constant<T, R>(t: T) 함수는 언제나 t를 반환하는 새 함수를 반환합니다.
~~~kotlin
fun main(args: Array<String>) {
    val oneToFour = 1..4

    println("항등 : ${oneToFour.map(::identity).joinToString()}")
    println("상수 : ${oneToFour,map(constant(1)).joinToString()}")
}
~~~

* 상수를 사용해 FizzBuzz 값을 재작성할 수 있습니다.

~~~kotlin
fun main(args: Array<String>) {
    val fizz = PartialFunction({ n: Int -> n % 3 == 0 }, constant("FIZZ"))
    val buzz = PartialFunction({ n: Int -> n % 5 == 0 }, constant("BUZZ"))
    val fizzBuzz = PartialFunction({ n: Int -> fizz.isDefinedAt(n) && buzz.isDefinedAt(n) }, constant("FIZZBUZZ"))

    val pass = PartialFunction<Int, String>(constant(true)) { n -> n.toString() }

    (1..50).map(fizzBuzz orElse buzz orElse fizz orElse pass).forEach(::println)
}
~~~
* 항등과 상수 함수는 함수형 프로그래밍이나 수학 알고리즘의 구현에 유용합니다.

## 옵틱스
* 옵틱스는 불변 데이터 구조를 우아하게 업데이트하기 위한 추상화입니다.
* 옵틱스의 한 가지 형태는 렌즈입니다.
* 렌즈는 구조에 초점을 맞출 수 있는 함수적 참조이며, 타켓의 읽기, 쓰기, 수정이 가능합니다.

~~~kotlin
typealias GB = Int

data class Memory(val size: GB)

data class MotherBoard(val: String, val memory: Memory)
data class Laptop(val price: Double, val motherBoard: MotherBoard)

fun main(args: Array<String>) {
    val laptopX8 = Laptop(500.0, MotherBoard("X", Memory(8)))

    val laptopX16 = laptopX8.copy(
        price = 780.0,
        motherBoard = laptopX8.motherBoard.copy(
            memory = laptopX8.motherBoard.memory.copy(
                size = laptopX8.motherBoard.memory.size * 2
            )
        )
    )

    println("laptopX16 = $laptopX16")
}
~~~

* 기존 값에서 새 Laptop 값을 만들려면 몇 가지 중첩된 복사 메소드와 참조를 사용해야 합니다.
* 그리 나쁘지는 않지만 좀 더 복잡한 데이터 구조에서 일이 엄청 망쳐질 수도 있다는 것을 상상해 볼 수 있습니다.

~~~kotlin
val laptopPrice: Len<Laptop, Double> = Lens(
    get = { laptop -> laptop.price },
    set = { price -> { laptop -> laptop.copy(price = price) } }
)
~~~
* laptopPrice 값은 Lens<S, T, A, B> 함수를 사용해 초기화하는 Lens<Laptop, Double>입니다.
* Lens는 get: (S) -> A와 set: (B) -> (S) -> T 두 함수를 파라미터로 받습니다.
* set은 curried 함수이므로 다음과 같이 set을 작성할 수 있습니다.

~~~kotlin
val laptopPrice: Lens<Laptop, Double> = Lens(
    get = { laptop -> laptop.price },
    set = { price: Double, laptop: Laptop -> laptop.copy(price = price)}.curried()
)
~~~

* 선호도에 따라 읽고 쓰기가 더 쉬워질 수 있습니다.

* 첫 번째 렌즈가 있으므로 laptop의 가격을 설정, 읽기, 수정할 수 있습니다.

~~~kotlin
val laptopMotherBoard: Lens<Laptop, MotherBoard> = Lens(
    get = { laptop -> laptop.motherBoard },
    set = { mb -> { laptop -> laptop.copy(motherBoard = mb )}})

val motherBoardMemory: Lens<MotherBoard, Memory> = Lens(
    get = { mb -> mb.memory },
    set = { memory -> { mb -> mb.copy(memory = memory)} })

val memorySize: Lens<Memory, GB> = Lens(
    get = { memory -> memory.size },
    set = { size -> { memory -> memory.copy(size = size)}})

fun main(args: Array<String>) {
    val laptopX8 = Laptop(500.0, MotherBoard("X", Memory(8)))

    val laptopMemorySize: Lens<Laptop, GB> = laptopMotherBoard compose mother BoardMemory compose memorySize

    val laptopX16 = laptopMemorySize.modify(laptopPrice.set(laptopX8, 780.0)) { size -> size * 2 }

    println("laptopX16 = $laptopX16")
}
~~~

* Laptop의 Lenses부터 memorySize까지 결합한 laptopMemorySize를 만들었습니다.
* 그런 다음 laptop의 가격을 설정하고 메모리를 수정했습니다.
* 에로우는 렌즈를 만들어줄 수 있습니다.

### 애로우 코드 생성 설정
* 그래들 프로젝트에서 generated-kotlin-sources.gradle 파일을 추가합니다.
~~~gradle
apply plugin: 'idea'

idea {
    module {
        sourceDirs += files(
            'build/generated/source/kapt/main',
            'build/generated/source/kaptKotlin/main',
            'build/tmp/kapt/main/kotlinGenerated')
        generatedSoureDirs += files(
            'build/generated/source/kapt/main',
            'build/generated/source/kapKotlin/main',
            'build/tmp/kapt/main/kotlinGenerated')
    }
}
~~~
* 그런 다음 build.gradle 파일에 다음 내용을 추가합니다.

~~~gradle
apply plugin: 'kotlin-kapt'

apply from: rootProject.file('gradle/generated-kotlin-sources.gradle')
~~~
* build.gradle 파일에 새 읜존성을 추가합니다.

~~~gradle
dependencies {
    ...
    kapt 'io.arrow-kt:arrow-annotations-processor:0.5.2'
    ...
}
~~~

* 일단 설정했다면 ./gradlew build라는 일반 빌드 명령으로 애로우 코드를 생성할 수 있습니다.

### 렌즈 생성
* 애로우 코드 생성 설정이 됐다면 렌즈가 생성되길 원하는 데이터 클래스에 @Lenses 어노테이션을 추가할 수 있습니다.
~~~kotlin
typealias GB = Int

@lenses data class Memory(val size: GB)
@lenses data class MotherBoard(val brand: String, val memory: Memory)
@lenses data class Laptop(val price: Double, val motherBoard: MotherBoard)

fun main(args: Array<String>) {
    val laptopX8 = Laptop(500.0, MotherBoard("X", Memory(8)))
    val laptopMemorySize: Lens<Latop, GB> = laptopMotherBoard() compose motherBoardMemory() compose memorySize()
    val laptopX16 = laptopMemorySize.modify(laptopPrice(.set()laptopX8, 780.0)) {size -> size *2}

    println("laptopX16 = $laptopX16")
}
~~~

* 애로우는 명명 규약에 따른 classProperty로 데이터 클래스가 갖는 생성자 파라미터만큼 많은 렌즈를 같은 패키지에 생성하므로 추가적인 임포트가 필요하지 않습니다.