/*
 * tempnew.h
 *
 *  Created on: 2015/12/04
 *      Author: Wei
 */

#ifndef TEMPNEW_H_
#define TEMPNEW_H_

//协议
//加热 制冷
//加热         u up
//制冷         v
//停止加热 x
//停止制冷 z
//加热         v3 v2 v1 v0 v1 v0  1010
//制冷         v3 v2 v1 v0 v1 v1  1011
//停止加热 v3 v2 v1 v1 v0 v0  1100
//停止制冷 v3 v2 v1 v1 v0 v1  1101
void TempNew_u(void);
void TempNew_v(void);
void TempNew_x(void);
void TempNew_z(void);


#endif /* TEMPNEW_H_ */
