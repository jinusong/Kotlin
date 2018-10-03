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