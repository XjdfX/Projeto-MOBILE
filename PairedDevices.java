package comunicacao.bluetooth.projeto;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Set;


public class PairedDevices extends ListActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView lv = getListView();
        LayoutInflater inflater = getLayoutInflater();
        View header = inflater.inflate(R.layout.text_header, lv, false);
        lv.addHeaderView(header, null, false);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        setListAdapter(adapter);
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                adapter.add(device.getName() + "\n" + device.getAddress());
            }
        }

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        String item = (String) getListAdapter().getItem(position-1);
        String devName = item.substring(0, item.indexOf("\n"));
        String devAddress = item.substring(item.indexOf("\n")+1, item.length());

        Intent returnIntent = new Intent();
        returnIntent.putExtra("btDevName", devName);
        returnIntent.putExtra("btDevAddress", devAddress);
        setResult(RESULT_OK, returnIntent);
        finish();

    }
}
