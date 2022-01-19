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

    private var database: FirebaseDatabase = FirebaseDatabase.getInstance()
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

    fun init_setting() {

        //초기
        location_Ref.get().addOnSuccessListener {
            latitude = it.child("latitude").value.toString().toDouble()
            longtitude = it.child("longtitude").value.toString().toDouble()

            println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@")
            println("latitude : " + latitude)

        }
    }

    // 위도, 경도 이벤트 발생
    fun location_update(){

        location_Ref.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                latitude = snapshot.child("latitude").value.toString().toDouble()
                longtitude = snapshot.child("longtitude").value.toString().toDouble()
                println("latitude : " + latitude)
                val location = LatLng(latitude, longtitude)


                if(::marker.isInitialized){
                    marker.remove()
                    marker = mMap.addMarker(MarkerOptions().position(location).title("Title"))!!
                }
                else{
                    marker = mMap.addMarker(MarkerOptions().position(location).title("Title"))!!
                }



                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 18.5f))
                list.add(location) // 추가
                polyLineOptions.add(location)
                mMap.addPolyline(polyLineOptions)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    // 플래그 이벤트 발생
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