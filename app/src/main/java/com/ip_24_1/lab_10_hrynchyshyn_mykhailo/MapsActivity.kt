package com.ip_24_1.lab_10_hrynchyshyn_mykhailo

import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var tvSelectedAddress: TextView
    private lateinit var etMapTaskTitle: EditText
    private lateinit var taskPreferences: TaskPreferences

    private var selectedLatLng: LatLng? = null
    private var selectedAddressText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        taskPreferences = TaskPreferences(this)
        tvSelectedAddress = findViewById(R.id.tvSelectedAddress)
        etMapTaskTitle = findViewById(R.id.etMapTaskTitle)
        val btnSaveLocationTask: Button = findViewById(R.id.btnSaveLocationTask)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        btnSaveLocationTask.setOnClickListener {
            val title = etMapTaskTitle.text.toString().trim()
            val point = selectedLatLng

            if (title.isEmpty()) {
                Toast.makeText(this, "Введіть назву завдання", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (point == null) {
                Toast.makeText(this, "Оберіть точку на карті", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fullLocationInfo = "$selectedAddressText (${String.format(Locale.US, "%.4f, %.4f", point.latitude, point.longitude)})"
            val newTask = Task(
                title = title,
                address = fullLocationInfo,
                latitude = point.latitude,
                longitude = point.longitude
            )
            taskPreferences.addTask(newTask)
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Початкова позиція камери: центр Івано-Франківська
        val cityCenter = LatLng(48.9226, 24.7111)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cityCenter, 13f))

        // Додавання маркера та визначення адреси при натисканні на карту
        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            selectedLatLng = latLng
            mMap.addMarker(MarkerOptions().position(latLng).title("Обрана локація"))
            resolveAddress(latLng)
        }
    }

    private fun resolveAddress(latLng: LatLng) {
        tvSelectedAddress.text = "Визначення адреси..."
        val geocoder = Geocoder(this, Locale("uk", "UA"))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1) { addresses ->
                runOnUiThread {
                    val addr = addresses.firstOrNull()?.getAddressLine(0)
                        ?: "Адресу не знайдено"
                    selectedAddressText = addr
                    tvSelectedAddress.text = "Адреса: $addr"
                }
            }
        } else {
            @Suppress("DEPRECATION")
            val addresses = try {
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            } catch (e: Exception) {
                null
            }
            val addr = addresses?.firstOrNull()?.getAddressLine(0) ?: "Адресу не знайдено"
            selectedAddressText = addr
            tvSelectedAddress.text = "Адреса: $addr"
        }
    }
}