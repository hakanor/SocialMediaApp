package com.example.socialmediaapp.screens

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.service.ApiService
import com.example.socialmediaapp.Constants
import com.example.socialmediaapp.R
import com.example.socialmediaapp.RecyclerViewAdapter
import com.example.socialmediaapp.model.Post
import com.example.socialmediaapp.service.CognitoService
import com.example.socialmediaapp.service.CognitoServiceCallback
import com.google.android.material.navigation.NavigationView
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class HomeActivity : AppCompatActivity(), CognitoServiceCallback, NavigationView.OnNavigationItemSelectedListener{

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var postList: List<Post>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        var createPostButton = findViewById<Button>(R.id.newPostButton)

        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        loadingProgressBar.visibility = View.VISIBLE

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RecyclerViewAdapter()

        recyclerView.adapter = adapter
        fetchPosts()

        var cognitoService = CognitoService(this,this)
        var user = cognitoService.userPool.currentUser.userId
        Log.d("HomeActivity","CurrentUser = $user")

        createPostButton.setOnClickListener {
            /*
            val intent = Intent(this, CreateOrderActivity::class.java)
            startActivity(intent)
            TODO:
             */
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // Handle move action (not needed for swipe)
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        val builder = AlertDialog.Builder(this@HomeActivity)
                        builder.setTitle("Confirm Deletion")
                        builder.setMessage("Are you sure you want to delete this item? This action cannot be undone.")

                        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
                            var apiService = ApiService()
                            var deleteUrl = Constants.URL_POSTS +"/"+ postList[viewHolder.adapterPosition].id
                            apiService.sendHttpRequestWithApiKey(deleteUrl, "DELETE", null) { responseBody, error ->
                                if (error != null) {
                                    error.printStackTrace()
                                    Toast.makeText(this@HomeActivity, error.toString(), Toast.LENGTH_SHORT).show()
                                } else {
                                    responseBody?.let {
                                        var deleteId = postList[viewHolder.adapterPosition].id
                                        val updatedList = postList.filterNot { it.id == deleteId }
                                        postList = updatedList
                                        runOnUiThread{
                                            adapter.deleteItem(viewHolder.adapterPosition)
                                            updateRecyclerView(postList)
                                            Toast.makeText(this@HomeActivity, responseBody, Toast.LENGTH_SHORT).show()
                                            loadingProgressBar.visibility = View.INVISIBLE
                                        }
                                    }
                                }
                            }
                        }

                        builder.setNegativeButton(android.R.string.no) { dialog, which ->
                            adapter.notifyItemChanged(viewHolder.adapterPosition)
                            dialog.dismiss()
                        }
                        builder.show()
                    }
                    ItemTouchHelper.RIGHT -> {
                        /*
                        val intent = Intent(this@HomeActivity, HomeActivity::class.java)
                        var order = postList[viewHolder.adapterPosition]
                        intent.putExtra("EXTRA_ORDER", order)
                        adapter.notifyItemChanged(viewHolder.adapterPosition)
                        startActivity(intent)
                        TODO: EditPostActivity
                         */
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addCornerRadius(0,25)
                    .addSwipeRightActionIcon(R.drawable.baseline_edit_24)
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(this@HomeActivity,
                        R.color.lightBlue
                    ))
                    .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(this@HomeActivity,
                        R.color.errorRed
                    ))
                    .create()
                    .decorate()

                super.onChildDraw(
                    c, recyclerView, viewHolder, dX / 7, dY,
                    actionState, isCurrentlyActive
                )
                //super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val editText = findViewById<EditText>(R.id.editTextSearch)
        editText.setOnKeyListener(View.OnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
                val filtered = postList.filter { it.toString().contains(editText.text.toString(), true) }
                updateRecyclerView(filtered)
                return@OnKeyListener true
            }
            false
        })
    }
    private fun navigateToLoginActivity () {
        /* TODO : NavigateNoLoginActivity
        //val intent = Intent(this, LoginActivity::class.java)
        //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        */

    }
    private fun fetchPosts () {
        val apiService = ApiService()
        val url = Constants.URL_POSTS
        val method = "GET"

        apiService.sendHttpRequestWithApiKey(url, method, null) { responseBody, error ->
            if (error != null) {
                error.printStackTrace()
            } else {
                responseBody?.let {
                    postList = apiService.parseJsonToPostList(responseBody)
                    runOnUiThread{
                        updateRecyclerView(postList)
                        loadingProgressBar.visibility = View.INVISIBLE
                    }
                }
            }
        }
    }
    override fun onResume() {
        super.onResume()
        fetchPosts()
    }

    fun updateRecyclerView(data: List<Post>) {
        adapter.setData(data)
        adapter.notifyDataSetChanged()
    }
    override fun onLoginSuccess() {
    }

    override fun onSignOut() {
        navigateToLoginActivity()
    }

    override fun onRegisterSuccess() {
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_account -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                return true
            }
            R.id.nav_settings -> {
                return true
            }

            R.id.nav_logout -> {
                var cognitoService = CognitoService(this,this)
                cognitoService.userSignOut()
                navigateToLoginActivity()
                return true
            }
            // Handle other items as needed
        }
        return false
    }
}