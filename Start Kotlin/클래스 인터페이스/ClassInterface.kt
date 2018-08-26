// 클래스와 인터페이스는 크게 다르지 않습니다.

class Foo {
    val foo: String = "foo"

    fun foo(name: String) : String{
        return name
    }

    fun main(){
        var Name = "Nice Kotlin"
        Name = foo(Name)
        println(Name)
    }
}

interface Bar{
    fun bar(){

    }
}