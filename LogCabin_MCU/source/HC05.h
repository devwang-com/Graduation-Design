/*
 * HC05.h
 *
 *  Created on: 2015/11/04
 *      Author: Wei
 */

#ifndef HC05_H_
#define HC05_H_

extern int uart_temp,flag_uart_temp;

void Init_HC05();
void UartPutchar(unsigned char c);
unsigned char UartGetchar();

#endif /* HC05_H_ */
