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
* Anko와 함께라면 쉽게 새로운 테이블을 만들고 삭제할 수 있습니다. 구문은 간단합니다.
~~~kotlin
database.use {
    createTable("Customer", true,
    "id" to INTEGER + PRIMARY_KEY + UNIQUE, 
    "name" to TEXT,
    "photo" to BLOB)
}
~~~
* SQLite에서는 NULL, INTEGER, REAL, TEXT, BLOB 총 5개의 메인 타입들이 있습니다.
* 하지만 각 열에는 PRIMATY KEY 또는 UNIQUE와 같은 수정자가 있을 수 있습니다. 이러한 수정자는 기본 유형 이름에 "추가"와 함께 추가 할 수 있습니다.
* 테이블을 지울 때에는 dropTable 함수를 사용합니다.
~~~kotlin
dropTable("User", true)
~~~
## Inserting data
* 일반적으로 테이블에 행을 삽입하려면 ContentValues ​​인스턴스가 필요합니다. 다음은 그 예입니다.
~~~kotlin
val values = ContentValues()
values.put("id", 5)
values.put("name", "John Smith")
values.put("email", "user@domain.org")
db.insert("User", null, values)
~~~
* Anko는 insert() 함수의 인자로 값을 직접 전달하여 이러한 코드의 거치래을 제거 할 수 있습니다.
~~~kotlin
// Where db is an SQLiteDatabase
// eg: val db = database.writeableDatabase
db.insert("User", 
    "id" to 42,
    "name" to "John",
    "email" to "user@domain.org"
)
~~~
* database.use를 사용하면
~~~kotlin
database.use {
    insert("User",
        "id" to 42,
        "name" to "John",
        "email" to "user@domain.org"
    )
}
~~~
## Querying data
* Anko는 편리한 쿼리 빌더를 제공합니다. db.select (tableName, vararg columns)를 사용하여 만들 수 있습니다. db는 SQLiteDatabase의 인스턴스입니다.

함수                                   | 기능
--------------------------------------|---------- 
`column(String)`                      | 검색어 선택하는 열 추가하기
`distinct(Boolean)`                   | 고유한 쿼리
`whereArgs(String)`                   | 원시 문자열 `where` 쿼리를 지정하기
`whereArgs(String, args)` (중요)      | 인자를 사용하여`where` 쿼리를 지정하기
`whereSimple(String, args)`           | `? '마크 인자를 가진`where` 쿼리를 지정하기
`orderBy(String, [ASC/DESC])`         | 이 열의 순서
`groupBy(String)`                     | 이 열의 그룹화하기
`limit(count: Int)`                   | 쿼리 결과 행 수 제한하기
`limit(offset: Int, count: Int)`      | 오프셋이있는 쿼리 결과 행 수 제한하기
`having(String)`                      | 원시 'having'표현식 지정하기
`having(String, args)` (중요)         | 인자로 `having' 표현식을 지정하기
* (중요) 로 표시된 함수는 특별한 방법으로 인자를 구문 분석합니다. 어떤 순서로든 값을 제공하고 원활한 escaping을 지원합니다.
~~~kotlin
db.select("User", "name")
    .whereArgs("(_id >  {userId}) and (name = {userName})",
        "userName" to "John", 
        "userId" to 42)
~~~
* 여기에서 {userId} 부분은 42, {userName}은 'John'으로 바뀝니다. 형식이 숫자가 아닌 경우 (Int, Float 등) 또는 Boolean 값이면 escaped됩니다. 다른 형식의 경우 toString() 표현이 사용됩니다.
* whereSimple 함수는 String 자료형을 허용합니다. 이것은 SQLiteDatavase의 query() 와 같은 일을 합니다. (질문 표시 ?는 자료형의 실제 값과 대체됩니다.)