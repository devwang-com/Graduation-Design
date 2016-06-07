/*
 * Key.c
 *
 *  Created on: 2015/11/05
 *      Author: Wei
 */

#include<MSP430G2553.h>
#include"Key.h"


void Key_Init(){
	P1REN|=BIT3;//启用P1.3内部上下拉电阻
	P1OUT|=BIT3;//将电阻设置为上拉
	P1DIR&=~BIT3;//P1.3设为输入（可省略）
	P1IES|=BIT3;//P1.3设为下降沿中断
	P1IE|=BIT3;//允许P1.3中断
}

//烟雾传感器MQ-2
void MQ2_Init(){
	P1REN|=BIT7;
	P1OUT|=BIT7;
	P1DIR&=~BIT7;
	P1IES|=BIT7;//设为下降沿中断
	P1IE|=BIT7;
}
#pragma vector=PORT1_VECTOR
 __interrupt void PORT1_ISR(void){
	 P1_IODect();
	 P1IFG=0;
}

void P1_IODect(){
	 unsigned int Push_Key=0;
	 Push_Key=P1IFG&(~P1DIR);
	 __delay_cycles(10000);
	 if((P1IN&Push_Key)==0){
		 switch(Push_Key){
		 case BIT3:P13_Onclick();break;
		 case BIT7:P17_MQ2();break;//烟雾传感器MQ-2 当由煤气漏气时 输出低电平
		 default:break;
		 }
	 }
 }

//P2.X
//红外传感器 检测是否有客人来访
void Ir_Init(void){
	P2REN|=BIT5;
	P2OUT|=BIT5;
	P2DIR&=~BIT5;
	P2IES&=~BIT5;//设为上升沿中断   红外无遮当 输出低电平 红外有遮挡 输出高电平
	//P2IES|=BIT5;
	P2IE|=BIT5;
}

//声音传感器 检测是否起了大风
void Sound_Init(void){
	P2REN|=BIT4;
	P2OUT|=BIT4;
	P2DIR&=~BIT4;
	//P2IES&=~BIT4;//设为上升沿中断   红外无遮当 输出低电平 红外有遮挡 输出高电平
	P2IES|=BIT4;
	P2IE|=BIT4;
}

void P2_IODect(void){
	 unsigned int Push_Key=0;
	 Push_Key=P2IFG&(~P2DIR);
	 __delay_cycles(10000);
	 if((P2IN&Push_Key)==0){
		 switch(Push_Key){
		 case BIT5:P25_Ir();break;//红外传感器 检测是否有客人来访  红外有遮挡 输出高电平
		 case BIT4:P24_Sound();break;//声音传感器 检测是否起了大风
		 default:break;
		 }
	 }
}




#pragma vector=PORT2_VECTOR
 __interrupt void PORT2_ISR(void){
	 P2_IODect();
	 P2IFG=0;
}
