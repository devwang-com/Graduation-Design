/*
 * led.c
 *
 *  Created on: 2015/11/04
 *      Author: Wei
 */

#include <msp430G2553.h>
#include "led.h"

 void led_init(){
	 P1DIR|=BIT0+BIT6;
	 P1OUT|=BIT6;
	 P1OUT&=~BIT0;
 }

 void led0_on(void){
	 P1OUT|=BIT0;
 }
 void led6_on(void){
	 P1OUT|=BIT6;
 }
 void led0_off(void){
	 P1OUT&=~BIT0;
 }
 void led6_off(void){
	 P1OUT&=~BIT6;
 }
 void led06_on(void){
	 P1OUT|=(BIT6+BIT0);
 }
 void led06_off(void){
	 P1OUT&=~(BIT6+BIT0);
 }
