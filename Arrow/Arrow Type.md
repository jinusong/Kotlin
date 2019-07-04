# Arrow Type
* 애로우에서는 Option, Either, Try 같은 시존 함수형 타입의 많은 구현과 펑터, 모나드같은 다른 타입 클래스를 포함합니다.

## Option
* Option<T> 데이터 타입은 T값의 유무를 표현합니다.
* 애로우에서 Option<T>는 T 값의 존재를 나타내는 데이터 클래스 Some<T>와 값이 없음을 나타내는 오브젝트 None의 두 하위 타입으로 구성된 sealed 클래스입니다.
* sealed 클래스로 정의된 Option<T>는 다른 하위 타입을 가질 수 없습니다.
* 따라서 컴파일러는 철저하게 구문을 확인할 수 있습니다. 두 경우 모두 라면 Some<T>와 None이 다뤄집니다.

* Option은 null이 가능한 타입보다 훨씬 많은 값을 제공합니다.

~~~kotlin
fun divide(num: Int, den: Int): Int? {
    return if (num % den != 0) {
        null
    } else {
        num / den
    }
}

fun division(a: Int, b: Int, den: Int): Pair<Int, Int>? {
    val aDiv = divide(a, den)
    return when (aDib) {
        is Int -> {
            val bDiv = divide(b, den)
            when(bDiv) {

                is Int -> aDiv to bDiv
                eles -> null
            }
        }
        else -> null
    }
}
~~~

* division 함수는 정수 두 개와 분모라는 세 파라미터를 받고 두 숫자 모두 den으로 나눌 수 있다면 Pair<Int, Int>를 반환하고 그 외에는 null을 반환합니다.

~~~kotlin
fun optionDivide(num: Int, den: Int): Option<Int> = divide(num, den).toOption()

fun optionDivision(a: Int, b: Int, den: Int): Option<Pair<Int, Int>> {
    val aDiv = optionDivide(a, den)
    return when (aDiv) {
        is Some -> {
            val bDiv = optionDivide(b, den)
            when (bDiv) {
                is Some -> Some(aDiv.t to bDiv.t)
                else -> None 
            }
        }
        else -> None
    }
}
~~~

* optionDivide 함수는 나눗셈에서 null이 가능한 결과를 얻고 그것을 toOption() 확장 함수를 사용해 Option으로 잔환합니다.
* optionDivision과 division을 비교하면 큰 차이가 없으며, 다른 타입으로 표현된 같은 알고리즘입니다.
* 여기서 멈추면 Option<T>는 null이 가능한 값의 위에 추가적인 값을 제공하지 않습니다.

~~~Kotlin
fun flatMapDivision(a: Int, b: Int, den: Int): Option<Pair<Int, Int>> {
    return optionDivide(a, den).flatMap { aDiv: Int -> 
        optionDivide(b, den).flatMap { bDiv: Int -> 
            Some(aDiv to bDiv)
        }
    }
}
~~~

* Option은 내부 값을 처히는 몇 가지 함수를 제공합니다.
* 이 경우 모나드로서 flatMap이며, 이제 코드가 더 짧아 보입니다.

* Option<T> 함수의 일부입니다.
    * exists(p: Predicate<T>): Boolean: 값 T가 존재하면 predicate p 결과를, 그 외에는 null을 반환합니다.
    * filter(p: Predicate<T>): Option<T>: T 값이 존재하고 predicate P를 충족하면 Some<T>를, 그외에는 None을 반환합니다.
    * flatMap(f: (T) -> Option<T>): Option<T>: flatMap 변형 함수
    * <R> fold(ifEmpty: () -> R, some: (T) -> R): R<R>: R로 변환된 값을 반환하고 None에 대해 ifEmpty를, Some<T>에 대해 some을 호출합니다.
    * getOrElse(default:() -> T): T: T가 존재한다면 T를 반환하고 그 외에는 기본 결과를 반환합니다.
    * <R> map(f: (T) -> R): Option<T>: 펑터같은 트랜스폼 함수
    * orNull(): T?: 값 T를 null이 가능한 T?로 반환합니다.

* division의 마지막 구현은 컴프리헨션을 사용합니다.

~~~kotlin
fun comprhensionDivision(a: Int, b: Int, den: Int): Option<Pair<Int, Int>> {
    return Option.monad().binding {
        val aDiv: Int = optionDivide(a, den).bind()
        val bDiv: Int = optionDivide(b, den).bind()
        aDiv to bDiv
    }.ev()
}
~~~

