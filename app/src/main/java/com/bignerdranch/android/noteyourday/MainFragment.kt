package com.bignerdranch.android.noteyourday

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bignerdranch.android.noteyourday.MemoryList.MemoryListFragment
import com.bignerdranch.android.noteyourday.databinding.FragmentMainBinding

private const val KEY_NAME = "name"

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.qualityFiveImage.setOnClickListener { view: View ->
            Toast.makeText(
                requireContext(),
                R.string.yay_feeling,
                Toast.LENGTH_SHORT
            )
                .show()
        }

        binding.qualityFourImage.setOnClickListener { view: View ->
            Toast.makeText(
                requireContext(),
                R.string.yay_feeling,
                Toast.LENGTH_SHORT
            )
                .show()
        }

        binding.qualityThreeImage.setOnClickListener { view: View ->
            Toast.makeText(
                requireContext(),
                R.string.better_feeling,
                Toast.LENGTH_SHORT
            )
                .show()
        }

        binding.qualityTwoImage.setOnClickListener { view: View ->
            Toast.makeText(
                requireContext(),
                R.string.better_feeling,
                Toast.LENGTH_SHORT
            )
                .show()
        }

        binding.qualityOneImage.setOnClickListener { view: View ->
            Toast.makeText(
                requireContext(),
                R.string.sad_feeling,
                Toast.LENGTH_SHORT
            )
                .show()
        }

        binding.qualityZeroImage.setOnClickListener { view: View ->
            Toast.makeText(
                requireContext(),
                R.string.sad_feeling,
                Toast.LENGTH_SHORT
            )
                .show()
        }

        binding.monthlyJourney.setOnClickListener { view: View ->
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, MemoryListFragment())
                .commit()
        }

        binding.doneButton.setOnClickListener {
            addName()
            saveUsername()
        }
        binding.nameText.setOnClickListener {
            updateName()
        }

        updateViews()

        return view
    }

    private fun addName() {
        binding.nameText.text = "Hi " + binding.nameEdit.text
        binding.nameEdit.visibility = View.GONE
        binding.doneButton.visibility = View.GONE
        binding.nameText.visibility = View.VISIBLE
    }

    // Change the name if users put wrong
    private fun updateName() {
        binding.nameEdit.visibility = View.VISIBLE
        binding.doneButton.visibility = View.VISIBLE
        binding.nameText.visibility = View.GONE
    }

    // save username and show up the next time using sharedPreference
    private fun saveUsername() {
        val sharedPreferences = requireActivity().getPreferences(MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            val name = binding.nameText.text.toString()
            putString(KEY_NAME, name)
            apply()
        }
    }

    private fun getUsername(): String? {
        val sharedPreferences = requireActivity().getPreferences(MODE_PRIVATE)
        return sharedPreferences.getString(KEY_NAME, "")
    }

    private fun updateViews() {
        binding.nameText.text = getUsername()

        if (getUsername() != "") {
            binding.nameText.visibility = View.VISIBLE
            binding.nameEdit.visibility = View.GONE
            binding.doneButton.visibility = View.GONE
        }
    }
}

