# Anko SQLite
* Android 커서를 사용하여 SQLite 쿼리 결과를 파싱하는 것은 힘듭니다. 
* 쿼리의 결과 행을 구문 분석하기 위해 많은 상용구 코드를 작성하고 이를 열거 된 모든 리소스를 적절하게 닫으려면 셀 수없는 try..finally 블록으로 묶어야합니다.
* Anko는 SQLite databases와 함께 간단하게 작동할 수 있도록 많은 기능들을 제공합니다.

## Using Anko SQLite in your project
* build.gradle의 dependency에 anko-sqlite를 추가합니다.
~~~gradle
dependencies {
    implementation "org.jetbrains.anko:anko-sqlite:$anko_version"
}
~~~
## Accessing the database
* SQLiteOpenHelper를 사용하는 경우 일반적으로 getReadableDatabase () 또는 getWritableDatabase ()를 호출합니다. 
* 결과는 실제로 프로덕션 코드에서 동일하지만 수신 된 SQLiteDatabase에서 close () 메서드를 호출해야합니다. 
* 또한 어딘가에 도우미 클래스를 캐시해야하며 여러 스레드에서이 클래스를 사용하는 경우 동시 액세스를 인식하고 있어야합니다. 
* 이 모든 것은 꽤 힘듭니다. 그래서 안드로이드 개발자는 디폴트 SQLite API에 열중하지 않고 대신 ORM과 같은 값 비싼 래퍼를 선호합니다.
* Anko는 기본 클래스를 완벽하게 대체하는 ManagedSQLiteOpenHelper 클래스를 제공합니다. 사용 방법은 다음과 같습니다.
~~~kotlin
class MyDatabaseOpenHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MyDatabase", null, 1) {
    companion object {
        private var instance: MyDatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDatabaseOpenHelper {
            if (instance == null) {
                instance = MyDatabaseOpenHelper(ctx.getApplicationContext())
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable("Customer", true, 
                    "id" to INTEGER + PRIMARY_KEY + UNIQUE,
                    "name" to TEXT,
                    "photo" to BLOB)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("User", true)
    }
}

// Access property for Context
val Context.database: MyDatabaseOpenHelper
    get() = MyDatabaseOpenHelper.getInstance(getApplicationContext())
~~~
* try 블록에 코드를 포함하는 대신 이제 다음과 같이 작성할 수 있습니다.
~~~kotlin
database.use {
    // 'this' is a SQLiteDatabase instance
}
~~~
* {} 안에 모든 코드를 실행 한 후에 데이터베이스가 완전히 닫힙니다.

* 비동기 호출 예제 : 
~~~kotlin
class SomeActivity : Activity() {
    private fun loadAsync() {
        async(UI) {
            val result = bg { 
                database.use { ... }
            }
            loadComplete(result)
        }
    }
}
~~~
* 아래 언급된 메서드들과 모든 메서드를은 SQLiteException에 throw 할 수 있습니다.
* Anko가 오류가 발생하지 않는 것처럼 가장하는 것은 무리일 수 있으므로 직접 처리해야합니다.
## Creating and dropping tables
