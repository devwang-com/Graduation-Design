/*
 * rgb.c
 *
 *  Created on: 2015/11/07
 *      Author: Wei
 */

#include<MSP430G2553.h>
#include"rgb.h"
#include"wx315.h"

#define Num 8

//��ɫ��ָ��Ԫɫ������ɫ�����졢�ơ����ͳȡ��̡���
//��red����yellow����blue�� ��orange����green����purple
//��white ��dieout
void RGB_r(){
	unsigned char i;
	for(i=0;i<Num;i++){
		v3();v2();v0();v0();v0();v0();wx315_m=0;while(wx315_m<40);
	}
}
void RGB_o(){
	unsigned char i;
	for(i=0;i<Num;i++){
		v3();v2();v0();v0();v0();v1();wx315_m=0;while(wx315_m<40);
	}
}
void RGB_y(){
	unsigned char i;
	for(i=0;i<Num;i++){
		v3();v2();v0();v0();v1();v0();wx315_m=0;while(wx315_m<40);
	}
}
void RGB_g(){
	unsigned char i;
	for(i=0;i<Num;i++){
		v3();v2();v0();v0();v1();v1();wx315_m=0;while(wx315_m<40);
	}
}
void RGB_b(){
	unsigned char i;
	for(i=0;i<Num;i++){
		v3();v2();v0();v1();v0();v0();wx315_m=0;while(wx315_m<40);
	}
}
void RGB_p(){
	unsigned char i;
	for(i=0;i<Num;i++){
		v3();v2();v0();v1();v0();v1();wx315_m=0;while(wx315_m<40);
	}
}
void RGB_w(){
	unsigned char i;
		for(i=0;i<Num;i++){
			v3();v2();v0();v1();v1();v0();wx315_m=0;while(wx315_m<40);
		}
}
void RGB_d(){
	unsigned char i;
		for(i=0;i<Num;i++){
			v3();v2();v0();v1();v1();v1();wx315_m=0;while(wx315_m<40);
		}
}
