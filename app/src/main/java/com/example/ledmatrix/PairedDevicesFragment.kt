package com.example.ledmatrix

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders

/**
 * A simple [Fragment] subclass.
 */
class PairedDevicesFragment : Fragment() {

    private lateinit var viewModel: EnableBtViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Create the view
        val inflatedView : View = inflater.inflate(R.layout.fragment_paired_devices, container, false)

        // Load the associated viewModel
        Log.i("PairedDevicesFragment", "Called ViewModelProviders.of")
        viewModel = activity?.run {
            ViewModelProviders.of(this).get(EnableBtViewModel::class.java)
        } ?: throw Exception("Invalid Activity")



        var mListView : ListView = inflatedView.findViewById(R.id.paired_devices_list)      // get the ListView

        var devices = viewModel?.getBtDevices()
        activity?.run{
            val adapter = BtDeviceAdapter(this , ArrayList(devices))
            mListView.adapter = adapter
        } ?: throw Exception("Invalid Activity")


        // on item click listener
        mListView.setOnItemClickListener{parent, view, position, id->
            Log.i("PairedDevicesFragment","In setOnItemClickListener")
            var device = parent.getItemAtPosition(position) as BluetoothDevice
            viewModel.selectedItem("${device.name} ${device.address}")
            viewModel.deviceSelected(device)
        }


        val ItemObserver = Observer<MutableMap<BluetoothDevice, Boolean>> {
            Log.i("PairedDeviceFragment", "Item observer")
            var devices = viewModel?.getBtDevices()


            activity?.run{
                val adapter = BtDeviceAdapter(this , ArrayList(devices))
                mListView.adapter = adapter
            } ?: throw Exception("Invalid Activity")
        }
        viewModel.btDevices.observe(this, ItemObserver)


        // Inflate the layout for this fragment
        return inflatedView
    }





}
