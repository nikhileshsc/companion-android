package com.companion.astrodating.ui.uploadKyc.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.companion.astrodating.R
import com.companion.astrodating.base.BaseActivity
import com.companion.astrodating.databinding.ActivityUploadKycBinding
import com.companion.astrodating.databinding.BottomDialogChooseImageBinding
import com.companion.astrodating.ui.intro.SplashActivity
import com.companion.astrodating.ui.states.UiState
import com.companion.astrodating.ui.uploadKyc.domain.model.GetKycDomain
import com.companion.astrodating.ui.uploadKyc.viewmodel.UploadKycViewModel
import com.companion.astrodating.util.CAMERA_PERMISSION_CODE
import com.companion.astrodating.util.ERROR_CODE_LOGOUT
import com.companion.astrodating.util.LoadingDialog
import com.companion.astrodating.util.clearCache
import com.companion.astrodating.util.hideVisibility
import com.companion.astrodating.util.invisible
import com.companion.astrodating.util.isInternetConnection
import com.companion.astrodating.util.launchScreenAndFinish
import com.companion.astrodating.util.showErrorDialog
import com.companion.astrodating.util.showLoggedOutDialog
import com.companion.astrodating.util.showShortDurationSnackBar
import com.companion.astrodating.util.showShortDurationToast
import com.companion.astrodating.util.showVisibility
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@AndroidEntryPoint
class UploadKYCActivity : BaseActivity(), View.OnClickListener {

