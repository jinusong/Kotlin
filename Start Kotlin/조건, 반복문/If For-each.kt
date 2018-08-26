// if-else문은 자바와 사용법이 동일합니다.
fun max(a: Int, b: Int) : Int{
    if(a > b){
        return a
    } else {
        return b
    }
}

// when문은 자바의 switch와 동일한 역할을 합니다.
fun countItems(count: Int) {
    when (count) {
        1 -> println("There is $count item.")
        else -> println("There are $count items.")
    }
}

// Kotlin 에서는 for문을 쓰지 않고 for-each문을 지원합니다.
fun For(){
    val items = listOf("foo", "bar", "baz")
    for (item in items) {
        println("item: $item")
    }
}

//while문은 Java와 동일합니다.
fun While(){
    val items = listOf("foo", "bar", "baz")
    var i = 0
    while(i < items.size) {
        println("item: ${items[i]}")
        i++
    }
}