/*
 * wdt.c
 *
 *  Created on: 2015/11/07
 *      Author: Wei
 */

#include<MSP430G2553.h>
#include"WDT.h"
#include"wx315.h"

void WDT_Init(){
	WDTCTL = WDT_MDLY_0_064;//0.064ms  64us
	IE1|=WDTIE;
}

#pragma vector=WDT_VECTOR
__interrupt void WDT_ISR(void){ //64us进一次中断
	cnt_128us++;
	if(cnt_128us==2){//64*2=128us
		wx315_m++;
		cnt_128us=0;
	}
}
