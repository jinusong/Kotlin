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

