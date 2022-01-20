package com.example.security_project

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.security_project.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.database.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var marker: Marker
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance() // 파이어 베이스 객체
    private var location_Ref : DatabaseReference = database.getReference("location") // 로케이션 참조
    private val flag_Ref : DatabaseReference = database.getReference("theft") // 플래그 참조

    val list = ArrayList<LatLng>()
    var before_maker = MarkerOptions()


    val polyLineOptions = PolylineOptions().width(5f).color(Color.RED) // 폴리라인 적용

    var latitude :Double = 12.0
    var longtitude :Double = 13.1

//    private lateinit var latitude: DatabaseReference
//    private lateinit var longtitude: DatabaseReference


    private lateinit var databaseRef: DatabaseReference // firebase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        init_setting() // 초기 세팅
        location_update() // 위치 업데이트
        flag_update() // 플래그 업데이트트




       binding = ActivityMapsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is r ady to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment


        mapFragment.getMapAsync(this)
    }

    //초기 셋팅
    fun init_setting() {


        location_Ref.get().addOnSuccessListener {

            //파이어베이스 값 읽어오기
            latitude = it.child("latitude").value.toString().toDouble() // String to Double
            longtitude = it.child("longtitude").value.toString().toDouble() // String to Double


            println("latitude : " + latitude) // 올바른 값이 들어왔는지 Check

        }
    }

    // 위도, 경도 이벤트 발생
    fun location_update(){

        location_Ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                latitude = snapshot.child("latitude").value.toString().toDouble()
                longtitude = snapshot.child("longtitude").value.toString().toDouble()
                println("latitude : " + latitude)

                val location = LatLng(latitude, longtitude) // LatLng -> 위도,경도를 담을 수 있는 객체


                //  최신 위치만 마커 표시

                if(::marker.isInitialized){ // 처음 어플을 켰을 때
                    marker.remove()  // 이전의 마크 제거
                    marker = mMap.addMarker(MarkerOptions().position(location).title("도둑(물건) 위치"))!! // 새로운 마크 추가
                }
                else{ // 그 이후
                    marker = mMap.addMarker(MarkerOptions().position(location).title("도둑(물건) 위치"))!!
                }



                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18.5f)) // 카메라 이동
                list.add(location) // 리스트에 현재 위치 추가

                // 물건 위치의 경로 표시
                polyLineOptions.add(location)
                mMap.addPolyline(polyLineOptions)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    // 플래그 이벤트(물건 도난) 발생
    fun flag_update(){

        flag_Ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                var flag : String  = snapshot.value.toString()

                println("flag : " + flag)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val location = LatLng(latitude, longtitude)

        println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
        println("onMapReady latitude : " + latitude)


        mMap.addMarker(MarkerOptions().position(location).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18.5f))

    }


}