/*
 * TimerA.c
 *
 *  Created on: 2015/11/11
 *      Author: Wei
 */


#include<MSP430G2553.h>
#include"TimerA.h"

void TimerA_Init(){
	 TACTL = TASSEL_1 +TACLR+MC_1; // ���ö�ʱ��A���ƼĴ�����
	 TACCR0=65535;
	 TACCTL0|=CCIE;
}
