package com.bignerdranch.android.noteyourday

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bignerdranch.android.noteyourday.MemoryList.MemoryListFragment
import java.util.*

private const val TAG = "MainActivity"

private const val CAMERA_PERMISSION_CODE = 3

class MainActivity : AppCompatActivity(), MemoryListFragment.Callbacks{

    private val currentFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragment_container)
    }

    private var isCameraPermissionGranted: Boolean = false

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        isCameraPermissionGranted = isGranted

        if (isCameraPermissionGranted) {
            Toast.makeText(this, "Camera permission granted yay :3", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No camera permission :(", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (currentFragment == null) {
            val fragment = MainFragment()
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit()
        }
    }

    override fun onMemorySelected(memoryId: UUID) {
      val fragment = MemoryFragment.newInstance(memoryId)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container,fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        currentFragment?.let { fragment ->
            (fragment as MemoryFragment).isCameraPermissionGranted = this.isCameraPermissionGranted
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
                Toast.makeText(this, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}