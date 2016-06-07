/*
 * window.c
 *
 *  Created on: 2015/12/03
 *      Author: Wei
 */



#include<MSP430G2553.h>
#include"window.h"
#include"wx315.h"
#define Num 8
//开窗 关窗
//协议
//开窗：v3 v2 v1 v0 v0 v0
//关窗：v3 v2 v1 v0 v0 v0
//开窗：m  motor
//关窗：n

void Window_m(){
	unsigned char i;
	for(i=0;i<Num;i++){
		v3();v2();v1();v0();v0();v0();wx315_m=0;while(wx315_m<40);
	}
}
void Window_n(){
	unsigned char i;
	for(i=0;i<Num;i++){
		v3();v2();v1();v0();v0();v1();wx315_m=0;while(wx315_m<40);
	}
}
