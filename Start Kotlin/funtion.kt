// 함수는 자바의 메서드에 대응
// 값 및 변수 선언과 마찬가지로 반환 타입을 뒤에 적습니다.

fun greet(name: String) : Unit{
    println("Hello, $name!")
}

fun sum(a: Int, b: Int) : Int{
    return a + b
}

// Unit은 void와 비슷한 의미를 가지며 다음과 같이 반환형을 생략 가능

fun greet2(name: String) {
    println("Hello, $name!")
}

fun main(args: Array<String>){
    greet("Songjinwoo")
    greet2("Songjinwoo")
}