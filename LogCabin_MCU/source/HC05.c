/*
 * HC05.c
 *
 *  Created on: 2015/11/04
 *      Author: Wei
 */
#include <msp430G2553.h>
#include "HC05.h"

/***********************************************
 函数名称：Init_HC06
 功    能：对HC06进行初始化设置波特率及收发串口，使能接收中断
 参    数：无
 返回值  ：无
 ***********************************************/
 void Init_HC05() {
    P1SEL = BIT1+BIT2;    //P1.1为RXD,P1.2为TXD
 	P1SEL2 = BIT1+BIT2;  //P1.1为RXD,P1.2为TXD
 	UCA0CTL1 |= UCSSEL_2; //选择时钟BRCLK
 	UCA0BR0 = 106;  //1Mhz 9600
 	UCA0BR1 = 0;     //1Mhz 9600
 	UCA0MCTL = UCBRS2 + UCBRS0;  //波特率=BRCLK/(UBR+(M7+...0)/8)
 	UCA0CTL1 &=~ UCSWRST;   //初始化顺序：SWRST=1设置串口 然后设置SWRST=0 最后设置相应中断
 	IE2|=UCA0RXIE;    //使能接收中断
 }

 /***********************************************
 函数名称：UartPutchar
 功    能：将一个字符写入串口发送缓冲区
 参    数：等待串口发送的数据
 返回值  ：无
 ***********************************************/
 void UartPutchar(unsigned char c)
 {
 	while(!(IFG2&UCA0TXIFG)); //(待发送为空)
 	UCA0TXBUF=c;
 	IFG2&=~UCA0RXIFG;
 }

 /***********************************************
 函数名称：UartGetchar
 功    能：从串口接受缓冲区读取一个字符
 参    数：无
 返回值  ：读取到的字符
 ***********************************************/
// unsigned char UartGetchar()
// {
// 	unsigned char c;
// 	while(!(IFG2&UCA0RXIFG)); //(等待接收完成)
// 	c=UCA0RXBUF;
// 	IFG2 &=~ UCA0TXIFG;
// 	UCA0RXBUF=0;
// 	return c;
// }

 /**
  * 串口接收中断入口
  */
 #pragma vector=USCIAB0RX_VECTOR
  __interrupt void USART_RECEIVE(void){
 	 while(!(IFG2&UCA0RXIFG));
 	 uart_temp=UCA0RXBUF;
 	 flag_uart_temp=UCA0RXBUF;
 	 IFG2&=~UCA0RXIFG;
  }
