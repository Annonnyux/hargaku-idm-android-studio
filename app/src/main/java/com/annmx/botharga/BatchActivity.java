package com.annmx.botharga;

import android.animation.*;
import android.app.*;
import android.content.*;
import android.content.res.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.media.*;
import android.net.*;
import android.os.*;
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
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.budiyev.android.codescanner.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.*;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.*;
import org.json.*;

public class BatchActivity extends AppCompatActivity {
	
	private Timer _timer = new Timer();
	
	private String temp_response_parse = "";
	private String temp_pure_source = "";
	private String temp_scrapped = "";
	private String parsed_data_processing_2 = "";
	private String final_harga = "";
	private HashMap<String, Object> batch_harga_map = new HashMap<>();
	private String jsonstring = "";
	private String temp_pure_to_json = "";
	private HashMap<String, Object> map_plu = new HashMap<>();
	private double increaser = 0;
	private double time_delay = 0;
	
	private ArrayList<String> list_plu = new ArrayList<>();
	private ArrayList<String> list_harga = new ArrayList<>();
	
	private LinearLayout canvas_ground;
	private LinearLayout canvas_title;
	private LinearLayout canvas_input;
	private LinearLayout canvas_process;
	private ScrollView canvas_result_scroll;
	private TextView view_title;
	private EditText input_plu;
	private TextView btn_batch_send;
	private TextView view_result;
	
	private RequestNetwork apicall;
	private RequestNetwork.RequestListener _apicall_request_listener;
	private TimerTask timer;
	
	@Override
	protected void onCreate(Bundle _savedInstanceState) {
		super.onCreate(_savedInstanceState);
		setContentView(R.layout.batch);
		initialize(_savedInstanceState);
		initializeLogic();
	}
	
	private void initialize(Bundle _savedInstanceState) {
		canvas_ground = findViewById(R.id.canvas_ground);
		canvas_title = findViewById(R.id.canvas_title);
		canvas_input = findViewById(R.id.canvas_input);
		canvas_process = findViewById(R.id.canvas_process);
		canvas_result_scroll = findViewById(R.id.canvas_result_scroll);
		view_title = findViewById(R.id.view_title);
		input_plu = findViewById(R.id.input_plu);
		btn_batch_send = findViewById(R.id.btn_batch_send);
		view_result = findViewById(R.id.view_result);
		apicall = new RequestNetwork(this);
		
		btn_batch_send.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View _view) {
				view_result.setText("");
				jsonstring = "";
				if (input_plu.getText().toString().contains("\n")) {
					temp_pure_to_json = "{\"".concat(input_plu.getText().toString().replace("\n", "\":\"\",\"").concat("\":\"\"}"));
					SketchwareUtil.showMessage(getApplicationContext(), temp_pure_to_json);
					map_plu = new Gson().fromJson(temp_pure_to_json, new TypeToken<HashMap<String, Object>>(){}.getType());
					SketchwareUtil.getAllKeysFromMap(map_plu, list_plu);
					increaser = 1;
					for(int _repeat13 = 0; _repeat13 < (int)((list_plu.size() - 1)); _repeat13++) {
						apicall.startRequestNetwork(RequestNetworkController.GET, "https://www.klikindomaret.com/search/?key=".concat(list_plu.get((int)(increaser))), "batch", _apicall_request_listener);
						increaser++;
					}
					time_delay = list_plu.size() / 50;
					timer = new TimerTask() {
						@Override
						public void run() {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									jsonstring = "{\"\":\"".concat(jsonstring.concat("\"}")).replace("\n", "");
									batch_harga_map = new Gson().fromJson(jsonstring, new TypeToken<HashMap<String, Object>>(){}.getType());
									SketchwareUtil.getAllKeysFromMap(batch_harga_map, list_harga);
									Collections.sort(list_harga);
									increaser = 0;
									for(int _repeat49 = 0; _repeat49 < (int)(list_harga.size()); _repeat49++) {
										view_result.setText(view_result.getText().toString().concat(list_harga.get((int)(increaser)).concat("\n")));
										increaser++;
									}
								}
							});
						}
					};
					_timer.schedule(timer, (int)(time_delay * 5000));
				}
				else {
					SketchwareUtil.showMessage(getApplicationContext(), "HARGA BATCH MINIMAL 2 ITEM!");
				}
			}
		});
		
		_apicall_request_listener = new RequestNetwork.RequestListener() {
			@Override
			public void onResponse(String _param1, String _param2, HashMap<String, Object> _param3) {
				final String _tag = _param1;
				final String _response = _param2;
				final HashMap<String, Object> _responseHeaders = _param3;
				temp_pure_source = _response;
				_response_parsing();
				String[] final_places = final_harga.split("\",\"");
				jsonstring = jsonstring.concat("\",\n\"".concat(Arrays.asList(final_places).get(0).toString().concat("__".concat(String.valueOf((long)(SketchwareUtil.getRandom((int)(1), (int)(99)))).concat("__")).concat(Arrays.asList(final_places).get(1).toString().concat("\":\"")))));
			}
			
			@Override
			public void onErrorResponse(String _param1, String _param2) {
				final String _tag = _param1;
				final String _message = _param2;
				
			}
		};
	}
	
	private void initializeLogic() {
		((EditText)input_plu).setMaxLines((int)50);
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