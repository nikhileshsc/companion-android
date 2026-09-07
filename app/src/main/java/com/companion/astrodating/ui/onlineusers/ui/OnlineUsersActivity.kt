package com.companion.astrodating.ui.onlineusers.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityOnlineUsersBinding
import com.companion.astrodating.ui.onlineusers.adapter.OnlineUsersAdapter
import com.companion.astrodating.ui.onlineusers.domain.model.GetOnlineUsersDomainEntity
import com.companion.astrodating.ui.onlineusers.viewmodel.OnlineUsersViewModel
import com.companion.astrodating.ui.profileDetails.ui.ProfileDetailsActivity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.USER_ID
import com.companion.astrodating.util.launchScreen
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnlineUsersActivity : BaseActivity() {

    private lateinit var binding: ActivityOnlineUsersBinding
    private lateinit var adapter: OnlineUsersAdapter

    private lateinit var loadingDialog: LoadingDialog

    private val viewModel: OnlineUsersViewModel by viewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnlineUsersBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(this)
        setContentView(binding.root)
//        enableEdgeToEdge()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        setupRecyclerView()
        initObservers()
        requestLocationAndFetchUsers(this,1001)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                requestLocationAndFetchUsers(this,1001)
            } else {
                // Check if "Don't ask again" was selected
                val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )

                if (!showRationale) {
                    // User has permanently denied permission — guide to app settings
                    showSettingsDialog()
                } else {
                    Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun showSettingsDialog() {
        MaterialAlertDialogBuilder(this@OnlineUsersActivity, R.style.RoundedAlertDialog)
            .setTitle("Permission Required")
            .setMessage("Location permission is permanently denied. Some features won't work without location permission.")
            .setPositiveButton("Open Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.parse("package:$packageName")
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }





    private fun setupRecyclerView() {
        adapter = OnlineUsersAdapter { selectedUser ->
            // Handle user click here (e.g., navigate to profile)
//            val intent = Intent(this, ProfileDetailsActivity::class.java)
//            intent.putExtra(USER_ID, selectedUser._id) // or "user" if passing whole object
//            startActivity(intent)
            launchScreen<ProfileDetailsActivity>{
                putExtra(USER_ID, selectedUser._id)
            }
        }

        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@OnlineUsersActivity, 2)
            adapter = this@OnlineUsersActivity.adapter
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestLocationAndFetchUsers(activity: Activity,REQUEST_CHECK_SETTINGS: Int) {
        if (!isLocationEnabled()) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
                .setMinUpdateIntervalMillis(5000L)
                .build()


            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val client: SettingsClient = LocationServices.getSettingsClient(activity)
            client.checkLocationSettings(builder.build())
                .addOnSuccessListener { locationSettingsResponse ->
                    // Location settings are satisfied
                }
                .addOnFailureListener { e ->
                    if (e is ResolvableApiException) {
                        try {
                            e.startResolutionForResult(activity, REQUEST_CHECK_SETTINGS)
                        } catch (sendEx: SendIntentException) {
                            sendEx.printStackTrace()
                        }
                    }
                }
//            Toast.makeText(this, "Please enable location services", Toast.LENGTH_LONG).show()
//            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
            return
        }

        // Check permissions
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                1001
            )
            return
        }

        val cancellationTokenSource = CancellationTokenSource()

        // First try current location
        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource.token
        ).addOnSuccessListener { location ->
            if (location != null) {
                val lat = location.latitude
                val lng = location.longitude
                Log.d("Location", "Lat: $lat, Lng: $lng")
                viewModel.getOnlineUsers(1, 20, lat, lng)
            } else {
                // No current location available, try last known location as fallback
                fetchLastKnownLocation()
            }
        }.addOnFailureListener {
            // Fallback if current location fails
            fetchLastKnownLocation()
        }
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun fetchLastKnownLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { lastLocation ->
                if (lastLocation != null) {
                    val lat = lastLocation.latitude
                    val lng = lastLocation.longitude
                    Log.d("Location", "Fallback LastLocation: $lat, $lng")
                    viewModel.getOnlineUsers(1, 20, lat, lng)
                } else {
                    Toast.makeText(
                        this,
                        "Unable to fetch location. Please try again.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get location", Toast.LENGTH_SHORT).show()
            }
    }




    override fun initObservers() {
        viewModel.onlineUsersList.observe(this) { state ->
            when (state) {
                is UiState.Loading -> {
                    loadingDialog.showDialog()
                    binding.tvTitle.text = "Loading..."
//                    binding.tvLoading.visibility = View.VISIBLE
//                    binding.tvNoUsers.visibility = View.GONE
                    binding.recyclerView.visibility = View.GONE
                }
                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    binding.tvTitle.text = "Online Users"
//                    binding.tvLoading.visibility = View.GONE
                    if (state.data.users.isEmpty()) {
                        binding.tvNoUsers.visibility = View.VISIBLE
                        binding.recyclerView.visibility = View.GONE
                    } else {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.tvNoUsers.visibility = View.GONE
                        adapter.setList(state.data.users)
                    }
                }
                is UiState.Error -> {
                    loadingDialog.hideDialog()
//                    binding.tvTitle.text = "Failed to Load"
//                    binding.tvLoading.visibility = View.GONE
//                    binding.tvNoUsers.visibility = View.VISIBLE
                    binding.recyclerView.visibility = View.GONE
                }
            }
        }

    }


}
