package com.example.utils;

import com.example.jiemian.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {

	MainActivity mainActivity = new MainActivity();

	public void onReceive(Context context, Intent intent) {
		if (intent.getAction()
				.equals("android.provider.Telephony.SMS_RECEIVED")) {
			// ..�������´���
			this.abortBroadcast();
			StringBuffer sb = new StringBuffer();
			String telephone;
			String message;
			Bundle bundle = intent.getExtras();
			if (bundle != null) {
				// ͨ��pdus���Ի�ý��յ������ж�����Ϣ
				Object[] pdus = (Object[]) bundle.get("pdus");
				// �������Ŷ�������
				SmsMessage[] msgs = new SmsMessage[pdus.length];
				for (int i = 0; i < pdus.length; i++) {
					msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}
				for (SmsMessage msg : msgs) {
					telephone = msg.getDisplayOriginatingAddress();
					message = msg.getMessageBody();
					String string[] = message.split(",");

					if (telephone.equals(Config.telephone)
							|| telephone.equals(Config.numberString[1])
							|| telephone.equals(Config.numberString[1])) {
						if(string[0].equals("4")){
							Config.latitude=Double.parseDouble(string[1]);
							Config.longitude=Double.parseDouble(string[2]);
						}
						switch (Integer.parseInt(message)) {
						// ����
						case 1:
							EnvStatus.isCloseLampChecked = false;
							EnvStatus.isOpenLampChecked = true;
							break;
						// �ص�
						case 2:
							EnvStatus.isCloseLampChecked = true;
							EnvStatus.isOpenLampChecked = false;
							break;
						// ��������
						case 3:
							SmsSender.sendText(context,
									msg.getDisplayOriginatingAddress(), "4,"
											+ "," + Config.latitude + ","
											+ Config.longitude);
							break;
						case 4:

							break;

						default:
							break;
						}
					} else {
						System.out.println("erro number");
					}

				}
				EnvStatus.isOpenLamp = false;
				EnvStatus.isCloseLamp = false;

				Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG)
						.show();
			}

		}
	}

}
