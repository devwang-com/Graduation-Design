/*
 * Key.c
 *
 *  Created on: 2015/11/05
 *      Author: Wei
 */

#include<MSP430G2553.h>
#include"Key.h"


void Key_Init(){
	P1REN|=BIT3;//����P1.3�ڲ�����������
	P1OUT|=BIT3;//����������Ϊ����
	P1DIR&=~BIT3;//P1.3��Ϊ���루��ʡ�ԣ�
	P1IES|=BIT3;//P1.3��Ϊ�½����ж�
	P1IE|=BIT3;//����P1.3�ж�
}

//��������MQ-2
void MQ2_Init(){
	P1REN|=BIT7;
	P1OUT|=BIT7;
	P1DIR&=~BIT7;
	P1IES|=BIT7;//��Ϊ�½����ж�
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
		 case BIT7:P17_MQ2();break;//��������MQ-2 ����ú��©��ʱ ����͵�ƽ
		 default:break;
		 }
	 }
 }

//P2.X
//���⴫���� ����Ƿ��п�������
void Ir_Init(void){
	P2REN|=BIT5;
	P2OUT|=BIT5;
	P2DIR&=~BIT5;
	P2IES&=~BIT5;//��Ϊ�������ж�   �������ڵ� ����͵�ƽ �������ڵ� ����ߵ�ƽ
	//P2IES|=BIT5;
	P2IE|=BIT5;
}

//���������� ����Ƿ����˴��
void Sound_Init(void){
	P2REN|=BIT4;
	P2OUT|=BIT4;
	P2DIR&=~BIT4;
	//P2IES&=~BIT4;//��Ϊ�������ж�   �������ڵ� ����͵�ƽ �������ڵ� ����ߵ�ƽ
	P2IES|=BIT4;
	P2IE|=BIT4;
}

void P2_IODect(void){
	 unsigned int Push_Key=0;
	 Push_Key=P2IFG&(~P2DIR);
	 __delay_cycles(10000);
	 if((P2IN&Push_Key)==0){
		 switch(Push_Key){
		 case BIT5:P25_Ir();break;//���⴫���� ����Ƿ��п�������  �������ڵ� ����ߵ�ƽ
		 case BIT4:P24_Sound();break;//���������� ����Ƿ����˴��
		 default:break;
		 }
	 }
}




#pragma vector=PORT2_VECTOR
 __interrupt void PORT2_ISR(void){
	 P2_IODect();
	 P2IFG=0;
}
