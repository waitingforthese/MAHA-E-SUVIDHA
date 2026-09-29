package rahul.jagtap.dmas.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Build

class ConnectionDetector(
//							Log.d("Network",
    private val _context: Context
) {
    //									"NETWORKNAME: " + anInfo.getTypeName());
    /*ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		  if (connectivity != null)
		  {
			  NetworkInfo[] info = connectivity.getAllNetworkInfo();
			  if (info != null)
				  for (int i = 0; i < info.length; i++)
					  if (info[i].getState() == NetworkInfo.State.CONNECTED)
					  {
						  return true;
					  }

		  }*/
    val isConnectingToInternet: Boolean
        get() {
            /*ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		  if (connectivity != null)
		  {
			  NetworkInfo[] info = connectivity.getAllNetworkInfo();
			  if (info != null)
				  for (int i = 0; i < info.length; i++)
					  if (info[i].getState() == NetworkInfo.State.CONNECTED)
					  {
						  return true;
					  }

		  }*/
            val connectivityManager =
                _context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val networks = connectivityManager.allNetworks
                var networkInfo: NetworkInfo
                for (mNetwork in networks) {
                    networkInfo = connectivityManager.getNetworkInfo(mNetwork)!!
                    if (networkInfo.state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            } else {
                val info = connectivityManager.allNetworkInfo
                for (anInfo in info) {
                    if (anInfo.state == NetworkInfo.State.CONNECTED) { //							Log.d("Network",
//									"NETWORKNAME: " + anInfo.getTypeName());
                        return true
                    }
                }
            }
            return false
        }

}