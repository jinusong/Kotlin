# Kotlin Coroutines
## intro
* 일부 API는 네트워크 IO, 파일 IO, CPU 또는 GPU 집약적인 작업 등과 같은 장기 실행 작업을 시작하고 호출자가 완료 될 때까지 차단하도록 요청합니다. coroutine은 스레드를 차단하지 않고 더 가볍고 제어 가능한 작업으로 바꾸는 방법을 제공합니다.
* coroutine은 비동기 프로그래밍을 단순화합니다. 프로그램의 로직은 코루틴에서 순차적으로 표현될 수 있으며 기본 라이브러리는 비동기를 파악합니다. 라이브러리에서는 사용자 코드의 관련 부분을 콜백으로 래핑하고, 관련 이벤트를 구독하고, 다른 스레드(또는 다른 시스템)에서 실행을 예약할 수 있으며 코드는 순차적으로 실행되는 것처럼 간단합니다.
## blocking and delay
* 기본적으로 coroutine은 스레드를 차단하지 않고 일시 중단할 수 있습니다. 스레드를 차단하는 것은 상대적으로 적은 수의 스레드만 유지할 수 있으므로 고부하인 경우 특히 비용이 많이 듭니다. 따라서 하나를 차단하면 중요한 작업이 지연됩니다.
* 반면 coroutine은 컨텍스트 스위치 또는 OS의 다른 개입은 필요하지 않습니다. 또한 사용자 라이브러리에 의해 서스펜션을 제어할 수 있습니다. 라이브러리 작성자는 서스펜션시 발생할 일을 결정하고 필요에 따라 최적화 / 로그 / 인터셉트를 결정할 수 있습니다.
* 또, coroutine은 무작위 명령에 의해 중단될 수 없으며, 특별히 표시된 기능에 대한 호출인 소위, 정지점에서만 중단된다는 것 입니다.
## 서스펜딩 함수
* 서스펜션은 suspend 라고 특별한 수식어를 붙인 함수를 호출할 때 발생합니다.
~~~kotlin
suspend fun doSomething(foo: Foo): Bar{
    ...
}
~~~
* 이러한 함수를 호출하면 coroutine이 일시중단 될 수 있기 때문에 이러한 함수를 서스펜딩 함수 라고 합니다. 
* 서스펜딩 함수는 일반 함수와 동일한 방식으로 매개 변수 및 반환 값을 사용할 수 있지만 coroutine 및 다른 서스펜딩 함수에서만 호출 할 수 있습니다.
* coroutine을 시작하려면 최소한 하나의 일시 중지 기능이 있어야하며 일반적으로 익명입니다. 다음은 라이브러리에서 가져온 async 함수입니다.
~~~kotlin
fun <T> async(block: suspend () -> T)
~~~
* 여기에 async()정규 함수이지만 block매개 변수에 suspend 수정자를 가진 함수 유형이 suspend () -> T있습니다. 그래서, 우리가 람다를 건네면 async(), 람다를 정지시키는 것이고, 정지 함수를 호출 할 수 있습니다.
~~~kotlin
async {
    doSomething(foo)
    ...
}
~~~
* 어떤 계산이 끝날 때까지 coroutine을 정지시키고 그 결과를 반환 await()할 수있는 정지 함수가 될 수 있습니다.
~~~kotlin
async {
    ...
    val result = computation.await()
    ...
}
~~~
* 실제 방법에 대한 자세한 내용은 async/await 기능의 작동은 kotlinx.coroutines에서 찾을 수 있습니다.
* 일시 중지 함수는 다음 await()과 doSomething()같은 정규 함수에서 호출할 수 없습니다 main().
~~~kotlin
fun main(arg: Array<String>){
    doSomething()
}
~~~
* 또한 일시 중단 함수일 수 있으며, 함수를 무시할 때 suspend 수식어를 지정해야 합니다.
~~~kotlin
interface Base{
    suspend fun foo()
}
class Derived: Base  {
    override suspend fun foo() { ... }
}
~~~
* 확장 함수는 suspend 일반 함수와 마찬가지로 표시 할 수 있습니다. 이를 통해 사용자가 확장 할 수있는 DSL 및 기타 API를 만들 수 있습니다. 
* 경우에 따라 라이브러리 작성자는 사용자가 코루틴을 일시 중단하는 새로운 방법을 추가하지 못하도록 해야 합니다.
* 이를 달성하기 위해 @RestrictsSuspension을 사용할 수 있습니다. 
* 수신자 클래스 또는 인터페이스 R에 주석이 달린 경우 모든 일시 중단 확장 프로그램은 회원 R 또는 다른 확장 프로그램에 위임해야 합니다. 
* 확장 프로그램은 무한정 서로 위임할 수 없으므로 R라이브러리 작성자가 완전히 제어할 수 있다는 호출 멤버를 통해 모든 일시 중단이 발생 합니다.
* 모든 서스펜션이 라이브러리에서 특별한 방식으로 처리되는 드문 경우에 관련이 있습니다. 
* 예를 들어, buildSequence() 함수를 통해 구현할 때 우리는 coroutine에서 일시 중단된 호출이 다른 함수나 다른 함수를 호출하지 않도록해야합니다. 
~~~kotlin
@RestrictsSuspension
public abstract class SequenceBuilder<in T> {
    ...
}
~~~