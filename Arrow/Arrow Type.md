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

* M