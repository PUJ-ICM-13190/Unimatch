package com.ajiaco.unimatch

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit

class MapaEventoActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var eventoLatitud: Double = 0.0
    private var eventoLongitud: Double = 0.0
    private var eventoTitulo: String = ""
    private var eventoLugar: String = ""
    private var currentPolyline: Polyline? = null
    private var currentLocationMarker: Marker? = null
    private lateinit var geoApiContext: GeoApiContext

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                // Permiso preciso concedido
                setupLocationAndRoute()
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                // Solo permiso aproximado concedido
                setupLocationAndRoute()
            }
            else -> {
                // No hay permisos concedidos
                Toast.makeText(this, "Se necesitan permisos de ubicación para mostrar la ruta", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_evento)

        // Inicializar el cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Inicializar el contexto de la API de direcciones
        geoApiContext = GeoApiContext.Builder()
            .apiKey("AIzaSyDb4gtVD7Mxeh5QTX7nOHCExfJPMK3FAh4")
            .connectTimeout(2, TimeUnit.SECONDS)
            .readTimeout(2, TimeUnit.SECONDS)
            .writeTimeout(2, TimeUnit.SECONDS)
            .build()

        // Obtener datos del intent
        eventoLatitud = intent.getDoubleExtra("latitud", 0.0)
        eventoLongitud = intent.getDoubleExtra("longitud", 0.0)
        eventoTitulo = intent.getStringExtra("titulo") ?: ""
        eventoLugar = intent.getStringExtra("lugar") ?: ""

        // Configurar toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Ubicación del evento"

        // Obtener el fragmento del mapa
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Configurar el mapa
        setupMap()

        // Verificar y solicitar permisos
        checkLocationPermission()
    }

    private fun setupMap() {
        // Crear la ubicación del evento
        val eventoUbicacion = LatLng(eventoLatitud, eventoLongitud)

        // Añadir marcador del evento
        mMap.addMarker(MarkerOptions()
            .position(eventoUbicacion)
            .title(eventoTitulo)
            .snippet(eventoLugar))

        // Configurar UI del mapa
        mMap.uiSettings.apply {
            isZoomControlsEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
            isMyLocationButtonEnabled = true
        }
    }

    private fun checkLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                setupLocationAndRoute()
            }
            else -> {
                locationPermissionRequest.launch(arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ))
            }
        }
    }

    private fun setupLocationAndRoute() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true

            // Obtener ubicación actual
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val currentLatLng = LatLng(it.latitude, it.longitude)

                    // Actualizar marcador de ubicación actual
                    currentLocationMarker?.remove()
                    currentLocationMarker = mMap.addMarker(
                        MarkerOptions()
                            .position(currentLatLng)
                            .title("Mi ubicación")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    )

                    // Centrar el mapa para mostrar ambos puntos
                    val bounds = LatLngBounds.Builder()
                        .include(currentLatLng)
                        .include(LatLng(eventoLatitud, eventoLongitud))
                        .build()
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

                    // Calcular y mostrar la ruta
                    calculateRoute(currentLatLng)
                }
            }
        }
    }

    private fun calculateRoute(origin: LatLng) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = DirectionsApi.newRequest(geoApiContext)
                    .origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
                    .destination(com.google.maps.model.LatLng(eventoLatitud, eventoLongitud))
                    .mode(TravelMode.DRIVING)
                    .await()

                withContext(Dispatchers.Main) {
                    addRouteToMap(result)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MapaEventoActivity,
                        "Error al calcular la ruta: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun addRouteToMap(result: DirectionsResult) {
        // Remover ruta anterior si existe
        currentPolyline?.remove()

        // Convertir el resultado a una lista de puntos para el mapa
        val decodedPath = result.routes[0].overviewPolyline.decodePath()
        val points = decodedPath.map { LatLng(it.lat, it.lng) }

        // Dibujar la nueva ruta
        currentPolyline = mMap.addPolyline(
            PolylineOptions()
                .addAll(points)
                .color(Color.BLUE)
                .width(10f)
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        geoApiContext.shutdown()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}