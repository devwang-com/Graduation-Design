/*
 * tempnew.c
 *
 *  Created on: 2015/12/04
 *      Author: Wei
 */

#include<MSP430G2553.h>
#include"tempnew.h"
#include"wx315.h"
#define Num 8
//Э��
//���� ����
//����         u up
//����         v
//ֹͣ���� x
//ֹͣ���� z
//����         v3 v2 v1 v0 v1 v0  1010
//����         v3 v2 v1 v0 v1 v1  1011
//ֹͣ���� v3 v2 v1 v1 v0 v0  1100
//ֹͣ���� v3 v2 v1 v1 v0 v1  1101


void TempNew_u(){
	unsigned char i;
	for(i=0;i<Num;i++){
		v3();v2();v1();v0();v1();v0();wx315_m=0;while(wx315_m<40);
	}
}
void TempNew_v(){
	unsigned char i;
	for(i=0;i<Num;i++){
		v3();v2();v1();v0();v1();v1();wx315_m=0;while(wx315_m<40);
	}
}
void TempNew_x(){
	unsigned char i;
	for(i=0;i<Num;i++){
		v3();v2();v1();v1();v0();v0();wx315_m=0;while(wx315_m<40);
	}
}
void TempNew_z(){
	unsigned char i;
	for(i=0;i<Num;i++){
		v3();v2();v1();v1();v0();v1();wx315_m=0;while(wx315_m<40);
	}
}
