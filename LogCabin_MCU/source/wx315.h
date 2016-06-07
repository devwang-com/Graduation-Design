/*
 * wx315.h
 *
 *  Created on: 2015/11/07
 *      Author: Wei
 */

#ifndef WX315_H_
#define WX315_H_

#define WX315_SET P1OUT|=BIT4
#define WX315_CLR P1OUT&=~BIT4

extern unsigned int wx315_m;

void WX315_Init(void);

void v0(void);
void v1(void);
void v2(void);
void v3(void);

#endif /* WX315_H_ */
