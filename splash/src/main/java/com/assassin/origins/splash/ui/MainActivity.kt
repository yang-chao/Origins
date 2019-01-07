package com.assassin.origins.splash.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.assassin.origins.splash.R
import kotlinx.android.synthetic.main.ac_main.*

class MainActivity : AppCompatActivity() {

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                drawer_layout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ac_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_baseline_menu_black)
        setTitle(R.string.navigation_featured)

        view_pager.adapter = object : FragmentPagerAdapter(supportFragmentManager) {

            override fun getCount(): Int {
                return 2
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return if (position == 0) {
                    getString(R.string.navigation_featured)
                } else {
                    "Test"
                }
            }

            override fun getItem(position: Int): Fragment {
                return Fragment()
            }
        }
        tab_layout.setupWithViewPager(view_pager)

        navigation.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.featured -> setTitle(R.string.navigation_featured)
                else -> title = "OOO"
            }
            drawer_layout.closeDrawers()
            return@setNavigationItemSelectedListener true
        }
    }
}