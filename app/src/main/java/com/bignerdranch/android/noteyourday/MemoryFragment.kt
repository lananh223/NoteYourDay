package com.bignerdranch.android.noteyourday

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import com.bignerdranch.android.noteyourday.MemoryList.MemoryDetailViewModel
import com.bignerdranch.android.noteyourday.MemoryList.MemoryListFragment
import com.bignerdranch.android.noteyourday.databinding.FragmentMemoriesOfTheDayBinding
import java.io.File
import java.util.*
import java.util.jar.Manifest

private const val TAG = "MemoryFragment"
private const val ARG_MEMORY_ID = "memory_id"
private const val DIALOG_DATE="DialogDate"
private const val REQUEST_DATE = 0
private const val DATE_FORMAT = "MMM-dd"
private const val REQUEST_PHOTO = 2

class MemoryFragment: Fragment(), DatePickerFragment.Callbacks, ActivityResultCallback<Boolean> {

    private var _binding: FragmentMemoriesOfTheDayBinding? = null
    private val binding get() = _binding!!
    private lateinit var memory: Memory
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private val memoryDetailViewModel:MemoryDetailViewModel by lazy{
        ViewModelProviders.of(this).get(MemoryDetailViewModel::class.java)
    }

    var isCameraPermissionGranted: Boolean = false

    // Create argument bundle to pass data around (memoryId), after that, update in MainActivity
    companion object{

        fun newInstance(memoryId: UUID):MemoryFragment{
            val args =  Bundle().apply{
                putSerializable(ARG_MEMORY_ID, memoryId)
            }
            return MemoryFragment().apply {
                arguments = args
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memory = Memory()
        //retrieve the UUID from fragment arguments(458)
        val memoryId:UUID = arguments?.getSerializable(ARG_MEMORY_ID) as UUID
        memoryDetailViewModel.loadMemory(memoryId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMemoriesOfTheDayBinding.inflate(inflater, container, false)
        val view = binding.root

        _binding!!.dateButton.setOnClickListener {
                DatePickerFragment.newInstance(memory.date).apply{
                    setTargetFragment(this@MemoryFragment, REQUEST_DATE)
                    show(this@MemoryFragment.requireFragmentManager(), DIALOG_DATE)
                }
            }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(requireActivity(), "Permission granted for camera :v", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireActivity(), "No permissions :(", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        memoryDetailViewModel.memoryLiveData.observe(
            viewLifecycleOwner,
            Observer { memory ->
                memory?.let {
                    this.memory = memory
                    photoFile = memoryDetailViewModel.getPhotoFile(memory)
                    // translate local file path into Uri
                    photoUri = FileProvider.getUriForFile(requireActivity(),
                    "com.bignerdranch.android.noteyourday.fileprovider",photoFile)
                    updateUI()
                }
            }
        )
    }
    override fun onStart() {
        super.onStart()

        val titleWatcher = object : TextWatcher {

            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This space intentionally left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                memory.detail = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
            }
        }

        _binding?.detailOfMemory?.addTextChangedListener(titleWatcher)

        _binding?.shareButton?.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply{
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT,getMemorySharing())
            }
                    // choose recipient
                .also{ intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.bbbb))
                startActivity(chooserIntent)
            }
        }

        _binding?.memoryCamera?.apply {
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolveActivity: ResolveInfo?=
                packageManager.resolveActivity(captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY)
            if (resolveActivity == null) {
                isEnabled = false
            }

            setOnClickListener{
                if (checkPermission()) {
                    // Permission granted, so open camera
                    startCamera(captureImage, packageManager)
                }
            }
                }

        _binding?.pictureOfMemory?.setOnLongClickListener {view: View ->
            Intent(Intent.ACTION_SEND).apply{
                type = "image/jpeg"
                putExtra(Intent.EXTRA_STREAM,photoUri)
            }
                // choose recipient
                .also{ intent ->
                    val chooserIntent = Intent.createChooser(intent, getString(R.string.bbbb))
                    startActivity(chooserIntent)
                }
            true
        }

        _binding?.doneButton?.setOnClickListener { view: View ->
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, MemoryListFragment())
                .commit()
        }
    }

    private fun MemoryFragment.startCamera(
        captureImage: Intent,
        packageManager: PackageManager
    ) {
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)

        val cameraActivities: List<ResolveInfo> =
            packageManager.queryIntentActivities(
                captureImage,
                PackageManager.MATCH_DEFAULT_ONLY
            )

        for (cameraActivity in cameraActivities) {
            requireActivity().grantUriPermission(
                cameraActivity.activityInfo.packageName, photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
        startActivityForResult(captureImage, REQUEST_PHOTO)
    }

    override fun onStop() {
        super.onStop()
        memoryDetailViewModel.saveMemory(memory)
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().revokeUriPermission(photoUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    }

    override fun onDateSelected(date: Date) {
        memory.date = date
        updateUI()
    }

    private fun updateUI(){
        _binding?.detailOfMemory?.setText(memory.detail)
        _binding?.dateButton?.text = memory.date.toString()
        updatePhotoView()
    }

    private fun updatePhotoView() {
        if(photoFile.exists()){
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
            _binding?.pictureOfMemory?.setImageBitmap(bitmap)
            _binding?.pictureOfMemory?.contentDescription=
                getString(R.string.memory_photo_image_description)
        } else {
            _binding?.pictureOfMemory?.setImageDrawable(null)
            _binding?.pictureOfMemory?.contentDescription =
                getString(R.string.memory_photo_no_image_description)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            requestCode == REQUEST_PHOTO -> {
                requireActivity().revokeUriPermission(
                    photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                updatePhotoView()
            }
        }
    }

    @SuppressLint("StringFormatInvalid")
    private fun getMemorySharing(): String {
        val dateString = DateFormat.format(DATE_FORMAT, memory.date).toString()
        return getString(R.string.memory_sharing) +" " + dateString +", "+ memory.detail
    }

    override fun onActivityResult(result: Boolean?) {
        TODO("Not yet implemented")
    }

    private fun checkPermission(): Boolean {
        if(ContextCompat.checkSelfPermission(
            requireActivity(),
            android.Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.CAMERA), 3)
            return false
        } else {
            return true
        }
    }
}
