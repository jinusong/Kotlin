# Anko Coroutines
## Using Anko Coroutines in your project
* build.gradle의 dependency에 anko-coroutines를 추가합니다.
~~~kotlin
dependencies {
    implementation "org.jetbrains.anko:anko-coroutines:$anko_version"
}
~~~
## Listener helpers
## asReference()
* 비동기 API가 취소를 지원하지 않으면 코루틴이 무기한 정지 될 수 있습니다. 
* 코루틴은 캡처 된 객체에 대한 강력한 참조를 보유하므로 Activity 또는 Fragment 인스턴스의 인스턴스를 캡처하면 메모리 누수가 발생할 수 있습니다.
* 이러한 경우에는 직접 캡처 대신 asReference ()를 사용하면 됩니다.
~~~kotlin
suspend fun getData(): Data { ... }

class MyActivity : Activity() {
    fun loadAndShowData() {
		// Ref<T> uses the WeakReference under the hood
		val ref: Ref<MyActivity> = this.asReference()
	
		async(UI) {
		    val data = getData()
				
		    // Use ref() instead of this@MyActivity
		    ref().showData(data)
		}
    }

    fun showData(data: Data) { ... }
}
~~~
## bg()
* bg ()를 사용하여 백그라운드 스레드에서 코드를 쉽게 실행할 수 있습니다.
~~~kotlin
fun getData(): Data { ... }
fun showData(data: Data) { ... }

async(UI) {
    val data: Deferred<Data> = bg {
		// Runs in background
		getData()
    }

    // This code is executed on the UI thread
    showData(data.await())
}
~~~