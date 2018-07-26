package com.volmit.lux;

import java.util.List;

import javax.swing.JDialog;

import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHBridgeSearchManager;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHHueParsingError;

public class LUX
{
	private static List<PHAccessPoint> acf = null;
	private static PHHueSDK sdk = PHHueSDK.getInstance();

	public static List<PHAccessPoint> findBridges()
	{
		PHBridgeSearchManager sm = (PHBridgeSearchManager) sdk.getSDKService(PHHueSDK.SEARCH_BRIDGE);
		sm.search(true, true);

		while(acf == null)
		{
			try
			{
				Thread.sleep(50);
			}

			catch(InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		GList<PHAccessPoint> k = new GList<PHAccessPoint>(acf);
	}

	private PHSDKListener listener = new PHSDKListener()
	{
		@Override
		public void onAccessPointsFound(List<PHAccessPoint> accessPointsList)
		{
			for(PHAccessPoint i : accessPointsList)
			{
				sdk.startPushlinkAuthentication(i);

			}
		}

		@Override
		public void onAuthenticationRequired(PHAccessPoint accessPoint)
		{
			// Start the Pushlink Authentication.
			desktopView.getFindingBridgeProgressBar().setVisible(false);

			pushLinkDialog = new PushLinkFrame(instance);
			pushLinkDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			pushLinkDialog.setModal(true);
			pushLinkDialog.setLocationRelativeTo(null); // Center the dialog.
			pushLinkDialog.setVisible(true);

		}

		@Override
		public void onBridgeConnected(PHBridge bridge, String username)
		{
			phHueSDK.setSelectedBridge(bridge);
			phHueSDK.enableHeartbeat(bridge, PHHueSDK.HB_INTERVAL);
			desktopView.getFindingBridgeProgressBar().setVisible(false);
			String lastIpAddress = bridge.getResourceCache().getBridgeConfiguration().getIpAddress();
			HueProperties.storeUsername(username);
			HueProperties.storeLastIPAddress(lastIpAddress);
			HueProperties.saveProperties();
			// Update the GUI.
			desktopView.getLastConnectedIP().setText(lastIpAddress);
			desktopView.getLastUserName().setText(username);
			// Close the PushLink dialog (if it is showing).
			if(pushLinkDialog != null && pushLinkDialog.isShowing())
			{
				pushLinkDialog.setVisible(false);
			}
			// Enable the Buttons/Controls to change the hue bulbs.s
			desktopView.getRandomLightsButton().setEnabled(true);
			desktopView.getSetLightsButton().setEnabled(true);

		}

		@Override
		public void onCacheUpdated(List<Integer> arg0, PHBridge arg1)
		{

		}

		@Override
		public void onConnectionLost(PHAccessPoint arg0)
		{

		}

		@Override
		public void onConnectionResumed(PHBridge arg0)
		{

		}

		@Override
		public void onError(int code, final String message)
		{

		}

		@Override
		public void onParsingErrors(List<PHHueParsingError> parsingErrorsList)
		{

		}
	};
}
