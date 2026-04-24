package com.example.authenticationexample.navigation
import com.example.authenticationexample.R
enum class NavScreen(var icon:Int,
                     var route:String){
    LOGIN(R.drawable.home, "Login"), //image not visible
    HOME(R.drawable.home,"Home"),
    MANAGER_HOME(R.drawable.home,"ManagerHome"),
    MANAGER_EDIT_TICKET(R.drawable.home, "EditTicket"), //image not visible
    ADD(R.drawable.add, "Add"),
    SIGNUP(R.drawable.home, "SignUp"), //image not visible
    EXIT(R.drawable.logout, "Exit")
}