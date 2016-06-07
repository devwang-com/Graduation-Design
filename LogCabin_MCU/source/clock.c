/*
 * clock.c
 *
 *  Created on: 2015/11/07
 *      Author: Wei
 */
#include<MSP430G2553.h>
#include"clock.h"

void Clock_Init(){
    DCOCTL=0;
    BCSCTL1=CALBC1_1MHZ;
    DCOCTL=CALDCO_1MHZ;
}

