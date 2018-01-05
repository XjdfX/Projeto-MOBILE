package comunicacao.bluetooth.projeto;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    public static int ENABLE_BLUETOOTH = 1;
    public static int SELECT_PAIRED_DEVICE = 2;
    public static int SELECT_DISCOVERED_DEVICE = 3;


    static TextView textSpace;

    ConnectionThread connect;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textSpace = (TextView) findViewById(R.id.textSpace);

        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter == null) {
            Toast.makeText(getApplicationContext(), "Que pena! Hardware Bluetooth não está funcionando", Toast.LENGTH_LONG).show();
        } else {
            if(!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, ENABLE_BLUETOOTH);
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth ativado!", Toast.LENGTH_LONG).show();
                connect = new ConnectionThread();
                connect.start();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == ENABLE_BLUETOOTH) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth ativado!", Toast.LENGTH_LONG).show();
                connect = new ConnectionThread();
                connect.start();
            }
            else {
                Toast.makeText(getApplicationContext(), "Bluetooth não ativado!", Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode == SELECT_PAIRED_DEVICE || requestCode == SELECT_DISCOVERED_DEVICE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Você selecionou " + data.getStringExtra("btDevName")
                        + "\n" + data.getStringExtra("btDevAddress"), Toast.LENGTH_LONG).show();

                connect = null;
                connect = new ConnectionThread(data.getStringExtra("btDevAddress"));
                connect.start();

            }
            else {
                Toast.makeText(getApplicationContext(), "Nenhum dispositivo selecionado!", Toast.LENGTH_LONG).show();
            }
        }

    }

    public void sendMessage(View view) {
        EditText messageBox = (EditText) findViewById(R.id.editText_MessageBox);
        String messageBoxString = messageBox.getText().toString();
        messageBox.setText("");
        byte[] data =  messageBoxString.getBytes();
        connect.write(data);
    }

    public void searchPairedDevices(View view) {

        Intent searchPairedDevicesIntent = new Intent(this, PairedDevices.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_PAIRED_DEVICE);
    }

    public void discoverDevices(View view) {

        Intent searchPairedDevicesIntent = new Intent(this, DiscoveredDevices.class);
        startActivityForResult(searchPairedDevicesIntent, SELECT_DISCOVERED_DEVICE);
    }

    public void enableVisibility(View view) {

        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 60);
        startActivity(discoverableIntent);
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            byte[] data = bundle.getByteArray("data");
            String dataString= new String(data);

            if(dataString.equals("---N"))
                textSpace.setText("Ocorreu um erro durante a conexão!");
            else if(dataString.equals("---S"))
                textSpace.setText("Conectado!");
            else {
                textSpace.setText(new String(data));
            }

        }
    };


}
