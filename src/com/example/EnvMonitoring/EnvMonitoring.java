package com.example.EnvMonitoring;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiemian.R;
import com.example.utils.Config;
import com.example.utils.EnvStatus;
import com.example.utils.SmsSender;
import com.example.utils.SocketService;
import com.example.utils.SocketServiceForListen;

public class EnvMonitoring extends Activity {

	final static int MENU_01_00=Menu.FIRST;
	final static int MENU_01_01=Menu.FIRST+1;
	final static int MENU_01_02=Menu.FIRST+2;
	private RadioButton radioWIFIForEnv,radioMSGForEnv;
	private RadioGroup radioGroup1;
	private Button btnTemp,btnHum,btnSmoke,btnFire,btnBlaze;
	private TextView tvTemp,tvHum,tvSmoke,tvFire,tvBlaze;
	String mSendMsg,mReceivedMsg;
    final Handler handler=new Handler();
    Runnable runnable;
    private Intent intentSocket;
    private Intent intentSocketForListen;
    public static boolean isOpenSocket=false;
	
	private static final String ACTION = "android.provider.Telephony.SMS_RECEIVED"; 
	
	private BroadcastReceiver myReceiver = new BroadcastReceiver() {  
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			if(arg1.getAction().equals(ACTION))
			{
				Bundle bundle=arg1.getExtras();
				if(bundle!=null)
				{
					Object[] objs=(Object[])bundle.get("pdus");
					SmsMessage[] messages=new SmsMessage[objs.length];
					for(int i=0;i<objs.length;i++){
						messages[i]=SmsMessage.createFromPdu((byte[]) objs[i]);
					}
					String smsBody=messages[0].getDisplayMessageBody();
					String smsSender=messages[0].getDisplayOriginatingAddress();
					Toast.makeText(arg0, "�յ�����Ϣ��"+smsBody+"\n"+"������:"+smsSender, Toast.LENGTH_SHORT).show();
					
					if(smsBody.contains("temp"))
					{
						String str[]=smsBody.split(":");
						tvTemp.setText("�¶ȣ�"+str[1]+"��");
					}
					else if(smsBody.contains("hum"))
					{
						String str[]=smsBody.split(":");
						tvHum.setText("ʪ�ȣ�%"+str[1]);
					}
					else if(smsBody.contains("smoke"))
					{
						String str[]=smsBody.split(":");
						tvSmoke.setText("����"+str[1]);
					}else if(smsBody.contains("fire"))
					{
						String str[]=smsBody.split(":");
						tvSmoke.setText("���棺"+str[1]);
					}else if(smsBody.contains("blaze"))
					{
						String str[]=smsBody.split(":");
						tvSmoke.setText("ǿ�⣺"+str[1]);
					}
				}

			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.envmonitoring);
		
		radioWIFIForEnv=(RadioButton)findViewById(R.id.radioWIFIForEnv);
		radioMSGForEnv=(RadioButton)findViewById(R.id.radioMSGForEnv);
		radioGroup1=(RadioGroup)findViewById(R.id.radioGroup1);
		btnTemp=(Button)findViewById(R.id.btnTemp);
		btnHum=(Button)findViewById(R.id.btnHum);
		btnSmoke=(Button)findViewById(R.id.btnSmoke);
		btnFire=(Button)findViewById(R.id.btnFire);
		btnBlaze=(Button)findViewById(R.id.btnBlaze);
		tvTemp=(TextView)findViewById(R.id.tvTemp);
		tvHum=(TextView)findViewById(R.id.tvHum);
		tvSmoke=(TextView)findViewById(R.id.tvSmoke);
		tvFire=(TextView)findViewById(R.id.tvFire);
		tvBlaze=(TextView)findViewById(R.id.tvBlaze);
		
		radioMSGForEnv.setEnabled(false);
		btnTemp.setEnabled(false);
		btnHum.setEnabled(false);
		btnSmoke.setEnabled(false);
		btnBlaze.setEnabled(false);
		
		EnvStatus.smoke="������";
    	EnvStatus.fire="�޻���";
    	EnvStatus.blaze="��ǿ��";
		
		intentSocketForListen=new Intent(EnvMonitoring.this,SocketServiceForListen.class);
		
		if(radioWIFIForEnv.isChecked())
		{
			EnvStatus.isGetTemp=true;
			EnvStatus.isGetHum=true;
			EnvStatus.isGetSmoke=true;
			EnvStatus.isGetBlaze=true;
			EnvStatus.isGetFire=true;
		}
		
		//ע�����receiver
		IntentFilter filter = new IntentFilter();  
        filter.addAction(ACTION);  
        filter.setPriority(Integer.MAX_VALUE);  
        registerReceiver(myReceiver, filter); 
        
        //ˢ��UI�Ķ�ʱ��
        runnable=new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                //Ҫ��������
                handler.postDelayed(this, Integer.parseInt(Config.refreshRate));
            	tvTemp.setText("�¶ȣ�"+EnvStatus.temp);
            	tvHum.setText("ʪ�ȣ�"+EnvStatus.hum);
            	tvSmoke.setText("����"+EnvStatus.smoke);
            	tvFire.setText("���棺"+EnvStatus.fire);
            	tvBlaze.setText("ǿ�⣺"+EnvStatus.blaze);
            }
        };
        handler.postDelayed(runnable, Integer.parseInt(Config.refreshRate));//..������ִ��һ��runnable.
        
        radioGroup1.setOnCheckedChangeListener(new OnCheckedChangeListener(){

			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if(arg1==R.id.radioWIFIForEnv)
				{
					EnvStatus.isGetTemp=true;
					EnvStatus.isGetHum=true;
					EnvStatus.isGetSmoke=true;
					EnvStatus.isGetBlaze=true;
					EnvStatus.isGetFire=true;
				}
				else if(arg1==R.id.radioMSGForEnv)
				{
					EnvStatus.isGetTemp=false;
					EnvStatus.isGetHum=false;
					EnvStatus.isGetSmoke=false;
					EnvStatus.isGetBlaze=false;
					EnvStatus.isGetFire=false;
				}
			}
        	
        });
        
		btnTemp.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if(radioMSGForEnv.isChecked())//����ģʽ�����¶�
				{
					SmsSender.sendText(getBaseContext(), Config.phoneNumber, "requestfortemp");
				}
				else
				{
					EnvStatus.isTempChecked=true;
				}
			}
		});
		btnHum.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				if(radioMSGForEnv.isChecked())//����ģʽ����ʪ��
				{
					SmsSender.sendText(getBaseContext(), Config.phoneNumber, "requestforhum");
				}
				else
				{
					EnvStatus.isHumChecked=true;
				}
			}
		});
		btnSmoke.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				if(radioMSGForEnv.isChecked())//����ģʽ��������
				{
					SmsSender.sendText(getBaseContext(), Config.phoneNumber, "requestforsmoke");
				}
				else
				{
					EnvStatus.isSmokeChecked=true;
				}
			}
		});
		btnFire.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//EnvStatus.isFireChecked=true;
				if(isOpenSocket)
				{
					stopService(intentSocket);
					startService(intentSocketForListen);
					btnFire.setEnabled(false);
					btnBlaze.setEnabled(true);	
				}
				else
				{
					Toast.makeText(EnvMonitoring.this, "�뿪��Socket���̷߳���	", Toast.LENGTH_SHORT).show();
				}
			}
		});
		btnBlaze.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				//EnvStatus.isBlazeChecked=true;
				startService(intentSocket);
				stopService(intentSocketForListen);
				btnFire.setEnabled(true);
				btnBlaze.setEnabled(false);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		//return super.onCreateOptionsMenu(menu);
		menu.add(0,MENU_01_00,0,"��������");
		menu.add(0,MENU_01_01,1,"ֹͣ����");
	    menu.add(0,MENU_01_02,2,"�˳�");
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		//return super.onOptionsItemSelected(item);
		intentSocket=new Intent(EnvMonitoring.this,SocketService.class);
		switch(item.getItemId())
		{
			case MENU_01_00:
				Log.v("SysSetting","�ѿ�������");
				startService(intentSocket);
				isOpenSocket=true;
				return true;
			case MENU_01_01:
				Log.v("SysSetting","�ѹرշ���");
				stopService(intentSocket);
				isOpenSocket=false;
				return true;
			case MENU_01_02:
				System.exit(0);
			default:
				return false;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		handler.removeCallbacks(runnable);
		stopService(intentSocket);
		stopService(intentSocketForListen);
		
	}
}
