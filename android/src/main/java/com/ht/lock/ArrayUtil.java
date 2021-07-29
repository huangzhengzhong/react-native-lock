package com.ht.lock;

public class ArrayUtil {
    /**
     * 去空值
     */
    public static byte [] arrayCheckNull (byte[] bytes) {
        int index = 0;
        for(int i=0;i<bytes.length;i++){
            if(bytes[i] == 0){
                index = i;
                break;
            }
            index = i;
        }
        byte [] returnArray = new byte[index];

        System.arraycopy(bytes, 0, returnArray, 0, returnArray.length);
        return returnArray;
    }
}
