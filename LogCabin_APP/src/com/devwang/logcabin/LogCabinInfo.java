package com.devwang.logcabin;

public class LogCabinInfo {
	//���¶�����Ǽ�ͥ����
	String ROOM_IN_REALTIME_TEMP_HUMD = "";//������ʪ��״̬
	String CURRENT_WINDOW_STATE = "";//��ǰ������״̬
	String CURRENT_RGB_LED_COLOR_STATE = "";//��ǰ����ɫ��״̬
	
	String STATE_VAPOUR="";//��� ��������
	String STATE_IR="";//��� ���⴫����
	String STATE_SOUND="";//��� ����������
	
	String ACTION_HEAT="";//����״̬
	String ACTION_COLD="";//����״̬
	
	String IDENTIFY_TEXT="";//����ʶ��
	String IDENTITY_VIOCE="";//����ʶ��
	
	String HOME_MODE="";//�龰ģʽ
	
	
	

	public LogCabinInfo(String rOOM_IN_REALTIME_TEMP_HUMD,
			String cURRENT_WINDOW_STATE, String cURRENT_RGB_LED_COLOR_STATE,
			String sTATE_VAPOUR, String sTATE_IR, String sTATE_SOUND,
			String aCTION_HEAT, String aCTION_COLD, String iDENTIFY_TEXT,
			String iDENTITY_VIOCE, String hOME_MODE) {
		super();
		ROOM_IN_REALTIME_TEMP_HUMD = rOOM_IN_REALTIME_TEMP_HUMD;
		CURRENT_WINDOW_STATE = cURRENT_WINDOW_STATE;
		CURRENT_RGB_LED_COLOR_STATE = cURRENT_RGB_LED_COLOR_STATE;
		STATE_VAPOUR = sTATE_VAPOUR;
		STATE_IR = sTATE_IR;
		STATE_SOUND = sTATE_SOUND;
		ACTION_HEAT = aCTION_HEAT;
		ACTION_COLD = aCTION_COLD;
		IDENTIFY_TEXT = iDENTIFY_TEXT;
		IDENTITY_VIOCE = iDENTITY_VIOCE;
		HOME_MODE = hOME_MODE;
	}


	public String getACTION_HEAT() {
		return ACTION_HEAT;
	}

	public void setACTION_HEAT(String aCTION_HEAT) {
		ACTION_HEAT = aCTION_HEAT;
	}

	public String getACTION_COLD() {
		return ACTION_COLD;
	}

	public void setACTION_COLD(String aCTION_COLD) {
		ACTION_COLD = aCTION_COLD;
	}

	public String getIDENTIFY_TEXT() {
		return IDENTIFY_TEXT;
	}

	public void setIDENTIFY_TEXT(String iDENTIFY_TEXT) {
		IDENTIFY_TEXT = iDENTIFY_TEXT;
	}

	public String getIDENTITY_VIOCE() {
		return IDENTITY_VIOCE;
	}

	public void setIDENTITY_VIOCE(String iDENTITY_VIOCE) {
		IDENTITY_VIOCE = iDENTITY_VIOCE;
	}

	public String getHOME_MODE() {
		return HOME_MODE;
	}

	public void setHOME_MODE(String hOME_MODE) {
		HOME_MODE = hOME_MODE;
	}

	public String getSTATE_SOUND() {
		return STATE_SOUND;
	}

	public void setSTATE_SOUND(String sTATE_SOUND) {
		STATE_SOUND = sTATE_SOUND;
	}

	public String getROOM_IN_REALTIME_TEMP_HUMD() {
		return ROOM_IN_REALTIME_TEMP_HUMD;
	}

	public void setROOM_IN_REALTIME_TEMP_HUMD(String rOOM_IN_REALTIME_TEMP_HUMD) {
		ROOM_IN_REALTIME_TEMP_HUMD = rOOM_IN_REALTIME_TEMP_HUMD;
	}

	public String getCURRENT_WINDOW_STATE() {
		return CURRENT_WINDOW_STATE;
	}

	public String getSTATE_VAPOUR() {
		return STATE_VAPOUR;
	}

	public void setSTATE_VAPOUR(String sTATE_VAPOUR) {
		STATE_VAPOUR = sTATE_VAPOUR;
	}

	public String getSTATE_IR() {
		return STATE_IR;
	}

	public void setSTATE_IR(String sTATE_IR) {
		STATE_IR = sTATE_IR;
	}

	public void setCURRENT_WINDOW_STATE(String cURRENT_WINDOW_STATE) {
		CURRENT_WINDOW_STATE = cURRENT_WINDOW_STATE;
	}

	public String getCURRENT_RGB_LED_COLOR_STATE() {
		return CURRENT_RGB_LED_COLOR_STATE;
	}

	public void setCURRENT_RGB_LED_COLOR_STATE(
			String cURRENT_RGB_LED_COLOR_STATE) {
		CURRENT_RGB_LED_COLOR_STATE = cURRENT_RGB_LED_COLOR_STATE;
	}

}
