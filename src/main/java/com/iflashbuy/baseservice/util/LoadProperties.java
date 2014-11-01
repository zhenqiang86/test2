package com.iflashbuy.baseservice.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class LoadProperties {
    static final Logger LOGGER = LoggerFactory.getLogger(LoadProperties.class);
    
    public static Properties propt;
    static{
         propt = new Properties();
         try {
            propt.load(new InputStreamReader(LoadProperties.class.getClassLoader().getResourceAsStream("config/sysconfig.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 锟矫碉拷锟斤拷锟皆硷拷锟较ｏ拷锟斤拷锟斤拷之锟斤拷锟矫ｏ拷锟斤拷锟斤拷
     * @param proptName
     * @return
     */
    public static List<String> getProptList(String proptName,String splitCode){
        String[] value;
        String platform = propt.getProperty(proptName);
        if(platform!=null){
            if(splitCode==null){
                splitCode = ",";
            }
            value = platform.split(splitCode);
            return Arrays.asList(value);
        }
        return null;
    }

    /**
     * key为值锟斤拷value为锟斤拷锟�     * @return
     */
    public static Map<String,String> getFunctionMap(){
        String[] values;
        String[] value; 
        Map<String,String> map = new HashMap<String,String>();
        String functions = propt.getProperty("soft.function");
        if(functions!=null){
            values = functions.split(";");
            for(int i=0;i<values.length;i++){
                value = values[i].split(",");
                map.put(value[1],value[0]);
            }
        }
        return map;
    }
    /**
     * key为锟斤拷疲锟絭alue为值
     * @return
     */
    public static Map<String,String> getFunctionMap2(){
        String[] values;
        String[] value; 
        Map<String,String> map = new HashMap<String,String>();
        String functions = propt.getProperty("soft.function");
        if(functions!=null){
            values = functions.split(";");
            for(int i=0;i<values.length;i++){
                value = values[i].split(",");
                map.put(value[0],value[1]);
            }
        }
        return map;
    }
    /**
     * 锟矫碉拷锟斤拷锟皆碉拷锟街凤拷值
     * @param proptName
     * @return
     */
    public static String getPropt(String proptName){
        return propt.getProperty(proptName);
    }
    
    /**
     * 锟斤拷取Int锟斤拷锟酵碉拷值
     * @param key
     * @return if parse failed, return 0.
     */
    public static int getInt(String key) {
        String val = null;
        try {
            val = propt.getProperty(key);
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            LOGGER.error("Key[" + key + "] expected Int, but is " + val);
            return 0;
        }
    }
    
    //锟斤拷锟斤拷锟斤拷锟斤拷
    public static void reload(){
        propt = new Properties();
         try {
            propt.load(new InputStreamReader(LoadProperties.class.getClassLoader().getResourceAsStream("sys_constant.properties")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
