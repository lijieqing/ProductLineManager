package hua.lee.plm.base;

import hua.lee.plm.bean.CommandWrapper;
import hua.lee.plm.vo.CommandVO;

import java.util.HashMap;
import java.util.Map;

/**
 * context
 *
 * @author lijie
 * @create 2019-01-08 12:10
 **/
public final class PLMContext {
    public static Map<String, CommandVO> cmdMap = new HashMap<>();
    public static Map<String, CommandWrapper> cmdWrapper = new HashMap<>();
}
