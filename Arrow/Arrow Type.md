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

