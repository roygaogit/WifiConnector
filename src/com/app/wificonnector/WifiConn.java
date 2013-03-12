package com.app.wificonnector;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ChannelListener;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.util.Log;

public class WifiConn extends BroadcastReceiver implements ChannelListener, PeerListListener  {
	
	final private String TAG = "WifiConn";

	final private MainActivity activity;
	
    private WifiP2pManager manager;
    private Channel channel;
    
    ProgressDialog progressDialog = null;
	
	public WifiConn (WifiP2pManager manager, Channel channel, MainActivity activity) {
		this.activity = activity;
		this.manager = manager;
		this.channel = channel;
	}
	
	@Override
	public void onChannelDisconnected() {
		Log.i(TAG,"onChannelDisconnected()");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPeersAvailable(WifiP2pDeviceList arg0) {
		Log.i(TAG,"onPeersAvailable(WifiP2pDeviceList arg0)");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG,"onReceive(Context context, Intent intent) ");
		
		
		String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {

            // UI update to indicate wifi p2p status.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                activity.setIsWifiP2pEnabled(true);
            } else {
                activity.setIsWifiP2pEnabled(false);

            }
            Log.d(TAG, "P2P state changed - " + state);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (manager != null) {
                manager.requestPeers(channel, (PeerListListener) this);
            }
            Log.d(TAG, "P2P peers changed");
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // we are connected with the other device, request connection
                // info to find group owner IP


                manager.requestConnectionInfo(channel, (ConnectionInfoListener) this);
            } else {
                // It's a disconnect
            	Log.d(TAG,"It is a connection");
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
        		Log.d(TAG,"Device changed action");

        }
		
	}
	
    /**
     * 
     */
    public void onInitiateDiscovery() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        progressDialog = ProgressDialog.show(activity, "Press back to cancel", "finding peers", true,
                true, new DialogInterface.OnCancelListener() {

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        
                    }
                });
    }

}
