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

//��������MQ-2
void MQ2_Init(void);
extern void P17_MQ2(void); //��������MQ-2 ����ú��©��ʱ ����͵�ƽ

//���⴫���� ����Ƿ��п�������
void Ir_Init(void);
void P2_IODect(void);
extern void P25_Ir(void);

//���������� ����Ƿ����˴��
void Sound_Init(void);
extern void P24_Sound(void);

#endif /* KEY_H_ */
