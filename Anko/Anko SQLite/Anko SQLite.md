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
* 이 쿼리를 어떻게 실행할까요? exec() 함수를 씁니다. Cursor. () -> T 형식의 확장 기능을 허용합니다. 수신한 확장 기능을 시작한 다음 Cursor를 닫아 스스로 수행할 필요가 없도록합니다.
~~~kotlin
db.select("User", "email").exec {
    // Doing some stuff with emails
}
~~~
## Parsing query results
* 그래서 우리는 Cursor를 가지고 있고, 그것을 정규 클래스로 어떻게 파싱할 수 있을까요? 
* Anko는 parseSingle, parseOpt 및 parseList 함수를 제공하여 훨씬 쉽게 처리 할 수 ​​있습니다.

| Method | Description |
|--------|-------------|
| parseSingle(rowParser): T | Parse exactly one row|
| parseOpt(rowParser): T? | Parse zero or one row |
| parseList(rowParser): List<T> | Parse zero or more rows |

* 수신 된 Cursor가 둘 이상의 행을 포함하면 parseSingle () 및 parseOpt ()가 예외를 throw합니다.
* rowParser 란 무엇일까요? 각 함수는 RowParser와 MapRowParser인 두 가지 유형의 파서를 지원합니다.
~~~kotlin
interface RowParser<T> {
    fun parseRow(columns: Array<Any>): T
}

interface MapRowParser<T> {
    fun parseRow(columns: Map<String, Any>): T
}
~~~
* 매우 효율적인 방법으로 쿼리를 작성하려면 RowParser를 사용합니다 (하지만 각 열의 인덱스를 알아야합니다). 
* parseRow는 Any의 타입을 받아들입니다 (Any 형은 Long, Double, String 또는 ByteArray 이외의 것일 수 있음). 
* 반면에 MapRowParser를 사용하면 열 이름을 사용하여 행 값을 가져올 수 있습니다.
* Anko는 이미 간단한 단일 열 행에 대한 파서를 보유하고 있습니다.

    * ShortParser
    * IntParser
    * LongParser
    * FloatParser
    * DoubleParser
    * StringParser
    * BlobParser
* 또한 클래스 생성자에서 행 파서를 만들 수 있습니다.
클래스가 있다고 가정합니다.
~~~kotlin
class Person(val firstName: String, val lastName: String, val age: Int)
~~~
* 파서는 간단해집니다.
~~~kotlin
val rowParser = classParser<Person>()
~~~
* 현재로서는 기본 생성자에 선택적 매개 변수가있는 경우 Anko는 이러한 파서 작성을 지원하지 않습니다.
* 또한 생성자는 Java Reflection을 사용하여 호출되므로 커다란 데이터 세트의 경우 사용자 정의 RowParser를 작성하는 것이 더 합리적입니다.
* Anko db.select () 빌더를 사용하는 경우에는 parseSingle, parseOpt 또는 parseList를 직접 호출하고 적절한 파서를 전달할 수 있습니다.
## Custom row parsers
* 예를 들어, 열 (Int, String, String)에 대해 새 파서를 만들어 봅시다. 가장 일반적인 방법은 다음과 같습니다.
~~~kotlin
class MyRowParser : RowParser<Triple<Int, String, String>> {
    override fun parseRow(columns: Array<Any>): Triple<Int, String, String> {
        return Triple(columns[0] as Int, columns[1] as String, columns[2] as String)
    }
}
~~~
* 자, 이제 코드에 3가지 명시적 캐스트가 있습니다. rowParser 함수를 사용하여 제거해보겠습니다.
~~~kotlin
val parser = rowParser { id: Int, name: String, email: String ->
    Triple(id, name, email)
}
~~~
* 이게 다 입니다. rowParser는 모든 캐스트를 생성하고 원하는대로 람다 매개 변수의 이름을 지정할 수 있습니다.
## Cursor streams
* Anko는 SQLite Cursor에 기능적으로 접근하는 방법을 제공합니다. 
*  cursor.asSequence () 또는 cursor.asMapSequence () 확장 함수를 호출하여 일련의 행을 가져옵니다. 커서를 닫는 것을 잊지 마세요 :)
## Updating values
* 사용자 중 한 명에게 새로운 이름을 줍니다.
~~~kotlin
update("User", "name" to "Alice")
    .where("_id = {userId}", "userId" to 42)
    .exec()
~~~
* 또한 전통적인 방식으로 쿼리를 제공하려는 경우 update에는 whereSimple () 메서드가 있습니다.
~~~kotlin
update("User", "name" to "Alice")
    .`whereSimple`("_id = ?", 42)
    .exec()
~~~
## Delete Data
* 행을 삭제 해 봅시다 (delete 메소드에는 whereSimple () 메소드가 없으며, 대신 인수에 직접 쿼리를 제공합니다).
~~~kotlin
val numRowsDeleted = delete("User", "_id = {userID}", "userID" to 37)
~~~
## Transaction
* transaction ()이라는 특별한 함수가 있는데, 여러 개의 데이터베이스 연산을 하나의 SQLite 트랜잭션으로 묶을 수 있습니다.
~~~kotlin
transaction {
    // Your transaction code
}
~~~
* {} 블록 내에 예외가 발생하지 않으면 트랜잭션은 성공으로 표시됩니다.
* 어떤 이유로 트랜잭션을 중단하려면 TransactionAbortException을 throw 하세요. 이 경우에는이 예외를 직접 처리 할 필요가 없습니다.

## 원문
* https://github.com/Kotlin/anko/wiki/Anko-SQLite