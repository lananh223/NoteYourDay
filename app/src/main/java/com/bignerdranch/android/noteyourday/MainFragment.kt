package com.bignerdranch.android.noteyourday

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bignerdranch.android.noteyourday.MemoryList.MemoryListFragment
import com.bignerdranch.android.noteyourday.databinding.FragmentMainBinding
import android.content.SharedPreferences
import android.widget.Toast

import android.content.Context.MODE_PRIVATE
import android.content.Context.MODE_PRIVATE

private const val KEY_NAME = "name"

class MainFragment: Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        _binding!!.qualityFiveImage?.setOnClickListener { view: View ->
            Toast.makeText(
                requireContext(),
                R.string.yay_feeling,
                Toast.LENGTH_SHORT
            )
                .show()
        }

        _binding!!.qualityFourImage?.setOnClickListener { view: View ->
            Toast.makeText(
                requireContext(),
                R.string.yay_feeling,
                Toast.LENGTH_SHORT
            )
                .show()
        }

        _binding!!.qualityThreeImage?.setOnClickListener { view: View ->
            Toast.makeText(
                requireContext(),
                R.string.better_feeling,
                Toast.LENGTH_SHORT
            )
                .show()
        }

        _binding!!.qualityTwoImage?.setOnClickListener { view: View ->
            Toast.makeText(
                requireContext(),
                R.string.better_feeling,
                Toast.LENGTH_SHORT
            )
                .show()
        }

        _binding!!.qualityOneImage?.setOnClickListener { view: View ->
            Toast.makeText(
                requireContext(),
                R.string.sad_feeling,
                Toast.LENGTH_SHORT
            )
                .show()
        }

        _binding!!.qualityZeroImage?.setOnClickListener { view: View ->
            Toast.makeText(
                requireContext(),
                R.string.sad_feeling,
                Toast.LENGTH_SHORT
            )
                .show()
        }

        _binding!!.monthlyJourney.setOnClickListener { view: View ->
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, MemoryListFragment())
                .commit()
        }

        _binding!!.doneButton.setOnClickListener {
            addName()
            saveUsername()
        }
        _binding!!.nameText.setOnClickListener {
            updateName()
        }

        updateViews()

        return view
    }

    private fun addName() {
        _binding!!.nameText.text = ("Hi " + _binding!!.nameEdit.text)
        _binding!!.nameEdit.visibility = View.GONE
        _binding!!.doneButton.visibility = View.GONE
        _binding!!.nameText.visibility = View.VISIBLE
        }

    // Change the name if users put wrong
    private fun updateName() {
    _binding!!.nameEdit.visibility = View.VISIBLE
    _binding!!.doneButton.visibility = View.VISIBLE
    _binding!!.nameText.visibility = View.GONE
    }

    // save username and show up the next time using sharedPreference
    private fun saveUsername() {
        val sharedPreferences = requireActivity().getPreferences(MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            val name = _binding!!.nameText.text.toString()
            putString(KEY_NAME, name)
            apply()
        }
    }

    private fun getUsername(): String? {
        val sharedPreferences = requireActivity().getPreferences(MODE_PRIVATE)
        return sharedPreferences.getString(KEY_NAME, "")
    }

    private fun updateViews() {
        _binding!!.nameText.text = getUsername()

        if (getUsername() != "") {
            _binding!!.nameText.visibility = View.VISIBLE
            _binding!!.nameEdit.visibility = View.GONE
            _binding!!.doneButton.visibility = View.GONE
        }
    }
}

