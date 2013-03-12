package com.app.wificonnector;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
    public static final String TAG = "MainActivity";
    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();
    private Channel channel;
    private BroadcastReceiver receiver = null;
    
    private TextView mTextView = null;
    
    private Button conn_button= null;
    private Button search_button = null;
    private Button sett_button = null;
    
    private WifiConn mWifiConn = null;
 


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTextView = (TextView) findViewById(R.id.text_id);
		mTextView.append("asdasd");

		
		intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        

        
        //Buttons
        conn_button = (Button) findViewById(R.id.conn_button);
        search_button = (Button) findViewById(R.id.find_button);
        sett_button = (Button) findViewById(R.id.setting_button);
        
        sett_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 if (manager != null && channel != null) {

	                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
	                } else {
	                    Log.e(TAG, "channel or manager is null");
	                }
				
			}
		});
        
        search_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				 if (!isWifiP2pEnabled) {
	                    Toast.makeText(MainActivity.this, "Wifi direct is off",
	                            Toast.LENGTH_SHORT).show();

	                }

	                ((WifiConn)receiver ).onInitiateDiscovery();
	                manager.discoverPeers(channel, new WifiP2pManager.ActionListener() {

	                    @Override
	                    public void onSuccess() {
	                        Toast.makeText(MainActivity.this, "Discovery Initiated",
	                                Toast.LENGTH_SHORT).show();
	                    }

	                    @Override
	                    public void onFailure(int reasonCode) {
	                        Toast.makeText(MainActivity.this, "Discovery Failed : " + reasonCode,
	                                Toast.LENGTH_SHORT).show();
	                    }
	                });
				
			}
		});
        
        
        Log.i(TAG,"onCreate");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
    public void onResume() {
        super.onResume();
        receiver = new WifiConn(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }
	
	
    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

}
