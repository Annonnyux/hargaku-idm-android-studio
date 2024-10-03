package com.annmx.botharga;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.Context;
import android.content.Intent;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.net.Uri;
import android.os.*;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.*;
import android.text.*;
import android.text.style.*;
import android.util.*;
import android.view.*;
import android.view.View;
import android.view.View.*;
import android.view.animation.*;
import android.webkit.*;
import android.widget.*;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.budiyev.android.codescanner.*;
import com.bumptech.glide.Glide;
import com.google.zxing.*;
import java.io.*;
import java.io.InputStream;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.*;
import org.json.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
	
	private String temp_scrapped = "";
	private String temp_response_parse = "";
	private String temp_pure_source = "";
	private String parsed_data_processing_2 = "";
	private double increaser = 0;
	private String final_harga = "";
	private String temper = "";
	private String repeatme = "";
	private String url = "";
	private String anchor_variable = "";
	private String generate = "";
	private double temp = 0;
	private HashMap<String, Object> local_db = new HashMap<>();
	
	private ArrayList<HashMap<String, Object>> local_to_db = new ArrayList<>();
	
	private LinearLayout linear1;
	private ImageView imageview1;
	private LinearLayout linear5;
	private LinearLayout linear2;
	private TextView view_harga;
	private TextView view_nama;
	private LinearLayout linear3;
	private TextView view_donate;
	private LinearLayout linear8;
	private LinearLayout linear4;
	private TextView btn_intent_batch;
	private EditText input_plu;
	private LinearLayout linear6;
	private EditText qty_input;
	private LinearLayout linear7;
	private TextView btn_send;
	
	private RequestNetwork apicalls;
	private RequestNetwork.RequestListener _apicalls_request_listener;
	private Intent intentions = new Intent();
	private Vibrator vibrator;
	private RequestNetwork verify;
	private RequestNetwork.RequestListener _verify_request_listener;
	private JSONObject json;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.main);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		linear1 = findViewById(R.id.linear1);
		imageview1 = findViewById(R.id.imageview1);
		linear5 = findViewById(R.id.linear5);
		linear2 = findViewById(R.id.linear2);
		view_harga = findViewById(R.id.view_harga);
		view_nama = findViewById(R.id.view_nama);
		linear3 = findViewById(R.id.linear3);
		view_donate = findViewById(R.id.view_donate);
		linear8 = findViewById(R.id.linear8);
		linear4 = findViewById(R.id.linear4);
		btn_intent_batch = findViewById(R.id.btn_intent_batch);
		input_plu = findViewById(R.id.input_plu);
		linear6 = findViewById(R.id.linear6);
		qty_input = findViewById(R.id.qty_input);
		linear7 = findViewById(R.id.linear7);
		btn_send = findViewById(R.id.btn_send);
		apicalls = new RequestNetwork(this);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		verify = new RequestNetwork(this);
		
		imageview1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				if (!input_plu.getText().toString().equals("")) {
					if (!qty_input.getText().toString().equals("")) {
						if (Double.parseDouble(qty_input.getText().toString()) < 1) {
							generate = qty_input.getText().toString();
						}
						else {
							generate = "B".concat(String.valueOf((long)((Double.parseDouble(input_plu.getText().toString()) * 10000) + Double.parseDouble(qty_input.getText().toString()))));
						}
					}
					else {
						generate = input_plu.getText().toString();
					}
					if (anchor_variable.contains("barcodeapi")) {
						anchor_variable = "https://assets.klikindomaret.com/products/plu/plu_thumb.jpg".replace("plu", input_plu.getText().toString());
					}
					else {
						anchor_variable = "https://barcodeapi.org/api/qr/".concat(generate);
					}
					Glide.with(getApplicationContext()).load(Uri.parse(anchor_variable)).into(imageview1);
					SketchwareUtil.showMessage(getApplicationContext(), "PLEASE WAIT");
				}
				else {
					SketchwareUtil.showMessage(getApplicationContext(), "Masukan PLU terlebih dahulu");
				}
			}
		});
		
		btn_intent_batch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				SketchwareUtil.showMessage(getApplicationContext(), "Masuk Menu Batch");
				intentions.setClass(getApplicationContext(), BatchActivity.class);
				startActivity(intentions);
			}
		});
		
		input_plu.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View _view) {
				intentions.setClass(getApplicationContext(), ScanActivity.class);
				startActivity(intentions);
				return true;
			}
		});
		
		btn_send.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View _view) {
				
				return true;
			}
		});
		
		btn_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				SketchwareUtil.showMessage(getApplicationContext(), "Mulai Mencari");
				apicalls.startRequestNetwork(RequestNetworkController.GET, "https://www.klikindomaret.com/search/?key=".concat(input_plu.getText().toString()), "Single", _apicalls_request_listener);
			}
		});
		
		_apicalls_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				anchor_variable = "https://assets.klikindomaret.com/products/plu/plu_thumb.jpg".replace("plu", input_plu.getText().toString());
				if (_response.contains("Hmm, apa produk yang") || _response.contains("Produk gak ditemukan")) {
					view_nama.setText("Barang TAG gambar akan tetap muncul dalam resolusi rendah");
					view_harga.setText("BARANG INI TAG N ATAU TIDAK DITEMUKAN");
					Glide.with(getApplicationContext()).load(Uri.parse(anchor_variable)).into(imageview1);
				}
				else {
					temp_pure_source = _response;
					_response_parsing();
					String[] final_places = final_harga.split("\",\"");
					view_nama.setText(Arrays.asList(final_places).get(0).toString().replace("\n", ""));
					view_harga.setText(Arrays.asList(final_places).get(1).toString());
					Glide.with(getApplicationContext()).load(Uri.parse(anchor_variable)).into(imageview1);
				}
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				
			}
		};
		
		_verify_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				
			}
		};
	}
	
	private void initializeLogic() {
		btn_intent_batch.setVisibility(View.GONE);
		if (getIntent().hasExtra("response")) {
			input_plu.setText(getIntent().getStringExtra("response"));
			btn_send.performClick();
		}
		else {
			SketchwareUtil.showMessage(getApplicationContext(), "MADE WITH ❤ BY UBAEDILLAH");
		}
	}
	
	@Override
	public void onBackPressed() {
		finishAffinity();
	}
	public void _response_parsing() {
		temp_response_parse = temp_pure_source.replace("instalment", "~");
		temp_response_parse = temp_response_parse.replace("product-collection", "~");
		String[] array_harga_proses = temp_response_parse.split("~");
		temp_scrapped = Arrays.asList(array_harga_proses).get(1).toString();
		parsed_data_processing_2 = temp_scrapped.replace("div class=\"title\">", "~");
		parsed_data_processing_2 = parsed_data_processing_2.replace("</div>\n<span class=\"", "~");
		parsed_data_processing_2 = parsed_data_processing_2.replace("price-value\">", "~");
		parsed_data_processing_2 = parsed_data_processing_2.replace("</span>\n</div>", "~");
		String[] array_harga = parsed_data_processing_2.split("~");
		final_harga = "";
		if (array_harga.length == 5) {
			final_harga = final_harga.concat(Arrays.asList(array_harga).get(1).toString());
			final_harga = final_harga.concat("\",\"");
			final_harga = final_harga.concat(Arrays.asList(array_harga).get(3).toString());
		}
		else {
			final_harga = final_harga.concat(Arrays.asList(array_harga).get(2).toString());
			final_harga = final_harga.concat("\",\"");
			final_harga = final_harga.concat(Arrays.asList(array_harga).get(4).toString());
		}
	}
	
	
	public void _returnable() {
		
	}
	
	
	@Deprecated
	public void showMessage(String _s) {
		Toast.makeText(getApplicationContext(), _s, Toast.LENGTH_SHORT).show();
	}
	
	@Deprecated
	public int getLocationX(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[0];
	}
	
	@Deprecated
	public int getLocationY(View _v) {
		int _location[] = new int[2];
		_v.getLocationInWindow(_location);
		return _location[1];
	}
	
	@Deprecated
	public int getRandom(int _min, int _max) {
		Random random = new Random();
		return random.nextInt(_max - _min + 1) + _min;
	}
	
	@Deprecated
	public ArrayList<Double> getCheckedItemPositionsToArray(ListView _list) {
		ArrayList<Double> _result = new ArrayList<Double>();
		SparseBooleanArray _arr = _list.getCheckedItemPositions();
		for (int _iIdx = 0; _iIdx < _arr.size(); _iIdx++) {
			if (_arr.valueAt(_iIdx))
			_result.add((double)_arr.keyAt(_iIdx));
		}
		return _result;
	}
	
	@Deprecated
	public float getDip(int _input) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, _input, getResources().getDisplayMetrics());
	}
	
	@Deprecated
	public int getDisplayWidthPixels() {
		return getResources().getDisplayMetrics().widthPixels;
	}
	
	@Deprecated
	public int getDisplayHeightPixels() {
		return getResources().getDisplayMetrics().heightPixels;
	}
}