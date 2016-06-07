/*
 * HC05.c
 *
 *  Created on: 2015/11/04
 *      Author: Wei
 */
#include <msp430G2553.h>
#include "HC05.h"

/***********************************************
 �������ƣ�Init_HC06
 ��    �ܣ���HC06���г�ʼ�����ò����ʼ��շ����ڣ�ʹ�ܽ����ж�
 ��    ������
 ����ֵ  ����
 ***********************************************/
 void Init_HC05() {
    P1SEL = BIT1+BIT2;    //P1.1ΪRXD,P1.2ΪTXD
 	P1SEL2 = BIT1+BIT2;  //P1.1ΪRXD,P1.2ΪTXD
 	UCA0CTL1 |= UCSSEL_2; //ѡ��ʱ��BRCLK
 	UCA0BR0 = 106;  //1Mhz 9600
 	UCA0BR1 = 0;     //1Mhz 9600
 	UCA0MCTL = UCBRS2 + UCBRS0;  //������=BRCLK/(UBR+(M7+...0)/8)
 	UCA0CTL1 &=~ UCSWRST;   //��ʼ��˳��SWRST=1���ô��� Ȼ������SWRST=0 ���������Ӧ�ж�
 	IE2|=UCA0RXIE;    //ʹ�ܽ����ж�
 }

 /***********************************************
 �������ƣ�UartPutchar
 ��    �ܣ���һ���ַ�д�봮�ڷ��ͻ�����
 ��    �����ȴ����ڷ��͵�����
 ����ֵ  ����
 ***********************************************/
 void UartPutchar(unsigned char c)
 {
 	while(!(IFG2&UCA0TXIFG)); //(������Ϊ��)
 	UCA0TXBUF=c;
 	IFG2&=~UCA0RXIFG;
 }

 /***********************************************
 �������ƣ�UartGetchar
 ��    �ܣ��Ӵ��ڽ��ܻ�������ȡһ���ַ�
 ��    ������
 ����ֵ  ����ȡ�����ַ�
 ***********************************************/
// unsigned char UartGetchar()
// {
// 	unsigned char c;
// 	while(!(IFG2&UCA0RXIFG)); //(�ȴ��������)
// 	c=UCA0RXBUF;
// 	IFG2 &=~ UCA0TXIFG;
// 	UCA0RXBUF=0;
// 	return c;
// }

 /**
  * ���ڽ����ж����
  */
 #pragma vector=USCIAB0RX_VECTOR
  __interrupt void USART_RECEIVE(void){
 	 while(!(IFG2&UCA0RXIFG));
 	 uart_temp=UCA0RXBUF;
 	 flag_uart_temp=UCA0RXBUF;
 	 IFG2&=~UCA0RXIFG;
  }