* 컴프리헨션은 flatMap 함수가 포함된 모든 타입에 대해 순차적으로 계산하는 기술이며, 모나드의 인스턴스를 제공할 수 있습니다.
* 애로우에서 컴프리헨션은 코루틴을 사용합니다. 코루틴은 비동기 실행 도메인 밖에서 유용합니다.
* 다음은 내용을 요약한 코드입니다.

~~~kotlin
fun comprehensionDivision(a: Int, b: Int, den: Int): Option<Pair<Int, Int>> {
    return Option.monad().binding {
        val aDiv: Int = optionDivide(a, den).bind()
        // 컨티뉴에이션 1 시작
        val bDiv: Int = optionDivide(b, den).bind()
        // 컨티뉴에이션 2 시작
        aDiv to bDiv
        // 컨티뉴에이션 2 끝
        // 컨티뉴에이션 1 끝
    }.ev()
}
~~~

* Option.monad().binding은 코루틴 빌더이고, bind() 함수는 suspended 함수입니다.
* 컨티뉴에이션은 일시 중지 지점 이후의 모든 코드를 나타내는 것입니다.
* 예시 코드에서 두 개의 Suspension 포인트와 두 컨티뉴에이션이 있습니다.
* 반환할 때 두 번째 컨티뉴에이션이 있으며, aDiv와 bDiv에 접근할 수 있습니다.

* 이 알고리즘을 컨티뉴에이션으로 읽는 것은 flatMapDivision 함수와 매우 비슷합니다.
* 씬 뒤에서 Option.monad().binding은 컴프리헨션을 만들기 위해 컨티뉴에이션과 함께 Option.flatMap을 사용합니다.
* 컴파일되면 ComprehensionDivision과 flatMapDivision은 대략적으로 동일합니다.

## Either
* Either<L, R>은 가능한 두 값 L과 R 중 하나를 표시하지만 동시에 둘 다는 안됩니다.
* Either는 Left<L>과 Right<R> 서브타입 두 개를 가진 sealed 클래스입니다.
* 보통 Either는 실패할 수 있는 결과를 표현하기 위해 사용되며, 왼쪽은 에러 표시로 사용되고 오른쪽은 성고적인 결과 표시로 사용됩니다.
* 실패할 수 았눈 적업 표시는 일반적인 시나리오이므로 애로우의 Either는 오른쪽으로 편향돼 있습니다.
* 즉, 문서화되지 않았면 모든 작업은 오른쪽에서 실행됩니다.

* division을 Option에서 Either로 변환합니다.
~~~kotlin
fun eitherDivide(num: Int, den: Int): Either<String, Int> {
    val option = optionDivide(num, den)
    return when(option) {
        is Some -> Right(option.t)
        None -> Left("$num은 $den으로 나눌 수 없다.")
    }
}
~~~
* 이제 None 값을 반환하는 대신 유저에게 귀중힌 정보를 반환합니다.
~~~kotlin
fun eitherDivision(a: Int, b: Int, den: Int): Either<String, Tuple2<Int, Int>> {
    val aDiv = eitherDivide(a, den)
    return when (aDiv) {
        is Right -> {
            val bDiv = eitherDivide(b, den)
            when (bDiv) {
                is Right -> Right(aDiv.getOrElse { 0 } toT bDiv.getOrElse { 0 })
                is Left -> bDiv as Either<String, Nothing>
            }
        }
        is Left -> aDiv as Either<String, Nothing>
    }
}
~~~

* eitherDivision에서 코틀린의 Pair<A, B> 대신 애로우의 Tuple<A, B>를 사용합니다.
* 튜플은 Pair/Triple보다  많은 기능을 제공하며, 지금부터 이것을 사용할 것입니다.
* Tuple2를 만들려면 중위 확장 함수 toT를 사용합니다.

* flatMap, map, isLeft, isRight 등의 함수들이 있습니다.

* flatMap은 다음과 같이 작동합니다.
~~~kotlin
fun flatMapEitherDivision(a: Int, b: Int, den:  Int): Either<String, Tuple2<Int, Int>> {
    return  eitherDivide(a, den).flatMap { aDiv -> 
        eitherDivide(b, den).flatMap  { bDiv -> 
            Right(aDiv toT bDiv)
        }
    }
}
~~~
* Either는 모나드 구현을 가지므로 바인딩함수를 호출할 수 있습니다.

