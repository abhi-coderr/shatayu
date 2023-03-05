package com.example.shatayu.ui.activity

//noinspection SuspiciousImport
import android.Manifest
import android.R
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Camera
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.shatayu.databinding.ActivityUploadVideoBinding
import com.example.shatayu.utils.Const.Companion.CAMERA_REQUEST_CODE
import com.example.shatayu.utils.Const.Companion.VIDEO_PICK_CAMERA_CODE
import com.example.shatayu.utils.Const.Companion.VIDEO_PICK_GALLERY_CODE
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer

class UploadVideoActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var activityUploadVideoBinding: ActivityUploadVideoBinding
    private val years =
        arrayOf("select disease", "Prostate", "Cervical", "Hip replacement", "Other")

    //camera request permissions
    private lateinit var cameraPermissions: Array<String>
    private var videoUri: Uri? = null // uri for pick video

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityUploadVideoBinding = ActivityUploadVideoBinding.inflate(layoutInflater)
        setContentView(activityUploadVideoBinding.root)

        permissionExternalStorage()
        setSpinner()

        videoUpload()
    }

    private fun videoUpload() {
        cameraPermissions =
            arrayOf(Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        //handle click event of upload button
        activityUploadVideoBinding.uploadVideoBtn.setOnClickListener { }

        //handle click event of pick button
        activityUploadVideoBinding.getVideoBtn.setOnClickListener {
            Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show()
            videoPickDialog()
        }
    }

    private fun videoPickDialog() {
        //options to display in dialog
        val options = arrayOf("Gallery", "Camera")
        // alert dialog
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Pick Testimony Form")
            .setItems(options) { dialogInterface, i ->
                if (i == 0) {
                    // camera
                    if (!checkCameraPermission()) {
                        //permission not allow
                        requestCameraPermission()
                    } else {
                        pickVideoFromGallery()
                    }
                } else {
                    //gallery
                    pickVideoFromCamera()
                }
            }
        builder.show()
    }

    private fun setVideoToPlayer() {
        //set the picked video to video view
        // video play controls

//        val mediaController = MediaController(this)
//        mediaController.setAnchorView(activityUploadVideoBinding.videoPlayer)
//
//        //set media controller
//        activityUploadVideoBinding.videoPlayer.setMediaController(mediaController)
//        //set video uri
//        activityUploadVideoBinding.videoPlayer.setVideoURI(videoUri)
//        activityUploadVideoBinding.videoPlayer.requestFocus()
//        activityUploadVideoBinding.videoPlayer.setOnPreparedListener {
//            //when video is ready, by default don't play automatically
//            activityUploadVideoBinding.videoPlayer.pause()
//        }

        val exoPlayer = SimpleExoPlayer.Builder(this).build()
        activityUploadVideoBinding.videoPlayer.player = exoPlayer
        val mediaItem = videoUri?.let { MediaItem.fromUri(it) }
        mediaItem?.let {
            exoPlayer.setMediaItem(mediaItem)
        }
        exoPlayer.prepare()
        exoPlayer.pause()
    }

    fun permissionExternalStorage() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(
                    this,
                    cameraPermissions,
                    CAMERA_REQUEST_CODE
                )
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

        }
    }

    private fun requestCameraPermission() {
        //request camera permission
        ActivityCompat.requestPermissions(
            this,
            cameraPermissions,
            CAMERA_REQUEST_CODE
        )
    }

    private fun checkCameraPermission(): Boolean {

        val result1 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val result2 = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        return result1 && result2
    }

    private fun pickVideoFromGallery() {

        //video pick intent gallery
        val intent = Intent()
        intent.type = "video/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(
            Intent.createChooser(intent, "Choose Testimony"),
            VIDEO_PICK_GALLERY_CODE
        )

    }

    private fun pickVideoFromCamera() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        startActivityForResult(intent, VIDEO_PICK_GALLERY_CODE)
    }

    private fun setSpinner() {

        val langAdapter = ArrayAdapter<CharSequence>(this, R.layout.simple_spinner_item, years)

        activityUploadVideoBinding.spinnerDiseaseCategory.adapter = langAdapter

        // on below line we are adding click listener for our spinner
        activityUploadVideoBinding.spinnerDiseaseCategory.onItemSelectedListener = this

    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Toast.makeText(this, years[p2], Toast.LENGTH_SHORT).show()
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

    // handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_REQUEST_CODE ->
                if (grantResults.isNotEmpty()) {
                    //check if permission is allow or denied
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (cameraAccepted && storageAccepted) {
                        // both permission allowed
                        pickVideoFromGallery()
                    } else {
                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // handle video pick result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            //video is picked from camera or gallery
            if (requestCode == VIDEO_PICK_CAMERA_CODE) {
                // pick from camera
                videoUri = data?.data
                setVideoToPlayer()
            } else if (requestCode == VIDEO_PICK_GALLERY_CODE) {
                // pick from gallery
                videoUri = data?.data
                setVideoToPlayer()
            }
        } else {
            // cancel picking video
            Toast.makeText(this, "Cancelled Testimony", Toast.LENGTH_SHORT).show()
        }

    }
}

















