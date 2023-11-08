package com.bignerdranch.android.noteyourday

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bignerdranch.android.noteyourday.Memory.MemoryFragment
import com.bignerdranch.android.noteyourday.MemoryList.MemoryListFragment
import java.util.UUID

const val GALLERY = 5
private const val CAMERA_AND_STORAGE_PERMISSION_CODE = 3


class MainActivity : AppCompatActivity(), MemoryListFragment.Callbacks {

    private val currentFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.fragment_container)
    }

    private var isCameraPermissionGranted: Boolean = false

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
            .replace(R.id.fragment_container, fragment)
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
        if (requestCode == CAMERA_AND_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}