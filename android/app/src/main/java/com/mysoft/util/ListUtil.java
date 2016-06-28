
package com.mysoft.util;

import java.util.List;

public class ListUtil {
    private static final String TAG = "ListUtil";
    
    // 是否为空
    public static <T> boolean isEmpty(List<T> myList) {
        if (myList == null || myList.size() == 0) {
            return true;
        }
        return false;
    }
    
    // 是否越界
    public static <T> boolean isNotOutOfBounds(List<T> myList, int position) {
        if (myList != null && position > -1 && position < myList.size()) {
            return true;
        }
        return false;
    }
}
