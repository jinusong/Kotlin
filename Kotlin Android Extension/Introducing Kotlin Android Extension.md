# Introducing Kotlin Android Extension
## Introducing problem
* findViewById() 메서드는 액티비티나 프래그먼트 등 레이아웃 파일에 선언된 여러 개의 뷰(view)로 구성된 화면에서 특정 뷰의 인스턴스를 얻기 위해 사용합니다.
* 하지만 이 메서드에서 반환한 뷰 객체를 잘못된 타입의 뷰로 캐스팅하거나 다른 레이아웃에 선언된 ID를 잘못 사용하면 널 값을 반환합니다. 
* 즉, 실수로 버그를 발생시키기 매우 쉬워 안드로이드 앱 개발자에게 이 메서드는 애증의 존재입니다.
* 이 뿐만 아니라, 화면을 구성하는 뷰의 개수만큼 findViewById() 메서드를 사용해야합니다.
* 때문에, 복잡한 구조로 구성된 화면을 다루는 경우 뷰 인스턴스를 받는 코드만 몇십 줄을 차지하여 코드의 가독성이 떨어집니다.

## problem code
~~~java
public class MainActivity extends AppCompatActivity {
    // 뷰 인스턴스 선언
    private TextView tvTitle;
    private TextView tvSubTitle;
    private ImageView ivProfile;
    private Button btnEdit;
    private TextView tvAddress;
    private TextView tvMemo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        // 뷰 인스턴스 초기화 수행
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSubTitle = (TextView) findViewById(R.id.tv_sub_title);
        tvProfile = (ImageView) findViewById(R.id.iv_profile);
        btnEdit = (Button) findViewById(R.id.btn_edit);
        tvAddress = (TextView) findViewById(R.id.tv_address);
        tvMemo = (TextView) findViewById(R.id.tv_memo);
    }
}
~~~

## Introducing Kotlin Android Extension
* Kotlin Android Extension은 이런 불편을 말끔히 해결하기 위해 존재합니다.