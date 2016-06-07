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

unsigned int flag_mq2=0;//烟雾传感器MQ-2
unsigned int flag_ir=0;//红外传感器
unsigned int flag_sound=0;//声音传感器

extern void P13_Onclick(void);
extern void P17_MQ2(void); //烟雾传感器MQ-2 当由煤气漏气时 输出低电平
extern void P25_Ir(void);//红外传感器 检测是否有客人来访  红外有遮挡 输出高电平
extern void P24_Sound(void);//声音传感器 检测是否起了大风

//情景模式
void Home_Mode_Backhome(void);//回家模式：开灯、开窗
void Home_Mode_Romatic(void);//浪漫模式：循环彩灯自动
void Home_Mode_Awayhome(void);//离家模式：关灯、关窗

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

	led0_off();//默认开蓝牙使能
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
			UartPutchar('V');//Vapour 烟雾传感器 有漏气 向APP发送指令'V'
		}
		if(flag_ir==1){
			flag_ir=0;
			UartPutchar('I');//Ir 红外传感器 有漏气 向APP发送指令'I'
		}
		if(flag_sound==1){
			flag_sound=0;
			UartPutchar('S');//Sound 声音传感器 有大风 向APP发送指令'S'
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
			 		case 'd'://关灯 dieout
			 			RGB_d();
			 			uart_temp=' ';
			 			break;
			 			//***********************************************************
			 		case 'm'://开窗 m motor
			 			Window_m();
			 			uart_temp=' ';
			 			break;
			 		case 'n'://关窗
			 			Window_n();
			 			uart_temp=' ';
			 			break;
			 			//***********************************************************
			 		case 'u'://加热 u up
			 			TempNew_u();
			 			uart_temp=' ';
			 			break;
			 		case 'v'://制冷
			 			TempNew_v();
			 			uart_temp=' ';
			 			break;
			 		case 'x'://停止加热
			 			TempNew_x();
			 			uart_temp=' ';
			 			break;
			 		case 'z'://停止制冷
			 			TempNew_z();
			 			uart_temp=' ';
			 			break;
			 			//************************************************************
			 		case 'c'://回家模式
			 			Home_Mode_Backhome();
			 			break;
			 		case 'e'://浪漫模式
			 			Home_Mode_Romatic();
			 			break;
			 		case 'f'://离家模式
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
	 led0_on();//高 打开蓝牙使能
	 if(flag_bt_en==0)
     led0_off();//低 关闭蓝牙使能
 }

//烟雾传感器MQ-2 当由煤气漏气时 输出低电平
void P17_MQ2(){
	flag_mq2=1;
}
void P25_Ir(){
	flag_ir=1;
}
//声音传感器 检测是否起了大风
void P24_Sound(){
	flag_sound=1;
}

//情景模式
void Home_Mode_Backhome(){
	RGB_w();//开灯
	Window_m();//开窗
}//回家模式：开灯、开窗
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
}//浪漫模式：循环彩灯自动
void Home_Mode_Awayhome(){
	RGB_d();//关灯
	Window_n();//关窗
}//离家模式：关灯、关窗