~~~Kotlin
fun comprehensionEitherDivision(a: Int, b: Int, den: Int): Either<String, Tuple2<Int, Int>> {
    return Either.monad<String>().binding {
        val aDiv = eitherDivide(a,  den).bind()
        val  bDiv = eitherDivide(b , den).bind()
        aDiv toT bDiv
    }.ev()
}
~~~
* Either.monad<L>()에 주의해야합니다.  Either<L, R>은 L 타입을 정의해야 합니다.

~~~kotlin
fun main(args: Array<String>) {
    eitherDivision(3, 2, 4).fold(::println, ::println) //3은 4로 나눠지지 않는다.
}
~~~

## 모나드 트랜스포머
* Either와 Option은 사용하기 쉽지만 둘을 결합하면 어떻게 될까요?

~~~kotlin
object UserService {
    fun findAge(user: String): Either<String, Option<Int>> {
        //Magic
    }
}
~~~

* UserService.findAge는 Either<String, Option<Int>>를 반환합니다. 
* 데이터베이스 또는 다른 인프라스트럭처에 접근하는 에러는 Left<String>을 반환합니다.
* 데이터베이스에서 값을 찾지 못한 경우에는 Right<None>을 반환하며, 값을 찾은 경우에는 Right<Some<Int>>를 반환합니다.

~~~kotlin
import arrow.core.*
import arrow.syntax.function.pipe

fun main(args: Array<String>) {
    val anakinAge: Either<String, Option<Int>> = UserService.findAge("Anakin")

    anakinAge.fold(::identity, { op -> 
        op.fold({ "Not found" }, Int::toString)
    }) pipe ::println
}
~~~

* age를 출력하려면 두 개의 중첩된 폴드가 필요합니다.
* 여러 값에 접근하는 작업을 수행해야 하는 경우 문제가 발생합니다.

~~~kotlin
import arrow.core.*
import arrow.syntax.function.pipe
import kotlin.math.absoluteValue

fun main(args: Array<String>) {
    val anakinAge: Either<String, Option<Int>> = UserService.findAge("Anakin")
    val padmeAge: Either<String, Option<Int>> = UserService.findAge("Padme")

    val difference: Either<String, Option<Either<String, Option<Int>>>> =
    anakinAge.map { aOp -> 
        padmeAge.map  { pOp -> 
            pOp.map {p -> 
                (a - b).absoluteValue
            }
        }
    }

    difference.fold(::identity, { op1 -> 
        op1.fold({ "Not Found"}, { either -> 
            either.fold(::identity, { op2 -> 
                op2.fold({ "Not Found" }, Int::toString)})
        })
    }) pipe ::println
}
~~~

* 모나드는 부드럽지 않으므로 이런 작업을 만드는 것이 아주 빠르게 복잡해질 수 있습니다.
* 그러나 언제나 컴프리헨션에 의지 할 수 있습니다.

~~~kotlin
import arrow.core.*
import arrow.syntax.function.pipe
import arrow.typeclasses.binding
import kotlin.math.absoluteValue

fun main(args:Array<String>) {
    val anakinAge: Either<String, Option<Int>> = UserService.findAge("Anakin")
    val padmeAge: Either<String,  Option<Int>> = UserService.findAge("Padme")
    val difference: Either<String, Option<Option<Int>>> = 
    Either.monad<String>().binding {
        val aOp: Option<Int> = anakinAge.bind()
        val pOp: Option<Int> = padmeAge.bind()
        aOp.map { a -> 
            pOp.map { p -> 
                (a - p).absoluteValue
            }
        }
    }.ev()

    difference.fold(::identity, { op1 -> 
        op1.fold({ "Not found" }, { op2 -> 
            op2.fold({ "Not found" }, Int::toString)})}) pipe ::println
}
~~~

* 반환 타입은 그렇게 길지 않고 폴드는 처리하기 더 쉽습니다.

* 다음 코드는 중첩된 컴프리헨션입니다.

~~~kotlin
fun main(args: Array<String>) {
    val anakinAge: Either<String, Option<Int>> = UserService.findAge("Anakin")
    val padmeAge: Either<String,  Option<Int>> = UserService.findAge("Padme")
    val difference: Either<String, Option<Option<Int>>> = 
    Either.monad<String>().binding {
        val aOp: Option<Int> = anakinAge.bind()
        val pOp: Option<Int> = padmeAge.bind()
        Option.monad().binding {
            val a: Int = aOp.bind()
            val p: Int = pOp.bind()
            (a - b).absoluteValue
        }.ev()
    }.ev()

    difference.fold(::identity, { op -> 
        op.fold({ "Not found" }, Int::toString)
    }) pipe ::println
}
~~~

* 같은 타입의 값과 결과를 얻었지만 모나드 트랜스포머는 다른 옵션을 가지고 있습니다.

* 모나드 트랜스포머는 하나처럼 실행될 수 있는 두 모나드의 조합입니다.

* 예를 들어 Option은 Either 내에 중첩된 모나드 타입이므로 OptionT를 사용할 것입니다.

~~~kotlin
import arrow.core.*
import arrow.data.OptionT
import arrow.data.monad
import arrow.data.value
import arrow.systax.function.pipe
import arrow.typeclasses.binding
import kotlin.math.absoluteValue

fun  main(args: Array<String>) {
    val anakinAge: Either<String, Option<Int>> = 
    OptionT.monad<EitherKindPartial<String>>().binding {
        val a: Int = OptionT(anakinAge).bind()
        val p: Int = OptionT(padmeAge).bind()
        (a - p).absoluteValue
    }.value().ev()

    difference.fold(::identity, { op -> 
        op.fold({ "Not found" }, Int::toString)
    }) pipe ::println
}
~~~

* OptionT.monad<EitherKindPartial<String>>().binding을 사용했습니다.
* EitherKindPartial<String> 모나드는 래퍼 타입이 Either<String, Option<T>> 타입의 값으로 OptionT를 사용합니다.
* 이전에는 ev() 메소드만 사용했지만 이제는 value() 메소드를 사용해 OptionT 내부값을 추출해야 합니다.

## Try
* Try는 계산이 실패했는지의 여부를 나타냅니다.
* Try<A>는 실패를 나타내는 Failure<A>와 성공적인 작없을 나타내는 Success<T>라는 두 개의 하위 클래스를 가진 sealed 클래스입니다.

~~~kotlin
import arrow.data.Try

fun tryDivide(num: Int, den: Int): Try<Int> = Try { divide(num, den)!! }
~~~
* Try 인스턴스를 만드는 가장 간단한 방법은 Try.invoke 연산자를 사용하는 것입니다.
* 블록 내부에서 예외가 발생하면 Failure를 반환합니다.
* 모든 것이 잘되면 Success<INT>를 반환합니다.
* 예를 들어 !! 연산자는 나누기가 null을 반환하면 NPE를 던질 것입니다.

~~~kotlin
fun tryDivision(a: Int, b: Int, den: Int): Try<Tuple2<Int, Int>> {
    val aDiv = tryDivide(a, den)
    return when (aDiv) {
        is Success -> {
            val bDiv = tryDivide(b, den)
            when (bDiv) {
                is Success -> {
                    Try { aDiv.value toT bDiv.value }
                }
                is Faulure -> Failure(bDiv.exception)
            }
        }
        is Failure -> Failure(aDiv.exception)
    }
}
~~~

* Try<T> 함수의 간단한 목록을 살펴봅니다.
* 함수는 flatMap, isFailure, isSuccess, getOrDefault 등이 있습니다.

* flatMap 구현은 Either나 Option과 매우 유사하며, 이름 및 행동 규약의 공통 세트를 가진 값을 보여줍니다.

~~~kotlin
fun flatMapTryDivision(a: Int, b: Int, den: Int): Try<Tuple2<Int, Int>> {
    return tryDivide(a, den).flatMap { aDiv -> 
        tryDivide(b, den).flatMap { bDiv -> 
            Try { aDiv toT bDiv }
        }
    }
}
~~~
* 모나딕 컴프리헨션은 Try에서도 사용 가능합니다.

~~~kotlin
fun comprehensionTryDivision(a: Int, b: Int, den: Int): Try<Tuple2<Int, Int>> {
    return Try.monad().binding {
        val aDiv = tryDivide(a, den).bind()
        val bDiv = tryDivide(b, den).bind()
        aDib toT bDiv
    }.ev()
}
~~~

