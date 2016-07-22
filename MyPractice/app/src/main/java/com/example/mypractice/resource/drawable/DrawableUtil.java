package com.example.mypractice.resource.drawable;

/*************************************************************************************
* Module Name: DrawableUtil</br>
* File Name: <b>DrawableUtil.java</b></br>
* Description: 工具类</br>
* Author: 郁金涛</br>
* 版权 2008-2015，金华长风信息技术有限公司</br>
* 所有版权保护
* 这是金华长风信息技术有限公司未公开的私有源代码, 本文件及相关内容未经金华长风信息技术有限公司
* 事先书面同意，不允许向任何第三方透露，泄密部分或全部; 也不允许任何形式的私自备份。
***************************************************************************************/
public class DrawableUtil {
	
	public static boolean hasState(int[] states, int state){
		if(states == null)
			return false;

        for (int state1 : states)
            if (state1 == state)
                return true;
		
		return false;
	}
}
