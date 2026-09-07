package com.companion.astrodating.ui.managePhotos.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityManagePhotosBinding
import com.companion.astrodating.ui.home.ui.HomePageActivity
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.managePhotos.domain.model.GetMyPhotoGalleryDomainEntity
import com.companion.astrodating.ui.managePhotos.viewmodel.ManagePhotosViewModel
import com.companion.astrodating.ui.notification.ui.PushNotificationActivity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.util.APP_EMPTY_STRING
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.FROM
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.PERMISSION_REQUEST_CODE
import com.companion.astrodating.util.REGISTRATION
import com.companion.astrodating.util.StorePreferences
import com.companion.astrodating.util.TAG
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showCommonDialogWithButtons
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showLongDurationSnackBar
import com.companion.astrodating.util.showShortDurationSnackBar
import com.companion.astrodating.util.showShortDurationToast
import com.companion.astrodating.util.showVisibility
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@AndroidEntryPoint
class ManagePhotosActivity : BaseActivity(), View.OnClickListener {

    private val binding by lazy {
        ActivityManagePhotosBinding.inflate(layoutInflater)
    }
    private val managePhotosAdapter by lazy {
        ManagePhotosAdapter()
    }
    private var galleyList : ArrayList<GetMyPhotoGalleryDomainEntity>?=null
    private var imageUri: Uri? = null
    private lateinit var loadingDialog: LoadingDialog
    private val managePhotosViewModel: ManagePhotosViewModel by viewModels()
    var layoutManager: GridLayoutManager? = null
    val addGalleryItem = GetMyPhotoGalleryDomainEntity("Add Photo",
        "", "",false, "Add Photo", "")
    var from: String = APP_EMPTY_STRING
    var url: String = APP_EMPTY_STRING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        enableEdgeToEdge()
        initData()
        handleClickEvents()
    }

    private fun handleClickEvents() {
        binding.layoutToolbar.ivToolbarBack.setOnClickListener(this)
        binding.btnNext.setOnClickListener(this)
        managePhotosAdapter.onItemClicked ={it ->
            if (it.status== getString(R.string.text_gallery_status_approved)) {
                showCommonDialogWithButtons(
                    title = getString(R.string.text_gallery_status_profilephoto),
                    description = getString(R.string.dialog_set_profile_desc),
                    btnPositiveText = getString(R.string.text_yes),
                    btnNegativeText = getString(R.string.text_no),
                    actionPositive = {
                        if (isInternetConnection()) {
                            managePhotosViewModel.setProfilePhoto(it.id)
                        } else {
                            binding.root.showLongDurationSnackBar(resources.getString(R.string.text_no_internet_connection))
                        }

                    }
                )
            }

        }
        managePhotosAdapter.onDeleteItemClicked ={
            showCommonDialogWithButtons(
                title = getString(R.string.dialog_delete_photo_title),
                description = getString(R.string.dialog_delete_photo_desc),
                btnPositiveText = getString(R.string.text_yes),
                btnNegativeText = getString(R.string.text_no),
                actionPositive = {
                    if (isInternetConnection()) {
                        managePhotosViewModel.deletePhoto(it.id)
                    } else {
                        binding.root.showLongDurationSnackBar(resources.getString(R.string.text_no_internet_connection))
                    }

                }
            )

        }
        managePhotosAdapter.onAddPhotoItemClicked ={
            requestPermissions(this)
        }


    }

    private fun initData() {
        loadingDialog = LoadingDialog(this)
        galleyList = ArrayList()
        layoutManager = GridLayoutManager(this, 2,GridLayoutManager.VERTICAL, false)
        binding.rvPhotos.layoutManager = layoutManager
        binding.rvPhotos.adapter = managePhotosAdapter

        binding.layoutToolbar.ivTitleLogo.hideVisibility()
        binding.layoutToolbar.ivToolbarBack.showVisibility()
        binding.layoutToolbar.tvTitle.text = getString(R.string.text_uploadkyc_uploadphoto)
        if (intent!=null){
            from = intent.getStringExtra(FROM) ?: APP_EMPTY_STRING
            if (from == REGISTRATION){
                binding.btnNext.showVisibility()
            }else{
                binding.btnNext.hideVisibility()
            }
        }


        if (isInternetConnection()) {
            managePhotosViewModel.getMyPhotos()
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }

    }

    override fun initObservers() {
        managePhotosViewModel.state.observe(this) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    galleyList!!.clear()
                    if (it.data.gallery!=null && it.data.gallery.isNotEmpty()){
                        url = it.data.gallery[0].galleryUrl
                        StorePreferences.saveProfileUrl(url)
                    }
                    galleyList!!.addAll(it.data.gallery)
                    galleyList!!.add(addGalleryItem)
                    if (galleyList!!.isEmpty()) {
                        binding.rvPhotos.hideVisibility()
                    } else {
                        binding.rvPhotos.showVisibility()
                        managePhotosAdapter.submitData(galleyList!!)
                    }
                }
            }
        }

        managePhotosViewModel.setProfileState.observe(this) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    StorePreferences.saveProfileUrl(it.data.profileUrl)
                    galleyList!!.clear()
                    galleyList!!.addAll(it.data.gallery)
                    galleyList!!.add(addGalleryItem)
                    if (galleyList!!.isEmpty()) {
                        binding.rvPhotos.hideVisibility()
                    } else {
                        binding.rvPhotos.showVisibility()
                        managePhotosAdapter.submitData(galleyList!!)
                    }
                }
            }
        }

        managePhotosViewModel.deletePhotoState.observe(this) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    StorePreferences.saveProfileUrl(it.data.profileUrl)
                    galleyList!!.clear()
                    galleyList!!.addAll(it.data.gallery)
                    galleyList!!.add(addGalleryItem)
                    if (galleyList!!.isEmpty()) {
                        binding.rvPhotos.hideVisibility()
                    } else {
                        binding.rvPhotos.showVisibility()
                        managePhotosAdapter.submitData(galleyList!!)
                    }
                }
            }
        }

        managePhotosViewModel.uploadPhotoState.observe(this) {
            when (it) {
                is UiState.Error -> {
                    loadingDialog.hideDialog()
                    if (it.errorCode == ERROR_CODE_LOGOUT) {
                        showLoggedOutDialog() {
                            clearCache()
                            launchScreenAndFinish<SplashActivity>()
                        }
                    } else {
                        showErrorDialog(it.error)
                    }
                }

                UiState.Loading -> {
                    loadingDialog.showDialog()
                }

                is UiState.Success -> {
                    loadingDialog.hideDialog()
                    if (isInternetConnection()) {
                        managePhotosViewModel.getMyPhotos()
                    } else {
                        binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                    }

                }
            }
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed to choose an image
                chooseImageLauncher.launch("image/*")
            } else {
                // Permission denied, show a message to the user
                showShortDurationToast("Permission denied to access gallery")
            }
        }
    }
    private val chooseImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { result: Uri? ->
            result?.let {
                imageUri = it
                if (imageUri != null && checkImageSize(imageUri!!)) {
                    uploadProfilePhoto()
                } else {
                    showShortDurationToast("Image size exceeds 5 MB")
                }
            }
        }

    override fun onClick(p0: View?) {
        when(p0){
            binding.layoutToolbar.ivToolbarBack ->{
                finish()
            }
            binding.btnNext ->{
                Log.e(TAG,"gallery - ${galleyList!!.size}")
                if (galleyList!=null && galleyList!!.size > 1) {
                    if (url != null && url.isNotEmpty()) {
                        if (NotificationManagerCompat.from(this)
                                .areNotificationsEnabled()
                        ) {
                            launchScreenAndFinish<HomePageActivity>() {
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                        } else {
                            launchScreenAndFinish<PushNotificationActivity>()
                        }
                    }
                }else{
                    showShortDurationToast("Please upload at least one photo")
                }

            }
        }
    }

    fun requestPermissions(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissions = arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES)
            ActivityCompat.requestPermissions(activity, permissions, PERMISSION_REQUEST_CODE)
        } else {
            // For older versions, use READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted, request it
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSION_REQUEST_CODE)
            } else {
                // Permission already granted, proceed to choose an image
                chooseImageLauncher.launch("image/*")
            }
        }
    }

    private fun uploadProfilePhoto() {
        if (isInternetConnection()) {
            managePhotosViewModel.uploadPhoto(fileType = "gallery", getMultiPartBody())
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }
    }

    private fun getMultiPartBody(): MultipartBody.Part {
        val filesDir = applicationContext.filesDir
        val file = File(filesDir, "profilePhoto.png")
        val inputStream = imageUri?.let { contentResolver.openInputStream(it) }
        val outputStream = FileOutputStream(file)
        inputStream!!.copyTo(outputStream)
        inputStream.close()
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData("file", file.name, requestBody)
    }

    private fun checkImageSize(imageUri: Uri): Boolean {
        val contentResolver = contentResolver
        val fileDescriptor = contentResolver.openFileDescriptor(imageUri, "r")
        val fileSize = fileDescriptor?.statSize ?: 0

        // Convert bytes to megabytes
        val fileSizeInMB = fileSize / (1024 * 1024)

        // Check if the file size is less than 5 MB
        return fileSizeInMB <= 1
    }
}