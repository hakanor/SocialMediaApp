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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmediaapp.service.ApiService
import com.example.socialmediaapp.Constants
import com.example.socialmediaapp.R
import com.example.socialmediaapp.RecyclerViewAdapter
import com.example.socialmediaapp.model.Post
import com.example.socialmediaapp.service.AuthService
import com.example.socialmediaapp.service.AuthServiceCallback
import com.example.socialmediaapp.service.SharedPreferencesService
import com.example.socialmediaapp.service.TokenService
import com.example.socialmediaapp.service.TokenServiceCallback
import com.google.android.material.navigation.NavigationView
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class HomeActivity : AppCompatActivity(), AuthServiceCallback, NavigationView.OnNavigationItemSelectedListener, TokenServiceCallback{

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecyclerViewAdapter
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var postList: List<Post>
    private lateinit var tokenService : TokenService
    private lateinit var authService : AuthService
    private lateinit var spService : SharedPreferencesService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val navView = findViewById<NavigationView>(R.id.nav_view)
        navView.setNavigationItemSelectedListener(this)

        val createPostButton = findViewById<Button>(R.id.newPostButton)

        authService = AuthService(this,this)
        tokenService = TokenService(this,this)
        spService = SharedPreferencesService(this)

        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        loadingProgressBar.visibility = View.VISIBLE

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = RecyclerViewAdapter()

        recyclerView.adapter = adapter
        fetchPosts()

        createPostButton.setOnClickListener {
            val intent = Intent(this, CreatePostActivity::class.java)
            startActivity(intent)
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

                        builder.setPositiveButton(android.R.string.yes) { _, _ ->
                            val apiService = ApiService()
                            val deleteUrl = Constants.URL_POSTS +"/"+ postList[viewHolder.adapterPosition].id
                            apiService.sendHttpRequestWithApiKey(deleteUrl, "DELETE", null) { responseBody, responseCode,error ->
                                if (error != null) {
                                    error.printStackTrace()
                                    Toast.makeText(this@HomeActivity, error.toString(), Toast.LENGTH_SHORT).show()
                                } else {
                                    responseBody?.let {
                                        val deleteId = postList[viewHolder.adapterPosition].id
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

                        builder.setNegativeButton(android.R.string.no) { dialog, _ ->
                            adapter.notifyItemChanged(viewHolder.adapterPosition)
                            dialog.dismiss()
                        }
                        builder.show()
                    }
                    ItemTouchHelper.RIGHT -> {
                        val intent = Intent(this@HomeActivity, EditPostActivity::class.java)
                        val post = postList[viewHolder.adapterPosition]
                        intent.putExtra("EXTRA_POST", post)
                        adapter.notifyItemChanged(viewHolder.adapterPosition)
                        startActivity(intent)
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
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    private fun fetchPosts () {
        val spService = SharedPreferencesService(this)
        val accessToken = spService.getCurrentAccessToken()
        val apiService = ApiService(accessToken)
        val url = Constants.URL_POSTS
        val method = "GET"

        apiService.sendHttpRequestWithApiKey(url, method, null) { responseBody, responseCode,error ->
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

            R.id.nav_changePassword -> {
                val intent = Intent(this, ChangePasswordActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.nav_logout -> {
                val token = spService.getCurrentAccessToken()
                if (token != null) {
                    tokenService.validateAccessToken(token)
                }
                return true
            }
        }
        return false
    }

    override fun onLogOut(message: String) {
        runOnUiThread {
            Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
        }
        navigateToLoginActivity()
    }

    override fun onError(error: String) {
        runOnUiThread {
            Toast.makeText(this,error,Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSuccess(message: String) {
        TODO("Not yet implemented")
    }

    override fun onValidAccessToken(message: String) {
        if (message.contains("true")) {
            authService.logOut()
        } else {
            runOnUiThread {
                spService.userRemoveAccessToken()
                val user = spService.getCurrentUser()
                tokenService.getUserSubId(user?:"")
            }
        }
    }

    override fun onRefreshAccessToken(message: String) {
        spService.updateAccessToken(message)
        authService.logOut()
    }

    override fun onGetUser(message: String) {
        val refreshToken = spService.getCurrentRefreshToken()
        if (refreshToken != null ){
            tokenService.refreshAccessToken(message,refreshToken)
        }
    }
}