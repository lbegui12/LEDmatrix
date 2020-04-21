package com.example.ledmatrix

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

class BluetoothSettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: EnableBtViewModel

    // BT device found broadcast receiver
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.i("BTActivity","->onReceive")
            val action: String = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    Log.i("BtSettingsActivity", "Discovery has found a device")
                    // Discovery has found a device. Get the BluetoothDevice object and its info from the Intent.
                    val device: BluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    viewModel.addDevice(device)
                }
            }
        }
    }

    @TargetApi(22)
    fun scanLeDevice(){
        Log.i("BtSettingActivity","->scanLeDevice")
        val ba= BluetoothAdapter.getDefaultAdapter()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            Log.i("Activity","permission NOT granted")
        }
        else{
            Log.i("Activity","permission granted !")
        }

        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 1)

        val bleScanner = ba.bluetoothLeScanner
        bleScanner.startScan(callBack)
    }

    @TargetApi(21)
    private val callBack: ScanCallback = object : ScanCallback(){
        override fun onScanResult(callbackType:Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            var rssi = result.rssi
            var bd = result.device

            if(bd!=null)
            {
                Log.i("BLEcallBack","Scan result : ${bd.name}")
                viewModel?.addDevice(bd)
            }

        }
    }


    fun startBtDiscovery(){
        Log.i("BtSettingActivity","->startBtDiscovery")
        val ba= BluetoothAdapter.getDefaultAdapter()
        if(ba.isDiscovering){
            ba.cancelDiscovery()
        }
        ba.startDiscovery()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_settings)

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        // instantiate the associated viewModel
        viewModel = ViewModelProviders.of(this).get(EnableBtViewModel::class.java)

        // Create an observer for paired devices
        val ItemObserver = Observer<String> {
            val selected = viewModel?.selected.value
            Log.i("BT_Activity", "Item observer $selected ")
            val displayFragment : Fragment = DisplayMessageFragment(selected)
            val transaction = supportFragmentManager.beginTransaction().apply {
                replace(R.id.fragment_container, displayFragment)
                addToBackStack(null)
            }
            transaction.commit()
        }
        viewModel.selected.observe(this, ItemObserver)

        // decide which fragment to display when activity is created
        if(findViewById<FrameLayout>(R.id.fragment_container) != null){
            if(savedInstanceState != null){
                return
            }

            val newFragment : Fragment
            when(BluetoothAdapter.getDefaultAdapter().isEnabled) {
                true  -> {
                    newFragment = PairedDevicesFragment()
                    //startBtDiscovery()
                    scanLeDevice()
                }
                false -> newFragment = EnableBluetoothFragment()
            }

            newFragment.arguments = intent.extras
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, newFragment).commit()

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == Activity.RESULT_OK) {                 // user enabled bluetooth
                val pairedDevicesFragment = PairedDevicesFragment()
                val transaction = supportFragmentManager.beginTransaction().apply {
                    replace(R.id.fragment_container, pairedDevicesFragment)
                    addToBackStack(null)
                }
                transaction.commit()
            } else if(resultCode == Activity.RESULT_CANCELED){      // user disabled bluetooth
            } else {

            }
        }
    }

    fun checkBluetoothConnectivity(view: View){
        val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter != null){       // BLUETOOTH IS SUPPORTED
            if (!bluetoothAdapter.isEnabled) {  // bluetooth is supported but disabled
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        }
        else {                            // BLUETOOTH IS NOT SUPPORTED
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }


}