* MonadError 인스턴스를 사용하는 다른 종류의 모나딕 컨프리헨션이 있습니다.

~~~kotlin
fun monadErrorTryDivision(a: Int, b: Int, den: Int): Try<Tuple2<Int, Int>> {
    return Try.monadError().bindingCatch {
        val aDiv = divide(a, den)!!
        val bDiv = divide(b, den)!!
        aDiv toT bDiv
    }.ev()
}
~~~
* monadError.bindingCatch를 사용하면 예외를 발생하는 모든 연산은 실패로 끝나고 마지막에 반환은 Try<T>로 래핑됩니다.
* MonadError는 Option과 Either에서도 사용할 수 있습니다.

## State
* State는 애플리케이션 상태를 처리하기 위한 함수적 접근 방식을 제공하는 구조입니다.

* State<S, A>는 S -> Tuple2<S, A>에 대한 추상화입니다.
* S는 상태 타입을 나타내며, Tuple2<S, A>은 결과인데, S는 새롭게 업데이트된 상태이며, A는 함수 반환입니다.

* 다음은 가격고 이를 계산하는 두 가지를 반환하는 간단한 예입니다.
* 가격을 계산하려면 VAT 20%를 추가하고 가격이 일정 기준을 초과하는 경우 할인을 적용해야 합니다.
~~~kotlin
import arrow.core.Tuple2
import arrow.core.toT
import arrow.data.State

typealias PriceLog = MutableList<Tuple2<String, Double>>

fun addVat(): State<PriceLog, Unit> = State { log: PriceLog -> 
    val (_, price) = log.last()
    val vat = price * 0.2
    log.add("Add VAT: $vat" toT price + vat)
    log toT Unit
}
~~~

* MutableList<Tuple2<String, Double>>을 위한 타입 앨리어스 PriceLog를 가집니다.

* PriceLog는 State 대표가 됩니다. 각 단계는 Tuple2<String, Double>로 표현됩니다.

* 첫 번째 함수 addVat(): State<PriceLog, Unit>은 첫 번째 단계를 표현합니다.

* PriceLog와 단계를 적용하기 전의 state를 받고 Tuple2<PriceLog, Unit>을 반환해야 하는 State 빌더를 사용하는 함수를 작성합니다.

* 현재는 각격이 필요 없으므로 Unit을 사용합니다.

~~~kotlin
fun applyDiscount(threshold: Double, discount: Double): State<PriceLog, Unit> = State { log -> 
    val (_, price) = log.last()
    if(price > threshold) {
        log.add("Applying - $discount" toT price - discount)
    } else {
        log.add("No discount applied" toT price)
    }
    log toT Unit
}
~~~
* applyDiscount 함수는 두 번째 단계입니다. 여기서 소개한 유일한 새로운 요소는 threshold, discount라는 파라미터입니다.

~~~kotlin
fun finalPrice(): State<PriceLog, Double> = State { log -> 
    val (_, price) = log.last()
    log.add("Final Price" toT price)
    log toT price
}
~~~
* 마지막 단계는 finalPrice() 함수로 표현되며, 이제 Unit 대신 Double을 반환합니다.

~~~kotlin
import arrow.data.ev
import arrow.instances.monad
import arrow.typeclasses.binding

fun calculatePrice(threshold: Double, discount: Double) = 
State().monad<PriceLog>().binding {
    addVat().bind()
    applyDiscount(threshold, discount).bind() //Unit
    val price: Double = finalPrice().bind()
    price
}.ev()
~~~

* 일련의 단계를 표현하기 위해 모나드 컴프리헨션을 사용하고 State 함수를 순차적으로 사용합니다.

* 한 함수에서 다음 함수로 PriceLog 상태가 암시적으로 흐릅니다.
* 마지막에 최종 가격을 산출합니다. 새 단계를 추가하거나 기존 것을 전환하는 것은 선을 추가하거나 이동하는 것처럼 쉽습니다.

~~~kotlin
import arrow.data.run
import arrow.data.runA

fun main(args: Array<String>) {
    val (history: PriceLog, price: Double) = calculatePrice(100.0, 2.0).run(mutableListOf("Init" toT 15.0))
    println("Price: $price")
    println("::History::")
    history.map { (text, value) -> "$text\t|\t$value" }
        .forEach(::println)
    
    val bigPrice: Double = calculatePrice(100.0, 2.0).runA(mutableListOf("Init" toT 1000.0))
    println("bigPrice = $bigPrice")
}
~~~
* calculatePrice 함수를 사용하려면 threshold와 discount 값을 제공해야 하며, 그런 다음 최초 상태와 함께 확장 함수를 실행해야 합니다.
* 가격에만 관심이 있다면 runA만 실행하고 history뿐이라면 runS를 실행합니다.

