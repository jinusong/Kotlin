# Kotlin Android Extension 사용하기

## 예시와 함께 설명
* EditText를 통해 이름을 입력받고, 버튼을 누르면 입력한 이름과 메시지가 출력되는 기능을 갖추고 있는 액티비티를 예로 듭니다.
* 레이아웃은 다음과 같습니다.
~~~xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <EditText android:id="@+id/et_name"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Enter name"/>

    <Button android:id="@+id/btn_submit"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Submit" />

    <TextView android:id="@+id/tv_message"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textAppearance="?android:attr/textAppearanceMedium"
        android:gravity="center"
        android:layout_marginTop="12dp"/>

</LinearLayout>
~~~


## 일반적인 액티비티 기능 구현
* 기능을 구현한 Java 코드입니다.
~~~java
public class MainAcitivty extends AppCompatActivity {
    
    // 뷰 인스턴스 선언

    EditText etName;
    
    Button btnSubmit;

    TextView tvMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // findViewById()를 사용하여 뷰 인스턴스를 받아옵니다.
        etName = (EditText) findViewById(R.id.et_name);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        tvMessage = (TextView) findViewById(R.id.tv_message);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               tvMessage.setText("Hello, " + etName.getText().toString())
           } 
        });
    }
}
~~~

## 액티비티에서 Kotlin Android Extension 적용하기
* Kotlin Android Extension을 이용하면 앞의 코드를 바꿀 수 있습니다.
* setContentView() 메서드를 사용하여 액티비티에서 사용할 레이아웃을 설정하는 것은 기존과 동일하지만 뷰의 ID를 사용하여 해당 뷰의 인스턴스에 바로 접근할 수 있으므로 코드의 양이 비약적으로 줄었습니다.
~~~kotlin
// activity_main 레이아웃에 있는 뷰를 사용하기 위하 import 문
import kotlinx.android.synthetic.main.activity_main.*

...
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 뷰 ID를 사용하여 인스턴스에 바로 접근합니다.
        btn_submit.setOnClickListener {
            tv_message.text = "Hello, " + et_name.text.toString()
        }
    }
}
~~~
## 프래그먼트에서 적용하기
* 앞의 액티비티에서 사용한 레이아웃과 동일한 뷰 구조와 기능을 갖는 레이아웃 fragment_main을 예로 듭니다.
* 프래그먼트를 구성하는 레이아웃을 onCreateView()에서 반환하는 것은 기존과 동일합니다.
* 프래그먼트애서 표시할 뷰가 생성 된 이후인 onViewCreated()부터 코틀린 안드로이드 익스텐션을 사용하여 뷰 인스턴스에 접근할 수 있습니다.
~~~kotlin
// fragment_main 레이아웃에 있는 뷰를 사용하기 위한 import 문
import kotlinx.android.synthetic.mainfragment_main.*
...

class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflaster.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        // 뷰 ID를 사용하여 인스턴스에 바로 접근합니다.
        btn_submit.setOnClickListener {
            tv_message.text = "Hello, " + et_name.text.toString()
        }
    }
}
~~~