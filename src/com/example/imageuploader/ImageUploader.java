package com.example.imageuploader;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public class ImageUploader {
	private String realURI;
	private String imageUrl;
	private Context context;

	public ImageUploader(String realURI, Context context) {
		this.realURI = realURI;
		this.context = context;
	}

	public void startUpload() {
		new CreateImage().execute(realURI);
	}

	private class CreateImage extends AsyncTask<String, Void, String> {

		// Setting needed variables (all private)
		private ProgressDialog dialog;
		private String succesError;
		
		// Edit the PHP file in the assets folder and put the link here:
		private String url = "";
		
		// Put the imagepath in front of the filename here, so you can put the link in the database
		// Remember, if you do this, the link must end with:  /
		private String imagePath = "";

		@Override
		protected void onPreExecute() {

			// Setting the Dialog
			super.onPreExecute();
			dialog = new ProgressDialog(context);
			dialog.setCancelable(false);
			dialog.setMessage("Afbeelding uploaden...");
			dialog.show();
		}

		/**
		 * This is the background method and is executed in another thread. Here
		 * an connection is made to the server and the image is uploading.
		 * 
		 * @param path
		 *            Getting String for doInBackround
		 * 
		 * @return null Returns empty String
		 */

		@Override
		protected String doInBackground(String... path) {

			String outPut = null;

			for (String sdPath : path) {

				// Setting the option for the bitmapFactory
				BitmapFactory.Options bfOptions = new BitmapFactory.Options();

				bfOptions.inDither = false; // Disable Dithering mode
				bfOptions.inPurgeable = true; // Tell to gc that whether it
												// needs
												// free memory, the Bitmap can
												// be
												// cleared
				bfOptions.inInputShareable = true; // Which kind of reference
													// will
													// be used to recover the
													// Bitmap
													// data after being clear,
													// when
													// it will be used in the
													// future
				bfOptions.inTempStorage = new byte[32 * 1024];

				// Decocde bitmapfile from sdPath and with the above options
				Bitmap bitmapOrg = BitmapFactory.decodeFile(sdPath, bfOptions);
				ByteArrayOutputStream bao = new ByteArrayOutputStream();

				// Resize the image
				double width = bitmapOrg.getWidth();
				double height = bitmapOrg.getHeight();
				double ratio = 400 / width;
				int newheight = (int) (ratio * height);

				System.out.println("Ã‘Ã‘Ã‘-width" + width);
				System.out.println("Ã‘Ã‘Ã‘-height" + height);

				bitmapOrg = Bitmap.createScaledBitmap(bitmapOrg, 400,
						newheight, true);

				// Setting compresstype. PNG is also permitted, but is not
				// preffered
				bitmapOrg.compress(Bitmap.CompressFormat.JPEG, 95, bao);

				// Creating array for sending bytes
				byte[] ba = bao.toByteArray();
				String ba1 = Base64.encodeToString(ba, 0);

				// Ready for uploading
				System.out.println("uploading image now Ã‘Ã‘Ã�" + ba1);

				// Setting array for all imagenames
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("image", ba1));

				// Executing sending of the image...
				try {
					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(url);
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					HttpResponse response = httpclient.execute(httppost);
					HttpEntity entity = response.getEntity();

					// print response
					outPut = EntityUtils.toString(entity);
					imageUrl = imagePath
							+ outPut;
					Log.i("GET RESPONSEÃ‘-", outPut);

					// Logging information if everything succeeds
					Log.e("log_tag ******", "good connection");

					bitmapOrg.recycle();

					// Exception occours, the message is set
				} catch (Exception e) {
					Log.e("log_tag ******",
							"Error in http connection " + e.toString());
					succesError = "Something went wrong";
				}

				// If no exception occours, the message is set
				succesError = "Uploading succes!";
			}
			return null;
		}
		
		
		/**
		 * Setting the Alertdialog message
		 * 
		 * @param t
		 *            Setting none, needs to be there for implementation...
		 * 
		 * @return void
		 */
		@Override
		protected void onPostExecute(String t) {
			dialog.dismiss();

			new AlertDialog.Builder(context)
					.setTitle("Uploadbericht")
					.setMessage(succesError)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// You can finish the activity with
									//((Activity) context).finish();
									// It will now just close the dialog
								}
							}).setIcon(android.R.drawable.ic_dialog_info)
					.show();
		}	
	}
	
	
	/**
	 * Method for getting the imageUrl that was created
	 * 
	 * @return imageUrl String for image
	 */

	public String returnImageUrl() {
		// Returning the url from the image
		return this.imageUrl;
	}
}
