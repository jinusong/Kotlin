# Kotlin Android Extension 리사이클러뷰

## 리사이클러뷰 문제점
* 리사이클러뷰는 각 항목을 표시하기 위해 뷰홀더를 사용하며, 뷰홀더에서 표시할 뷰를 구성하기 위해 주로 레이아웃 파일을 사용합니다.
* 때문에, 액티비티나 프래그먼트와 마찬가지로 findViewById()를 사용하여 뷰의 인스턴스를 받아 사용했습니다.

## 예시 레이아웃
* 레이아웃(item_city.xml)은 다음과 같이 도시 이름과 코드를 표시하기 위한 TextView 두 개로 구성되어 있습니다.
~~~xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="?attr/listPreferredItemHeight"
    android:orientation="vertical"
    android:padding="8dp">

    <TextView android:id="@+id/tv_city_name"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textAppearance="?android:attr/textAppearanceMedium"/>

    <TextView android:id="@+id/tv_city_code"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textAppearance="?android:attr/textAppearanceSmall"/>

</LinearLayout>
~~~
## 예시 Java코드
* 코드는 어댑터 Java 코드입니다.
* 각 항목을 리스트에 표시하기 위해 사용하는 뷰홀더 내부에서 액티비티와 프래그먼트와 유사하게 findViewById() 메서드를 사용하는 것을 확인할 수 있습니다.
~~~java
public class CityAdapter extends RecyclerView.Apdater<CityAdapter.Holder>{
    
    private List<Pair<String, String> cities;

    public CityAdapter() {
        cities = new ArrayList<>();
        cities.add(Pair.create("Seoul", "SEO"));
        cities.add(Pair.create("Tokyo","TOK"));
        cities.add(Pair.create("Mountain View","MTV"));
        cities.add(Pair.create("Singapore", "SIN"));
        cities.add(Pair.create("New York", "NYC"));
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(parent);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Pair<String, String> item = cities.get(position);

        // 각 부분에 해당하는 값을 반영합니다.
        holder.cityName.setText(item.first);
        holder.cityCode.setText(item.second);
    }

    @Override
    public int getItemCount() {
        return null != cities ? cities.size() : 0;
    }

    class Holder extends RecyclerView.ViewHolder {

        // 뷰 인스턴스 선언 
        TextView cityName;

        TextView cityCode;

        Holder(ViewGroup parent) {
            super(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city, parent, flase));

            // findViewById()를 사용하여 뷰 인스턴스를 받아옵니다.
            cityName = (TextView) itemView.findViewById(R.id.tv_city_name);
            cityCode = (TextView) itemView.findViewById(R.id.tv_city_code);
        }
    }
}
~~~

## Kotlin Android Extension 적용
* Kotlin Android Extension을 사용하면 앞의 Java 코드를 다음과 같이 표현할 수 있습니다.
* Kotlin Android Extension은 뷰홀더의 itemView를 통해 레이아웃 내 뷰에 접근할 수 있도록 지원합니다.

~~~kotlin
// item_city.xml 레이아웃에 있는 뷰를 사용하기 위한 import 문
import kotlinx.android.synthetic.main.item_city.view.*
...

class CityAdapter : RecyclerView.Adapter<CityAdapter.Holder>() {
    private val cities = listOf(
        "Seoul" to "SEO", 
        "Tokyo" to "TOK", 
        "Mountain View" to "MTV",
        "Singapore" to "SIN", 
        "New York" to "NTC")

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(parent)

        override fun onBindViewHolder(holder: Holder, position: Int) {
            val (city, code) = cities[position]

            // 코틀린 안드로이드 익스텐션을 사용하여 레이아웃 내 뷰에 접근하려면
            // 뷰홀더 내의 itemView를 거쳐야 합니다.
            with(holder.itemView) {
                
                // 뷰 ID를 사용하여 인스턴스에 바로 접근합니다.
                tv_city_name.text = city
                tv_city_code.text = code
            }
        }

        override fun getItemCount() = cities.size

        inner class Holder(parent: ViewGroup)
        : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflater(R.layout.item_city, parent, false))
}
~~~