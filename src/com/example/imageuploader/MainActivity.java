package com.example.imageuploader;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {
	
private Button btnChooseImage, btnUploadImage;
private Uri currImageURI;
private ImageView showImage;
private String imageOnServerUrl;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		/**
		 * Buttons for choosing / uploading image and showing image
		 */
		btnChooseImage = (Button) this.findViewById(R.id.chooseImage);
		btnUploadImage = (Button) this.findViewById(R.id.uploadImage);
		showImage = (ImageView) this.findViewById(R.id.showImage);
		
		
		/**
		 * Choose image from gallery
		 */
		btnChooseImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
					// To open up a gallery browser
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(
							Intent.createChooser(intent, "Select Picture"), 1);
				}
		});

		/**
		 * Upload the image
		 */
		btnUploadImage.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// Starting the upload for image
				startUpload();
				
			}
		});
	}

	
	/**
	 * To handle when an image is selected from the gallery
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {

				// currImageURI is the global variable I�m using to hold the
				// content:
				currImageURI = data.getData();
				System.out.println("Current image Path is --->"
						+ getRealPathFromURI(currImageURI));
				setImage(getRealPathFromURI(currImageURI));
			}
		}
	}
	
	
	/**
	 * Convert the image URI to the direct file system path of the image file
	 * This is usefull for showing the image, but also to get the full image path
	 * for showing the image in your app
	 * 
	 * @param contentUri
	 *            Url from where the picture exist on the mobile phone
	 * @return
	 */
	private String getRealPathFromURI(Uri contentUri) {
		String res = null;
		String[] proj = { MediaColumns.DATA };
		Cursor cursor = getContentResolver().query(contentUri, proj, null,
				null, null);
		if (cursor.moveToFirst()) {
			;
			int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
			res = cursor.getString(column_index);
		}
		cursor.close();
		return res;
	}
	
	
	
	/**
	 * Method for getting the image from the HTTPUploader class
	 * 
	 * @param url
	 */
	private void setImage(String url) {
		Bitmap bitmap = BitmapFactory.decodeFile(url);
		showImage.setImageBitmap(bitmap);
	}
	
	/**
	 * Method for starting the upload AsyncTask
	 */
	private void startUpload() {
		
		if(currImageURI != null){
		
		/**
		 * Setting up the imageUploader and setting the url that came from the image in the gallery
		 */
		ImageUploader imgUploader = new ImageUploader(getRealPathFromURI(currImageURI), MainActivity.this);
		
		// Starting the upload
		imgUploader.startUpload();
		

		// Getting imageUrl for viewing on the server (the true server url)
		imageOnServerUrl = imgUploader.returnImageUrl();
		
		}
		
		else{
			
			// Set own dialog here if you want to handle your exceptions
			
		}
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
