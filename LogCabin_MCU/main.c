#include <msp430g2553.h>

#include "HC05.h"
#include"led.h"
#include"key.h"
#include"wdt.h"
#include"wx315.h"
#include"rgb.h"
#include"DHT11.h"
#include"clock.h"
#include"TimerA.h"
#include"window.h"
#include"tempnew.h"



int i=0;

extern int uart_temp='0',flag_uart_temp='0';
extern unsigned int wx315_m=0;
extern unsigned int cnt_128us=0;//64us*2=128us
extern int t=0,h=0;



unsigned char c='c';
unsigned char ch[9] ="T18'C 70%";//

unsigned int temp_tx_flag=0;

unsigned int flag_315_tx=0,cnt_315_tx=0;

unsigned int flag_bt_en=0;

unsigned int flag_mq2=0;//��������MQ-2
unsigned int flag_ir=0;//���⴫����
unsigned int flag_sound=0;//����������

extern void P13_Onclick(void);
extern void P17_MQ2(void); //��������MQ-2 ����ú��©��ʱ ����͵�ƽ
extern void P25_Ir(void);//���⴫���� ����Ƿ��п�������  �������ڵ� ����ߵ�ƽ
extern void P24_Sound(void);//���������� ����Ƿ����˴��

//�龰ģʽ
void Home_Mode_Backhome(void);//�ؼ�ģʽ�����ơ�����
void Home_Mode_Romatic(void);//����ģʽ��ѭ���ʵ��Զ�
void Home_Mode_Awayhome(void);//���ģʽ���صơ��ش�

/*
 * main.c
 */
int main(void) {
    //WDTCTL = WDTPW | WDTHOLD;	// Stop watchdog timer
	Clock_Init();
    WDT_Init();
    WX315_Init();
	led_init();
	Init_HC05();
	TimerA_Init();
	Key_Init();
	MQ2_Init();
	Ir_Init();
	Sound_Init();

//	//__bis_SR_register(LPM0_bits + GIE);
	_EINT();

	led0_off();//Ĭ�Ͽ�����ʹ��
	delay_ms(1000);

	while(1){
		if(temp_tx_flag==1){
			for(i=0;i<8;i++){
				UartPutchar(ch[i]);
			}
			temp_tx_flag=0;
		}
		if(flag_mq2==1){
			flag_mq2=0;
			UartPutchar('V');//Vapour �������� ��©�� ��APP����ָ��'V'
		}
		if(flag_ir==1){
			flag_ir=0;
			UartPutchar('I');//Ir ���⴫���� ��©�� ��APP����ָ��'I'
		}
		if(flag_sound==1){
			flag_sound=0;
			UartPutchar('S');//Sound ���������� �д�� ��APP����ָ��'S'
		}
		switch(uart_temp){

			 		//color
			 		case 'r':
			 			RGB_r();
			 			uart_temp=' ';
			 			break;
			 		case 'o':
			 			RGB_o();
			 			uart_temp=' ';
			 			break;
			 		case 'y':
			 			RGB_y();
			 			uart_temp=' ';
			 			break;
			 		case 'g':
			 			RGB_g();
			 			uart_temp=' ';
			 			break;
			 		case 'b':
			 			RGB_b();
			 			uart_temp=' ';
			 			break;
			 		case 'p':
			 			RGB_p();
			 			uart_temp=' ';
			 			break;
			 		case 'w':
			 			RGB_w();
			 			uart_temp=' ';
			 			break;
			 		case 'd'://�ص� dieout
			 			RGB_d();
			 			uart_temp=' ';
			 			break;
			 			//***********************************************************
			 		case 'm'://���� m motor
			 			Window_m();
			 			uart_temp=' ';
			 			break;
			 		case 'n'://�ش�
			 			Window_n();
			 			uart_temp=' ';
			 			break;
			 			//***********************************************************
			 		case 'u'://���� u up
			 			TempNew_u();
			 			uart_temp=' ';
			 			break;
			 		case 'v'://����
			 			TempNew_v();
			 			uart_temp=' ';
			 			break;
			 		case 'x'://ֹͣ����
			 			TempNew_x();
			 			uart_temp=' ';
			 			break;
			 		case 'z'://ֹͣ����
			 			TempNew_z();
			 			uart_temp=' ';
			 			break;
			 			//************************************************************
			 		case 'c'://�ؼ�ģʽ
			 			Home_Mode_Backhome();
			 			break;
			 		case 'e'://����ģʽ
			 			Home_Mode_Romatic();
			 			break;
			 		case 'f'://���ģʽ
			 			Home_Mode_Awayhome();
			 			break;


			 		default:break;
			 		}
	}

	
	//return 0;
}


#pragma vector=TIMER0_A0_VECTOR
__interrupt void Timer_A(void){

	dht11();

	ch[1]=((int)t)/10+'0';
	ch[2]=((int)t)%10+'0';
	ch[6]=((int)h)/10+'0';
	ch[7]=((int)h)%10+'0';

	for(i=0;i<9;i++){
			 UartPutchar(ch[i]);
		 }
}



void P13_Onclick(){
	 flag_bt_en++;
	 if(flag_bt_en==2)flag_bt_en=0;
	 if(flag_bt_en==1)
	 led0_on();//�� ������ʹ��
	 if(flag_bt_en==0)
     led0_off();//�� �ر�����ʹ��
 }

//��������MQ-2 ����ú��©��ʱ ����͵�ƽ
void P17_MQ2(){
	flag_mq2=1;
}
void P25_Ir(){
	flag_ir=1;
}
//���������� ����Ƿ����˴��
void P24_Sound(){
	flag_sound=1;
}

//�龰ģʽ
void Home_Mode_Backhome(){
	RGB_w();//����
	Window_m();//����
}//�ؼ�ģʽ�����ơ�����
void Home_Mode_Romatic(){
	while(1){
		for(i=0;i<5;i++){
			delay_ms(500);
			switch(i){
			case 0:
				RGB_r();
				break;
			case 1:
				RGB_o();
				break;
			case 2:
				RGB_y();
				break;
			case 3:
				RGB_g();
				break;
			case 4:
				RGB_b();
				break;
			case 5:
				RGB_p();
				break;
			}
		}
	}
}//����ģʽ��ѭ���ʵ��Զ�
void Home_Mode_Awayhome(){
	RGB_d();//�ص�
	Window_n();//�ش�
}//���ģʽ���صơ��ش�

