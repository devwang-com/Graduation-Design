/*
 * DHT11.c
 *
 *  Created on: 2015/11/09
 *      Author: Wei
 */


#include<MSP430G2553.h>
#include"DHT11.h"

uchar start_DHT11(void)
{
uchar TData_H_temp,TData_L_temp,RHData_H_temp,RHData_L_temp,checktemp;
uchar  presence,flag;
uint count;
DHT11_OUTPUT;
DHT11_L;    //����18ms����
delay_ms(20);
DHT11_H;
DHT11_INPUT;
delay_us(40);
presence=DHT11_IN;
if(!presence)
{
   count=2;
   while((!DHT11_IN)&&count++);//�ȴ��͵�ƽ
   count=2;
   while((DHT11_IN)&&count++);//�ȴ��ߵ�ƽ
      RHData_H_temp = DHT11_ReadChar();
      RHData_L_temp = DHT11_ReadChar();
      TData_H_temp = DHT11_ReadChar();
      TData_L_temp = DHT11_ReadChar();
      CheckData_temp = DHT11_ReadChar();
      DHT11_OUTPUT;
      DHT11_H;
      checktemp = (RHData_H_temp + RHData_L_temp + TData_H_temp + TData_L_temp);
      if (checktemp == CheckData_temp) {
         DHT11RH_Data_H = RHData_H_temp;
         DHT11RH_Data_L = RHData_L_temp;
         DHT11T_Data_H = TData_H_temp;
         DHT11T_Data_L = TData_L_temp;
         flag=1;
      }
   }
   P1OUT |=BIT0;
   return flag;
}
uchar DHT11_ReadChar(void)
{
  unsigned char dat;
   unsigned int count;     //������ֹ����
   unsigned char i;
   for(i=0;i<8;i++)
   {
      count=2;
      while((!DHT11_IN)&&count++);     //�ȴ�50us�͵�ƽ����
     delay_us(40); //40us
      dat <<= 1;        //50us�͵�ƽ+28us�ߵ�ƽ��ʾ'0'
      if(DHT11_IN)    //50us�͵�ƽ+70us�ߵ�ƽ��ʾ'1'
         dat |= 1;
      count=2;
      while((DHT11_IN)&&count++);
      if(count==1)      //��ʱ������forѭ��
         break;
   }
   return dat;
}
void dht11()
{
uchar o;
o=start_DHT11();
delay_ms(300);
if (o)
      {
        delay_ms(10);
        h=DHT11RH_Data_H;
        t=DHT11T_Data_H;
  //sprintf(c, "%d.%d %d.%d %d\r\n",DHT11RH_Data_H,DHT11RH_Data_L,DHT11T_Data_H,DHT11T_Data_L,CheckData_temp);
      }
}


