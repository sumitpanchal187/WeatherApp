package com.example.uffiziopracticaltask.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.uffiziopracticaltask.adapter.SearchAdapter
import com.example.uffiziopracticaltask.adapter.WeatherAdapter
import com.example.uffiziopracticaltask.databinding.ActivityHomeBinding
import com.example.uffiziopracticaltask.network.RetrofitClient
import com.example.uffiziopracticaltask.repository.WeatherRepository
import com.example.uffiziopracticaltask.room.database.WeatherDatabase
import com.example.uffiziopracticaltask.viewModel.WeatherViewModel
import com.google.android.gms.location.LocationServices
import com.google.android.material.color.DynamicColors
import java.util.Locale
import java.util.concurrent.Executors

class HomeActivity : AppCompatActivity() {

    private lateinit var viewModel: WeatherViewModel
    private lateinit var binding: ActivityHomeBinding
    private val LOCATION_PERMISSION_CODE = 100

    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val executor = Executors.newSingleThreadExecutor()
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DynamicColors.applyToActivitiesIfAvailable(application)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val db = WeatherDatabase.getDatabase(this)
        val repo = WeatherRepository(RetrofitClient.api, db.weatherDao())
        viewModel = WeatherViewModel(repo)

        binding.recyclerSearch.layoutManager = LinearLayoutManager(this)
        binding.recyclerWeather.layoutManager = LinearLayoutManager(this)
        binding.recyclerWeather.adapter = WeatherAdapter(emptyList())

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                searchRunnable?.let { searchHandler.removeCallbacks(it) }

                if (newText.isNullOrEmpty()) {
                    binding.recyclerSearch.visibility = View.GONE
                    return true
                }

                searchRunnable = Runnable {
                    performGeocoderSearch(newText)
                }
                searchHandler.postDelayed(searchRunnable!!, 400)

                return true
            }
        })

        viewModel.weather.observe(this) {
            binding.recyclerWeather.adapter = WeatherAdapter(it)
            binding.progressLoader.visibility = View.GONE
            binding.recyclerWeather.visibility = View.VISIBLE
            binding.swipeRefresh.isRefreshing = false
        }

        binding.fabLocation.setOnClickListener {
            hideKeyboard()
            binding.searchView.clearFocus()
            getCurrentLocation()
        }

        binding.swipeRefresh.setOnRefreshListener {
            getCurrentLocation()
        }

        checkLocationPermission()
    }

    private fun performGeocoderSearch(query: String) {
        executor.execute {
            try {
                val geocoder = Geocoder(this@HomeActivity, Locale.getDefault())
                val list = geocoder.getFromLocationName(query, 5)
                val cityList = list?.mapNotNull {
                    it.locality ?: it.featureName?.takeIf { name -> name.isNotBlank() }
                }?.distinct() ?: emptyList()

                mainHandler.post {
                    if (!isDestroyed && !isFinishing) {
                        if (cityList.isEmpty()) {
                            binding.recyclerSearch.visibility = View.GONE
                        } else {
                            binding.recyclerSearch.adapter = SearchAdapter(cityList) { city ->
                                searchCity(city)
                            }
                            binding.recyclerSearch.visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mainHandler.post {
                    binding.recyclerSearch.visibility = View.GONE
                }
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        }
    }

    private fun getCurrentLocation() {
        binding.progressLoader.visibility = View.VISIBLE
        binding.recyclerWeather.visibility = View.GONE
        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
        hideKeyboard()
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lon = location.longitude
                viewModel.loadWeather(lat, lon)

                executor.execute {
                    try {
                        val geocoder = Geocoder(this, Locale.getDefault())
                        val addresses = geocoder.getFromLocation(lat, lon, 1)
                        mainHandler.post {
                            if (!isDestroyed && !isFinishing) {
                                if (!addresses.isNullOrEmpty()) {
                                    val city = addresses[0].locality
                                    val country = addresses[0].countryName
                                    binding.txtLocation.text = "$city, $country"
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else {
                binding.txtLocation.text = "Location not available"
                viewModel.loadWeather(23.0225, 72.5714)
            }
        }
    }

    private fun searchCity(city: String) {
        binding.progressLoader.visibility = View.VISIBLE
        binding.recyclerWeather.visibility = View.GONE

        binding.recyclerSearch.visibility = View.GONE
        binding.searchView.setQuery("", false)
        binding.searchView.clearFocus()
        hideKeyboard()

        executor.execute {
            try {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocationName(city, 1)
                mainHandler.post {
                    if (!isDestroyed && !isFinishing) {
                        if (!addresses.isNullOrEmpty()) {
                            val lat = addresses[0].latitude
                            val lon = addresses[0].longitude
                            binding.txtLocation.text = city
                            viewModel.loadWeather(lat, lon)
                        } else {
                            binding.progressLoader.visibility = View.GONE
                            binding.recyclerWeather.visibility = View.VISIBLE
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mainHandler.post {
                    binding.progressLoader.visibility = View.GONE
                    binding.recyclerWeather.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getCurrentLocation()
        }
    }

    private fun hideKeyboard() {
        val imm =
            getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let { searchHandler.removeCallbacks(it) }
        executor.shutdown()
    }
}