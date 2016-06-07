/*
 * DHT11.h
 *
 *  Created on: 2015/11/09
 *      Author: Wei
 */

#ifndef DHT11_H_
#define DHT11_H_

#define DHT11_OUTPUT   P1DIR|=BIT5
#define DHT11_INPUT   P1DIR&=~BIT5
#define DHT11_H   P1OUT|=BIT5
#define DHT11_L   P1OUT&=~BIT5
#define DHT11_IN   (P1IN&BIT5)
#define uchar unsigned char
#define uint unsigned int
#define ulong unsigned long
#define CPU          (1000000)
#define delay_us(x)   (__delay_cycles((double)x*CPU/1000000.0))
#define delay_ms(x)   (__delay_cycles((double)x*CPU/1000.0))

uchar start_DHT11(void);
uchar DHT11_ReadChar(void);
void dht11(void);
uchar DHT11T_Data_H, DHT11T_Data_L, DHT11RH_Data_H, DHT11RH_Data_L,CheckData_temp;
extern int t,h;

#endif /* DHT11_H_ */
