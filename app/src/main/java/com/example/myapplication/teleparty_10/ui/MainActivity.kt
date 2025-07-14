package com.example.myapplication.teleparty_10.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.myapplication.teleparty_10.R
import com.example.myapplication.teleparty_10.databinding.LayoutActivityMainBinding
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: LayoutActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.layout_activity_main)

        addFragmentStackListener()

        val fragment = supportFragmentManager.findFragmentById(R.id.fragmentContainer)
        if (fragment == null) {
            binding.fragmentContainer.visibility = View.GONE
            binding.btnTask1.visibility = View.VISIBLE
            binding.btnTask2.visibility = View.VISIBLE
        } else {
            binding.fragmentContainer.visibility = View.VISIBLE
            binding.btnTask1.visibility = View.GONE
            binding.btnTask2.visibility = View.GONE
        }

        binding.btnTask1.setOnClickListener {
            launchFragment(Task1Fragment())
        }

        binding.btnTask2.setOnClickListener {
            launchFragment(Task2Fragment())
        }
    }

    private fun launchFragment(fragment: Fragment) {
        binding.btnTask1.visibility = View.GONE
        binding.btnTask2.visibility = View.GONE
        binding.fragmentContainer.visibility = View.VISIBLE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun addFragmentStackListener() {
        supportFragmentManager.addOnBackStackChangedListener {
            val isFragmentDisplayed = supportFragmentManager.backStackEntryCount > 0
            binding.btnTask1.visibility = if (isFragmentDisplayed) View.GONE else View.VISIBLE
            binding.btnTask2.visibility = if (isFragmentDisplayed) View.GONE else View.VISIBLE
            binding.fragmentContainer.visibility = if (isFragmentDisplayed) View.VISIBLE else View.GONE
        }
    }
}