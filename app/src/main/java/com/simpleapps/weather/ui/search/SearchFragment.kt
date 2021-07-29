package com.simpleapps.weather.ui.search

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.provider.Settings
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.location.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.simpleapps.weather.R
import com.simpleapps.weather.core.BaseFragment
import com.simpleapps.weather.databinding.FragmentSearchBinding
import com.simpleapps.weather.db.entity.CitiesForSearchEntity
import com.simpleapps.weather.di.Injectable
import com.simpleapps.weather.ui.main.MainActivity
import com.simpleapps.weather.ui.search.result.SearchResultAdapter
import com.simpleapps.weather.utils.CacheUtils
import com.simpleapps.weather.utils.extensions.hideKeyboard
import com.simpleapps.weather.utils.extensions.observeWith
import com.simpleapps.weather.utils.extensions.tryCatch
import java.util.*


class SearchFragment : BaseFragment<SearchViewModel, FragmentSearchBinding>(
    R.layout.fragment_search,
    SearchViewModel::class.java
), Injectable {

    override fun init() {
        super.init()
        initSearchResultsAdapter()
        initSearchView()

        binding.viewModel?.getSearchViewState()?.observeWith(
            viewLifecycleOwner
        ) {
            binding.viewState = it
            it.data?.let { results -> initSearchResultsRecyclerView(results) }
        }
    }

    private fun skip() {
        val pDialog = SweetAlertDialog(activity, SweetAlertDialog.NORMAL_TYPE)
        pDialog.titleText = "Exit App"
        pDialog.contentText = "App Needs Location Permission,Close App Now?"
        pDialog.cancelText = "Cancel"
        pDialog.confirmText = "Exit"
        pDialog.setCancelClickListener {
            pDialog.cancel()
        }
        pDialog.setConfirmClickListener {
            requireActivity().finish()
        }
        pDialog.setCancelable(true)
        pDialog.setCanceledOnTouchOutside(true)
        pDialog.show()
    }

    private fun enablePermission() {
        Dexter.withContext(activity)
            .withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                    p0?.let {
                        if (p0.areAllPermissionsGranted()) {
                            continueAfterGettingLocation()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {

                }
            })
            .check();
    }

    private fun checkPermission(permArr: MutableList<String>): Boolean {
        val context = context
        var counter = 0
        if (context != null) {
            permArr.forEach { permission_name ->
                if (ContextCompat.checkSelfPermission(
                        context,
                        permission_name
                    ) == PERMISSION_GRANTED
                ) {
                    counter++
                }
            }
        }
        return permArr.size == counter
    }

    private fun continueAfterGettingLocation() {
        showSearchScreen()
        if (checkGpsStatus()) {
            fetchLocation()
        } else {
            val intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent1);
        }
    }

    override fun onResume() {
        super.onResume()
        if (checkGpsStatus()) {
            fetchLocation()
        } else {
            val intent1 = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent1);
        }
    }

    fun checkGpsStatus(): Boolean {
        val locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun showSearchScreen() {
        binding.permissionCl.visibility = GONE
        binding.searchingCl.visibility = VISIBLE
    }

    private fun showPermScreen() {
        binding.permissionCl.visibility = VISIBLE
        binding.searchingCl.visibility = GONE
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null
    private fun fetchLocation() {
        Log.d("texts", "fetchLocation: ")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        val permArr: MutableList<String> = mutableListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        Log.d("texts", "fetchLocation: " + checkPermission(permArr))
        if (checkPermission(permArr)) {
            Thread {
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    Log.d("texts", "fetchLocation: " + it)
                    if (it != null) {
                        val latitude = it.latitude
                        val longitude = it.longitude
                        Log.d("texts", """fetchLocation: $latitude $longitude """)
                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                        val addresses: List<Address> =
                            geocoder.getFromLocation(latitude, longitude, 1)
                        try {
                            val city = addresses[0].locality
                            locationFound(city, latitude, longitude)
                        } catch (e: Exception) {
                            Log.d("texts", "fetchLocation: " + e.localizedMessage)
                        }
                    } else {
                        locationRequest = LocationRequest.create()
                        val locationRequest1 = locationRequest
                        if (locationRequest1 != null) {
                            locationRequest1.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            locationRequest1.interval = (20 * 1000).toLong()
                            locationCallback = object : LocationCallback() {
                                override fun onLocationResult(locationResult: LocationResult) {
                                    for (location in locationResult.locations) {
                                        if (location != null) {
                                            try {
                                                val geocoder =
                                                    Geocoder(requireContext(), Locale.getDefault())
                                                val addresses: List<Address> =
                                                    geocoder.getFromLocation(
                                                        location.latitude,
                                                        location.longitude,
                                                        1
                                                    )
                                                val city = addresses[0].locality
                                                locationFound(
                                                    city,
                                                    location.latitude,
                                                    location.longitude
                                                )
                                                CacheUtils.setCache(
                                                    activity,
                                                    city,
                                                    CacheUtils.Companion.CACHEVAL.CITY
                                                )
                                            } catch (e: Exception) {
                                                Log.d(
                                                    "texts",
                                                    "fetchLocation: " + e.localizedMessage
                                                )
                                            }
                                            Log.d(
                                                "texts",
                                                "onLocationResult: " + location.latitude + " " + location.longitude
                                            )
                                            break
                                        }
                                    }
                                }
                            }
                        }
                    }
                }.addOnFailureListener {
                    Log.d("texts", "fetchLocation: " + it.localizedMessage)
                }
            }.start()
        } else {
            showPermScreen()
        }
    }

    private fun locationFound(city: String, latitude: Double, longitude: Double) {
        binding.locationAnimationView.pauseAnimation()
        binding.locationAnimationView.progress = 1.0F
        binding.searchingInfoTv.text = "Location Found"
        binding.cityNameTv.text = city
        binding.continueCityBtn.visibility = VISIBLE
        binding.continueCityBtn.setOnClickListener {
            val viewModel = binding.viewModel
            viewModel?.saveLatLong(latitude.toString(), longitude.toString())
            findNavController().navigate(R.id.action_searchFragment_to_dashboardFragment)
        }
    }

    private fun initSearchView() {
        binding.enablePermBtn.setOnClickListener { enablePermission() }
        binding.skipBtn.setOnClickListener { skip() }
        val permArr: MutableList<String> = mutableListOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (checkPermission(permArr)) {
            continueAfterGettingLocation()
        } else {
            showPermScreen()
        }
    }

    private fun initSearchResultsAdapter() {
        val adapter = SearchResultAdapter { item ->
            item.coord?.let {
                binding.viewModel?.saveCoordsToSharedPref(it)
                    ?.subscribe { _, _ ->

                        tryCatch(
                            tryBlock = {
                                binding.searchView.hideKeyboard((activity as MainActivity))
                            }
                        )
                        findNavController().navigate(R.id.action_searchFragment_to_dashboardFragment)
                    }
            }
        }

        binding.recyclerViewSearchResults.adapter = adapter
        binding.recyclerViewSearchResults.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
    }

    private fun initSearchResultsRecyclerView(list: List<CitiesForSearchEntity>) {
        (binding.recyclerViewSearchResults.adapter as SearchResultAdapter).submitList(list.distinctBy { it.getFullName() }
            .sortedBy { it.importance })
    }


}