    private val binding by lazy {
        ActivityUploadKycBinding.inflate(layoutInflater)
    }
    private lateinit var loadingDialog: LoadingDialog
    private val uploadKycViewModel: UploadKycViewModel by viewModels()
    var isAadharCardSelected = false
    var isDocumentSelected = false
    var isGetKycApiCalled = false
    var selectedDocumentType = ""
    var selectedDocumentSide = ""
    private var imageUri: Uri? = null
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var bottomDialogChooseImageBinding: BottomDialogChooseImageBinding
    private var getKycDomain: GetKycDomain? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
//        enableEdgeToEdge()
        initData()
        handleClickEvents()
    }

    private fun handleClickEvents() {

        binding.clFront.setOnClickListener(this)
        binding.clBack.setOnClickListener(this)
        binding.ivFront.setOnClickListener(this)
        binding.ivBack.setOnClickListener(this)
        binding.tvAadharCard.setOnClickListener(this)
        binding.tvPanCard.setOnClickListener(this)
        binding.tvDrivingLicense.setOnClickListener(this)
        binding.layoutToolbar.ivToolbarBack.setOnClickListener(this)
        bottomDialogChooseImageBinding.tvCamera.setOnClickListener(this)
        bottomDialogChooseImageBinding.tvGallery.setOnClickListener(this)
        bottomDialogChooseImageBinding.tvCancel.setOnClickListener(this)

    }

    private fun initData() {
        loadingDialog = LoadingDialog(this)

        binding.layoutToolbar.ivTitleLogo.hideVisibility()
        binding.layoutToolbar.ivToolbarBack.showVisibility()
        binding.layoutToolbar.tvTitle.text = getString(R.string.text_uploadkyc_uploadphoto)

        bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        bottomDialogChooseImageBinding = BottomDialogChooseImageBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomDialogChooseImageBinding.root)

        if (isInternetConnection()) {
            uploadKycViewModel.getKyc()
        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }

    }

    override fun initObservers() {
        uploadKycViewModel.state.observe(this) {
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
                    isGetKycApiCalled = true
                    getKycDomain = it.data
                }
            }
        }
        uploadKycViewModel.uploadPhotoState.observe(this) {
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
                        uploadKycViewModel.getKyc()
                    } else {
                        binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
                    }

                }
            }
        }


    }

    override fun onClick(p0: View?) {
        when (p0) {
            binding.clFront, binding.ivFront -> {
                selectedDocumentSide = "frontSide"
                if (!isDocumentSelected) {
                    binding.root.showShortDurationSnackBar(getString(R.string.text_uploadkyc_warning))
                } else {
                    bottomSheetDialog.show()
                }
            }

            binding.clBack, binding.ivBack -> {
                selectedDocumentSide = "backSide"
                if (!isDocumentSelected) {
                    binding.root.showShortDurationSnackBar(getString(R.string.text_uploadkyc_warning))
                } else {
                    bottomSheetDialog.show()
                }
            }

            binding.tvAadharCard -> {
                isDocumentSelected = true
                selectedDocumentType = "AadharCard"
                if (isGetKycApiCalled) {
                    if (getKycDomain!!.aadharCardFront != null) {
                        val frontUrl = getKycDomain!!.aadharCardFront.documentUrl

                        if (frontUrl.isNotEmpty()) {
                            binding.clFront.invisible()
                            binding.ivFront.showVisibility()
                            Glide.with(binding.ivFront.context).load(frontUrl)
                                .error(R.drawable.ic_default_profile)
                                .into(binding.ivFront)
                        } else {
                            binding.clFront.showVisibility()
                            binding.ivFront.invisible()
                        }
                    }
                    if (getKycDomain!!.aadharCardBack != null) {
                        val backUrl = getKycDomain!!.aadharCardBack.documentUrl
                        if (backUrl.isNotEmpty()) {
                            binding.clBack.invisible()
                            binding.ivBack.showVisibility()
                            Glide.with(binding.ivBack.context).load(backUrl)
                                .error(R.drawable.ic_default_profile)
                                .into(binding.ivBack)

                        } else {
                            binding.clBack.showVisibility()
                            binding.ivBack.invisible()
                        }
                    }
                } else {
                    binding.clFront.showVisibility()
                    binding.clBack.showVisibility()
                    binding.ivFront.invisible()
                    binding.ivBack.invisible()
                }
                selectedDocumentColor(binding.tvAadharCard)
                deselectedDocumentColor(binding.tvPanCard)
                deselectedDocumentColor(binding.tvDrivingLicense)

            }

            binding.tvPanCard -> {
                isDocumentSelected = true
                selectedDocumentType = "PanCard"
                if (isGetKycApiCalled) {
                    if (getKycDomain!!.panCardDetailsFront != null) {
                        val frontUrl = getKycDomain!!.panCardDetailsFront.documentUrl

                        if (frontUrl.isNotEmpty()) {
                            binding.clFront.hideVisibility()
                            binding.clBack.hideVisibility()
                            binding.ivBack.hideVisibility()
                            binding.ivFront.showVisibility()
                            Glide.with(binding.ivFront.context).load(frontUrl)
                                .error(R.drawable.ic_default_profile)
                                .into(binding.ivFront)
                        }else{
                            binding.clFront.showVisibility()
                            binding.clBack.hideVisibility()
                            binding.ivFront.invisible()
                            binding.ivBack.invisible()
                        }
                    }
                } else {
                    binding.clFront.showVisibility()
                    binding.clBack.hideVisibility()
                    binding.ivFront.invisible()
                    binding.ivBack.invisible()
                }
                selectedDocumentColor(binding.tvPanCard)
                deselectedDocumentColor(binding.tvAadharCard)
                deselectedDocumentColor(binding.tvDrivingLicense)
            }

            binding.tvDrivingLicense -> {
                isDocumentSelected = true
                selectedDocumentType = "DrivingLicense"
                if (isGetKycApiCalled) {
                    if (getKycDomain!!.drivingLicenseFront != null) {
                        val frontUrl = getKycDomain!!.drivingLicenseFront.documentUrl

                        if (frontUrl.isNotEmpty()) {
                            binding.clFront.hideVisibility()
                            binding.clBack.hideVisibility()
                            binding.ivBack.hideVisibility()
                            binding.ivFront.showVisibility()
                            Glide.with(binding.ivFront.context).load(frontUrl)
                                .error(R.drawable.ic_default_profile)
                                .into(binding.ivFront)
                        }else{
                            binding.clFront.showVisibility()
                            binding.clBack.hideVisibility()
                            binding.ivFront.invisible()
                            binding.ivBack.invisible()
                        }
                    }
                } else {
                    binding.clFront.showVisibility()
                    binding.clBack.hideVisibility()
                    binding.ivFront.invisible()
                    binding.ivBack.invisible()
                }
                selectedDocumentColor(binding.tvDrivingLicense)
                deselectedDocumentColor(binding.tvPanCard)
                deselectedDocumentColor(binding.tvAadharCard)

            }

            binding.layoutToolbar.ivToolbarBack -> {
                finish()
            }

            bottomDialogChooseImageBinding.tvCamera -> {
                bottomSheetDialog.dismiss()
                askCameraPermission()
            }

            bottomDialogChooseImageBinding.tvGallery -> {
                bottomSheetDialog.dismiss()
                openGallery()
            }

            bottomDialogChooseImageBinding.tvCancel -> {
                bottomSheetDialog.cancel()
            }

        }
    }

    fun selectedDocumentColor(textView: TextView) {
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))
        textView.background = ContextCompat.getDrawable(this, R.drawable.bg_rounded_pink_2)
    }

    fun deselectedDocumentColor(textView: TextView) {
        textView.setTextColor(ContextCompat.getColor(this, R.color.black))
        textView.background = ContextCompat.getDrawable(this, R.drawable.bg_rounded_white_2)
    }

    private fun askCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraLauncher.launch(cameraIntent)
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(galleryIntent)
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as Bitmap
                val type = "$selectedDocumentSide$selectedDocumentType".trim()
                imageUri = saveBitmapToFile(this, imageBitmap, "$type.png")

                validateUploadDocument(imageUri!!)

            }
        }

    private fun validateUploadDocument(imageUri: Uri) {
        val type = "$selectedDocumentSide$selectedDocumentType".trim()
        if (selectedDocumentType == "AadharCard") {
            if (selectedDocumentSide == "frontSide") {
                binding.clFront.invisible()
                binding.ivFront.showVisibility()
                if (imageUri != null && checkImageSize(imageUri)) {
                    uploadDocument(type, "$type.png")
                } else {
                    showShortDurationToast("Image size exceeds 5 MB")
                }
                binding.ivFront.setImageURI(imageUri)
            } else if (selectedDocumentSide == "backSide") {
                binding.clBack.invisible()
                binding.ivBack.showVisibility()

                if (imageUri != null && checkImageSize(imageUri)) {
                    uploadDocument(type, "$type.png")
                } else {
                    showShortDurationToast("Image size exceeds 5 MB")
                }
                binding.ivBack.setImageURI(imageUri)
            }
        } else {
            binding.clFront.invisible()
            binding.clBack.hideVisibility()
            binding.ivBack.hideVisibility()
            binding.ivFront.showVisibility()
            if (imageUri != null && checkImageSize(imageUri)) {
                uploadDocument(type, "$type.png")
            } else {
                showShortDurationToast("Image size exceeds 5 MB")
            }

            binding.ivFront.setImageURI(imageUri)

        }
    }

    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imageUri = result.data?.data
                val type = "$selectedDocumentSide$selectedDocumentType".trim()
                validateUploadDocument(imageUri!!)
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                showShortDurationToast("Camera permission is required to use camera.")
            }
        }
    }

    private fun uploadDocument(fileType: String, imageFileName: String) {
        if (isInternetConnection()) {
            println("File type - $fileType, imageFileName-$imageFileName")
            uploadKycViewModel.uploadDocument(fileType = fileType, getMultiPartBody(imageFileName))

        } else {
            binding.root.showShortDurationSnackBar(getString(R.string.text_no_internet_connection))
        }
    }

    private fun getMultiPartBody(imageFileName: String): MultipartBody.Part {
        val filesDir = applicationContext.filesDir
        val file = File(filesDir, imageFileName)
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

    fun saveBitmapToFile(context: Context, bitmap: Bitmap, fileName: String?): Uri? {
        var fos: FileOutputStream? = null
        return try {
            val folderName = "Companion"
            val directory = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                folderName
            )
            if (!directory.exists()) {
                directory.mkdirs() // Create the directory if it doesn't exist
            }
            val file = File(directory, fileName)
            fos = FileOutputStream(file)
            // Compress the bitmap and write it to the file
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            // Return the URI of the saved file
            Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            if (fos != null) {
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }
}