package uk.co.metro.carousel.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class UrlLoader {
	private Throwable lastException = null;
    private int timeoutConnection = 3000;
    private int timeoutSocket = 15000;
	
	public Throwable getLastException() {
		return lastException;
	}
	
	public void setTimeouts(int millis) {
		timeoutConnection = millis;
		timeoutSocket = millis;
	}

	public JSONObject getJSONFromUrl(String url) throws JSONException {
		JSONObject jObj = null;
		String json = getContentFromUrl(url);
		jObj = new JSONObject(json);
		return jObj;
	}
	
    private String getContentFromUrl(String url) {//throws MetroNetworkException {
    	lastException = null;

        InputStream is = null;
        BufferedReader reader = null;
        InputStreamReader in = null;
        String content = null;

        // Making HTTP request
        try {
        	Log.i("Feed", "url=" + url);
            HttpGet httpGet = new HttpGet(url);                       
            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is established.
            // The default value is zero, that means the timeout is not used. 
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
            // Set the default socket timeout (SO_TIMEOUT) 
            // in milliseconds which is the timeout for waiting for data.
            HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

            DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
            HttpResponse response = httpClient.execute(httpGet);

        	if (needsNetworkSignOn(response, url)) {
        		throw new RuntimeException/*XXX:NetworkSignOnException*/("Please check your internet connection - you may need to sign on");
        	}
        	
            HttpEntity httpEntity = response.getEntity();
            is = httpEntity.getContent();
            in = new InputStreamReader(is, "UTF-8");
            reader = new BufferedReader(in, 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }            
            content = sb.toString();

	    } catch (ConnectTimeoutException sox) {
	    	throw new RuntimeException(url);//XXX:DownloadTimeoutException(url);
	    } catch (SocketTimeoutException sox) {
	    	throw new RuntimeException(url);//XXX:DownloadTimeoutException(url);
        } catch (UnsupportedEncodingException e) {
        	handleError(e);
        } catch (ClientProtocolException e) {
        	handleError(e);
        } catch (IOException e) {
        	handleError(e);
        } finally {

            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                	handleError(ioe);
                }
            }

            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                	handleError(e);
                }
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                	handleError(e);
                }
            }

        }

        return content;
    }
    
    /* http://stackoverflow.com/questions/11962518/android-detect-if-wifi-requires-browser-login */
    private boolean needsNetworkSignOn(HttpResponse response, String url) throws MalformedURLException {
    	URL urlObj = new URL(url);
    	Header location = response.getFirstHeader("Location");
    	if ((location != null) && (location.getValue() != null)) {
    		URL locationUrlObj = new URL(location.getValue()); 
        	if (!urlObj.getHost().equals(locationUrlObj.getHost())) {
        		return true;
        	}
    	}
    	return false;
    }
    
    private void handleError(Throwable ex) {
    	ex.printStackTrace();
    	Log.e("Feed", ex.getMessage(), ex);
    	lastException = ex;
    }
    
    public Bitmap loadBitmapFromUrl(String url) throws SocketTimeoutException, IOException {
		HttpURLConnection conn = null;
		InputStreamReader input = null;
		Bitmap bitmap = null;
		BufferedInputStream buffer = null;

		try {
			HttpGet httpGet = new HttpGet(url);

			HttpParams httpParameters = new BasicHttpParams();
			// Set the timeout in milliseconds until a connection is established.
			// The default value is zero, that means the timeout is not used.
			HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
			// Set the default socket timeout (SO_TIMEOUT)
			// in milliseconds which is the timeout for waiting for data.
			HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);

			DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
			HttpResponse response = httpClient.execute(httpGet);

			InputStream content = response.getEntity().getContent();

			buffer = new BufferedInputStream(content);

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inPreferredConfig = Bitmap.Config.RGB_565;

			bitmap = BitmapFactory.decodeStream(buffer, null, options);
			// for testing
			// writeBitmapToFileSystem("output", bitmap);

			if (bitmap == null) {
				Log.e("Images", "bitmap is null: " + url);
			}
		} catch (ConnectTimeoutException sox) {
			throw new SocketTimeoutException(url);	// XXX: Give better message
		} catch (SocketTimeoutException sox) {
			throw sox;
//		} catch (IOException e) {
//			e.printStackTrace();
//			Log.i("Invalid URL:", url);
//			lastException = e;
		} finally {

			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					handleError(e);
				}
			}

			if (buffer != null) {
				try {
					buffer.close();
				} catch (IOException e) {
					handleError(e);
				}
			}

			if (conn != null) {
				conn.disconnect();
			}
		}
		return bitmap;
	}
}