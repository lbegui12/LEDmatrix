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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {
                val intent = Intent(this, DisplayMessageActivity::class.java).apply {
                    putExtra(EXTRA_MESSAGE, "" +
                            "You activated Bluetooth Thx ;) ")
                }
                startActivity(intent)
            } else if(resultCode == Activity.RESULT_CANCELED){
                val intent = Intent(this, DisplayMessageActivity::class.java).apply {
                    putExtra(EXTRA_MESSAGE, "You ass ")
                }
                startActivity(intent)
            } else {

            }
        }
    }


    // récupère la string et l'envoi dans display activity
    fun sendMessage(view: View){
        val editText = findViewById<EditText>(R.id.editText)
        val message = editText.text.toString()
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }



    fun checkBluetoothConnectivity(view: View){
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        var message: String
        if(bluetoothAdapter != null){       // BLUETOOTH IS SUPPORTED
            message = "Bluetooth is supported"
            if (bluetoothAdapter.isEnabled == false) {
                message = "Bluetooth is supported but disabled"
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }
        else {                            // BLUETOOTH IS NOT SUPPORTED
            message = "Bluetooth is NOT supported"
        }
        val intent = Intent(this, DisplayMessageActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        //startActivity(intent)
    }


    fun showPairedDevices(view: View) {
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
        }
        
    }
}
