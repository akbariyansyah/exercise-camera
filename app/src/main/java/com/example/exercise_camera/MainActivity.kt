package com.example.exercise_camera

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    val OPEN_CAMERA_REQUEST_CODE = 10
    val OPEN_STORAGE_REQUEST_CODE = 13
    val READ_STORAGE_REQUEST_CODE = 200

    lateinit var currentPhotoPath: String
    lateinit var photoFile: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkStoragePermission()
    }

    fun browseFile(v: View) {
        val selectedFileIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        startActivityForResult(selectedFileIntent, OPEN_STORAGE_REQUEST_CODE)
    }


    fun openCamera(v: View) {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.resolveActivity(packageManager)
        photoFile = createImageFile()
        val photoURI =
            FileProvider.getUriForFile(this, "com.example.exercise_camera.FileProvider", photoFile)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        startActivityForResult(cameraIntent, OPEN_CAMERA_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OPEN_CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            val imageBitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
            imageView5.setImageBitmap(imageBitmap)

        } else if (requestCode == OPEN_STORAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {

            println("DARI STORAGE....")
            val fileUri = data?.data
//                 imageView5.setImageURI(fileUri)
            val photoFile = File(fileUri?.path)
            val originalPath = getOriginalPathFromUri(fileUri!!)

            // INI FILE DIUPLOAD
            val imageFile = File(originalPath)

            imageView5.setImageBitmap(BitmapFactory.decodeFile(imageFile.absolutePath))

        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
            println(currentPhotoPath)

        }
    }


    fun getOriginalPathFromUri(contentUri: Uri): String? {
        var originalPath: String? = null
        val projection =
            arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = contentResolver.query(contentUri, projection, null, null, null)
        if (cursor?.moveToFirst()!!) {
            val columnIndex: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            originalPath = cursor.getString(columnIndex)
        }
        return originalPath
    }

    fun checkStoragePermission() {
        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
                , READ_STORAGE_REQUEST_CODE
            )
        }
    }


}