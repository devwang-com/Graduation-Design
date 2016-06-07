/*
 * key.h
 *
 *  Created on: 2015/11/05
 *      Author: Wei
 */

#ifndef KEY_H_
#define KEY_H_

void Key_Init(void);
void P1_IODect(void);
extern void P13_Onclick(void);

//烟雾传感器MQ-2
void MQ2_Init(void);
extern void P17_MQ2(void); //烟雾传感器MQ-2 当由煤气漏气时 输出低电平

//红外传感器 检测是否有客人来访
void Ir_Init(void);
void P2_IODect(void);
extern void P25_Ir(void);

//声音传感器 检测是否起了大风
void Sound_Init(void);
extern void P24_Sound(void);

#endif /* KEY_H_ */
