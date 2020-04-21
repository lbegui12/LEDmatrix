package com.example.ledmatrix

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import kotlinx.android.synthetic.main.activity_main.view.*

const val EXTRA_MESSAGE = "com.exemple.ledmatrix.MESSAGE"
const val REQUEST_ENABLE_BT = 1


class MainActivity : AppCompatActivity() {

    // On create on fait pas grand chose
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    fun navToBluetoothSettings(view: View){
        val intent = Intent(this, BluetoothSettingsActivity::class.java).apply {
        }
        startActivity(intent)
    }






}
