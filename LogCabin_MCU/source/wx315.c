/*
 * wx315.c
 *
 *  Created on: 2015/11/07
 *      Author: Wei
 */


#include<MSP430G2553.h>
#include"wx315.h"

void WX315_Init(){
	P1DIR|=BIT4;P2DIR|=BIT4;
}

void v0(){
	wx315_m=0;
	while(wx315_m<10) WX315_SET;
	//{P1OUT|=BIT4;P2OUT|=BIT4;}
	wx315_m=0;
	while(wx315_m<5) WX315_CLR;
	//{P1OUT&=~BIT4;P2OUT&=~BIT4;}
}
void v1(){
	wx315_m=0;
	while(wx315_m<20) WX315_SET;//{P1OUT|=BIT4;P2OUT|=BIT4;}
	wx315_m=0;
	while(wx315_m<5)WX315_CLR;//{P1OUT&=~BIT4;P2OUT&=~BIT4;}
}
void v2(){
	wx315_m=0;
	while(wx315_m<40) WX315_SET;//{P1OUT|=BIT4;P2OUT|=BIT4;}
	wx315_m=0;
	while(wx315_m<40) WX315_CLR;//{P1OUT&=~BIT4;P2OUT&=~BIT4;}
}
void v3(){
	wx315_m=0;
	while(wx315_m<10) WX315_SET;//{P1OUT|=BIT4;P2OUT|=BIT4;}
	wx315_m=0;
	while(wx315_m<5) WX315_CLR;{P1OUT&=~BIT4;P2OUT&=~BIT4;}
}
