/*
 * wdt.h
 *
 *  Created on: 2015/11/07
 *      Author: Wei
 */

#ifndef WDT_H_
#define WDT_H_

extern unsigned int cnt_128us;

void WDT_Init(void);
void delay_315(unsigned int t);

#endif /* WDT_H_ */
