package com.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

public class SocketServiceForListen extends Service {

	private Thread socketThreadForListen;

	private boolean flag=false;
	private Socket socket=null;
	private Runnable socketForListenRunnable=new Runnable()
	{

		@Override
		public void run() {
			int i=0;
			try {
				socket = new Socket(Config.ipAddress, Integer.parseInt(Config.portNumber));
				InputStream inputStream=socket.getInputStream();
				while(flag)
				{
					EnvStatus.smoke="������";
					EnvStatus.fire="�޻���";
					EnvStatus.blaze="��ǿ��";
					i++;
					byte []buffer=new byte[1];
	                int num=0;
	                String status="";
							//��InputStream���ж�ȡ�ͻ��������͵�����
	                for(int j=0;j<6;j++)
	                {
	                   
	                	num=inputStream.read(buffer);
	                	String str=new String(buffer,"utf-8");
	                	status+=str;
	                }
					
					//String str1[]=status.split("8");
					if(status.substring(0, 4).equals("8118"))
					{
						EnvStatus.smoke="������";
					}
					else if(status.substring(0, 4).equals("8218"))
					{
						EnvStatus.fire="�л���";
					}
					else if(status.substring(0, 4).equals("8318"))
					{
						EnvStatus.blaze="��ǿ��";
					}
					else if(status.substring(0, 4).equals("8219"))
					{
						EnvStatus.isOpenLampChecked=true;
						EnvStatus.isCloseLampChecked=false;
					}
					else if(status.substring(0, 4).equals("8209"))
					{
						EnvStatus.isCloseLampChecked=true;
						EnvStatus.isOpenLampChecked=false;
					}
					EnvStatus.isCloseLamp=true;
					EnvStatus.isOpenLamp=true;
					Thread.sleep(1000);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	};
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		socketThreadForListen=null;
		if(socketThreadForListen==null)
		{
			socketThreadForListen=new Thread(null,socketForListenRunnable,"socketThreadForListen");
			
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			socketThreadForListen.interrupt();
			socketThreadForListen=null;
			flag=false;
			if(socket!=null)
			{
				socket.close();
			}
			Toast.makeText(this, "ֹͣ����", Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(!flag)
		{
			socketThreadForListen.start();
			flag=true;
			Toast.makeText(this, "��ʼ����", Toast.LENGTH_SHORT).show();
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