### State가 있는 코리커젼
* State는 코리커젼에서 유용합니다.

~~~kotlin
fun <T, S> unfold(s: S, f: (S) -> Pair<T, S>?): Sequence<T> {
    val result = f(s)
    return if (result != null) {
        squenceOf(result.first) + unfold(result.second, f)
    } else {
        sequenceOf()
    }
}
~~~
* 원래 unfold 함수는 State<S, T>와 매우 비슷한 f: (S) -> Pair<T, S>?를 사용합니다.

~~~kotlin
fun <T, S> unfold(s: S, state: State<S, Option<T>>): Sequence<T> {
    val (actualState: S, value: Option<T>) = state.run(s)
    return value.fold(
        { sequenceOf() }, 
        { t -> 
            sequenceOf(t) + unfold(actualState, state)
    })
}
~~~
* lambda (S) -> Pair<T, S>?를 갖는 대신 State<S, Option<T>>를 사용하고 None을 위해 빈 시퀀스 혹은 Some<T>를 위해 재귀 호출을 갖는 Option으로부터 폴드 함수를 사용합니다.

~~~kotlin
fun factorial(size: Int): Sequence<Long> {
    return sequenceOf(1L) + unfold(1L to 1) { (acc, n) -> 
        if(size > n) {
            val x = n * acc
            (x) to (x to n + 1)
        } else null
    }
}
~~~
* 이 팩토리얼 함수는 Pair<Long, Int>와 람다 (Pair<Long, Int>) -> Pair<Long, Pair<Long, Int>>>를 갖는 unfold를 사용합니다.

~~~kotlin
import arrow.syntax.option.some

fun factorial(size: Int): Sequence<Long> {
    return sequenceOf(1L) + unfold(1L toT 1, State { (acc, n) -> 
        if (size > n) {
            val x = n * acc
            (x toT n + 1) toT x.some()
        } else {
            (0L toT 0) toT None
        }
    })
}
~~~
* 리팩토링된 팩토리얼은 State<Tuple<Long, Int>, Option<Long>>을 사용하지만 내부 로직은 대부분 같습니다.
* 새 팩토리얼은 null을 사용하지 않는데, 이 것은 상당한 개선입니다.

~~~kotlin
fun fib(size: Int): Sequence<Long> {
    return sequenceOf(1L) + unfold(Triple(0L, 1L, 1)) { (cur, next, n) -> 
        if  (size > n) {
            val x = cur + next
            (x) to Triple(next, x, n + 1)
        } else null
    }
}
~~~
* 마찬가지로 피보나치는 Triple<Long, Long, Int>와 람다 (Triple<Long, Long.Int>) -> Pair<Long, Tirple<Long, Long, Int>>? 를 갖는 unfold를 사용합니다.

~~~kotlin
import arrow.syntax.tuples.plus

fun fib(size: Int): Sequence<Long> {
    return sequenceOf(1L) + unfold((0L toT 1L) + 1, State { (cur, next, n) -> 
        if(size > n) {
            val x = cur + next
            ((next toT x) + (n + 1)) toT x.some()
        } else {
            ((0L toT 0L) + 0) toT None
        }
    })
}
~~~

* 그리고 리팩토링된 피보나치는 State<Tuple3<Long, Long, Int>, Option<Long>>을 사용합니다.
* Tuple2<A, B>와 함께 사용되는 확장 연산자 plus와 C는 Tuple3<A, B, C>를 반환한다는 점에 주의를 기울여야 합니다.

~~~kotlin
fun main(args: Array<String>) {
    factorial(10).forEach(::println)
    fib(10).forEach(::println)
}
~~~
* 이제는 시퀀스를 생성하기 위해 코리커젼 함수를 사용할 수 있습니다. 
* 엔터프라이즈 인티그레이션 패턴의 메시지 히스토리 혹은 비행 확인 긴 등록 양식과 같은 많은 단계를 갖는 폼 안내와 같은 State의 더 많은 사용법이 있습니다.