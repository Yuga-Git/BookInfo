package com.intershala.bookshop.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.intershala.bookshop.*
import com.intershala.bookshop.R.id.frame_layout
import com.intershala.bookshop.fragment.AboutAppFragment
import com.intershala.bookshop.fragment.DashboardFragment
import com.intershala.bookshop.fragment.FavouriteFragment
import com.intershala.bookshop.fragment.ProfileFragment

class MainActivity : AppCompatActivity() {

    lateinit var drawerlayout : DrawerLayout
    lateinit var coordinatorlayout : CoordinatorLayout
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var toolbar: androidx.appcompat.widget.Toolbar

    var previousMenuItem : MenuItem?= null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        drawerlayout = findViewById(R.id.drawer_layout)
        coordinatorlayout = findViewById(R.id.coordinator_layout)
        frameLayout = findViewById(R.id.frame_layout)
        navigationView = findViewById(R.id.navigation_view)
        toolbar = findViewById(R.id.toolbar)

        setUpToolbar()

        openDashboard()

        val actionBarDrawerToggle = ActionBarDrawerToggle(this@MainActivity, drawerlayout,
            R.string.open_drawer,
            R.string.close_drawer
        )

        drawerlayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {

            if (previousMenuItem != null){
                previousMenuItem?.isChecked = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it


            when (it.itemId){
                R.id.dashboard ->{
                    openDashboard()
                    drawerlayout.closeDrawers()
                }
                R.id.favourites ->{
                    supportFragmentManager.beginTransaction()
                        .replace(frame_layout, FavouriteFragment())
                        .commit()
                    supportActionBar?.title = "Favourites"
                    drawerlayout.closeDrawers()
                }
                R.id.profile ->{
                    supportFragmentManager.beginTransaction()
                        .replace(frame_layout, ProfileFragment())
                        .commit()
                    supportActionBar?.title = "Profile"
                    drawerlayout.closeDrawers()
                }
                R.id.aboutApp ->{
                    supportFragmentManager.beginTransaction()
                        .replace(frame_layout, AboutAppFragment())
                        .commit()
                    supportActionBar?.title = "About App"
                    drawerlayout.closeDrawers()
                }

            }

            return@setNavigationItemSelectedListener true
        }

    }

    fun setUpToolbar(){
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if(id == android.R.id.home){
            drawerlayout.openDrawer(GravityCompat.START)
        }

        return super.onOptionsItemSelected(item)
    }

    fun openDashboard(){
        val fragment = DashboardFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(frame_layout, fragment).addToBackStack("Dashboard")
            .commit()
        supportActionBar?.title = "Dashboard"
        navigationView.setCheckedItem(R.id.dashboard)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(frame_layout)

        when(frag){
            !is DashboardFragment -> openDashboard()
            else -> super.onBackPressed()
        }
    }
}